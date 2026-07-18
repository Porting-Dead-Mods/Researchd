package com.portingdeadmods.researchd.content.commands.arguments;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.utils.SpaghettiClient;
import dev.ftb.mods.ftbteams.api.TeamManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ResearchdTeamArgument implements ArgumentType<ResearchdTeamArgumentProvider> {
    private static final DynamicCommandExceptionType TEAM_NOT_FOUND = new DynamicCommandExceptionType((object) -> Component.translatable("researchd.team_not_found", object));
    private static final ResearchdTeamArgument INSTANCE = new ResearchdTeamArgument();

    private ResearchdTeamArgument() {
    }

    public static ResearchTeam get(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, ResearchdTeamArgumentProvider.class).getTeam(context.getSource());
    }

    public static ResearchdTeamArgument teamArgument() {
        return INSTANCE;
    }

    @Override
    public ResearchdTeamArgumentProvider parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '@') {
            EntitySelector selector = (new EntitySelectorParser(reader, true)).parse();
            if (selector.includesEntities()) {
                throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
            } else {
                return new SelectorProvider(selector);
            }
        } else {
            boolean quotationMarks = false;
            int i = reader.getCursor();

            if (reader.canRead() && reader.peek() == '"') {
                quotationMarks = true;
                reader.skip();
            }

            while (reader.canRead()) {
                if ((quotationMarks && reader.peek() == '"') || (!quotationMarks && reader.peek() == ' ')) {
                    break;
                } else {
                    reader.skip();
                }
            }

            if (quotationMarks) {
                reader.skip();
                return new IDProvider(reader.getString().substring(i + 1, reader.getCursor() - 1));
            }

            return new IDProvider(reader.getString().substring(i, reader.getCursor()));
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {
            Stream<String> list = this.getTeams(context).map(ResearchTeam::getName).map(name -> "\"" + name + "\"").sorted();
            return SharedSuggestionProvider.suggest(list, builder);
        } else {
            return Suggestions.empty();
        }
    }

    private Stream<ResearchTeam> getTeams(CommandContext<?> context) {
        ResearchTeamManager teamManager;
        if (context.getSource() instanceof CommandSourceStack sourceStack) {
            teamManager = ResearchdApi.getTeamManager(sourceStack.getLevel());
        } else {
            teamManager = ResearchdApi.getTeamManager(SpaghettiClient.getClientLevel());
        }

        if (teamManager == null) return Stream.empty();

        return teamManager.getTeamIds().stream().map(teamManager::getTeamById).filter(Objects::nonNull);
    }

    private record SelectorProvider(EntitySelector selector) implements ResearchdTeamArgumentProvider {
        public ResearchTeam getTeam(CommandSourceStack source) throws CommandSyntaxException {
            ServerPlayer player = this.selector.findSinglePlayer(source);
            ResearchTeam team = ResearchdApi.getTeamManager(source.getLevel()).getTeamByPlayer(player);
            if (team == null) {
                throw TEAM_NOT_FOUND.create(player.getGameProfile().getName());
            }
            return team;
        }
    }

    private record IDProvider(String id) implements ResearchdTeamArgumentProvider {
        private CommandSyntaxException error() {
            return TEAM_NOT_FOUND.create(this.id);
        }

        public ResearchTeam getTeam(CommandSourceStack source) throws CommandSyntaxException {
            ResearchTeamManager teamManager = ResearchdApi.getTeamManager(source.getLevel());
            ResearchTeam team = teamManager.getTeamByName(this.id);
            if (team != null) {
                return team;
            } else {
                Optional<UUID> playerUUID = source.getServer().getProfileCache().get(this.id).map(GameProfile::getId);
                ResearchTeam playerTeam = playerUUID.map(teamManager::getTeamByPlayerId).orElseThrow(this::error);
                if (playerTeam == null) {
                    throw this.error();
                }
                return playerTeam;
            }
        }
    }
}
