package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.content.commands.arguments.ResearchdTeamArgument;
import com.portingdeadmods.researchd.utils.registries.ResearchdManagers;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

/**
 * Completes and removes researches for a team. Researches are enumerated and resolved through the researches manager,
 * not the vanilla registry: KubeJS researches only exist in the manager.
 */
public final class ResearchCommands {
    private static final DynamicCommandExceptionType UNKNOWN_RESEARCH =
            new DynamicCommandExceptionType(id -> Component.literal("Unknown research: " + id));

    public static LiteralCommandNode<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("research")
                .then(Commands.literal("unlock")
                        .then(Commands.argument("targets", ResearchdTeamArgument.teamArgument())
                                .then(Commands.literal("all").executes(ctx -> unlock(ctx, allResearches(ctx))))
                                .then(researchArgument().executes(ctx -> unlock(ctx, List.of(research(ctx)))))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", ResearchdTeamArgument.teamArgument())
                                .then(Commands.literal("all").executes(ctx -> remove(ctx, allResearches(ctx))))
                                .then(researchArgument().executes(ctx -> remove(ctx, List.of(research(ctx)))))))
                .build();
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Identifier> researchArgument() {
        return Commands.argument("research-id", IdentifierArgument.id())
                .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(
                        allResearches(ctx).stream().map(ResourceKey::identifier), builder));
    }

    private static Collection<ResourceKey<Research>> allResearches(CommandContext<CommandSourceStack> ctx) {
        return ResearchdManagers.getResearchesManager(ctx.getSource().getLevel())
                .getLookup()
                .keySet();
    }

    private static ResourceKey<Research> research(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        Identifier id = IdentifierArgument.getId(ctx, "research-id");
        ResourceKey<Research> key = ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, id);
        if (!allResearches(ctx).contains(key)) {
            throw UNKNOWN_RESEARCH.create(id);
        }
        return key;
    }

    /** Online players by UUID, whoever runs the command: a console source has no player. */
    private static Function<UUID, Player> onlinePlayers(CommandSourceStack source) {
        return source.getServer().getPlayerList()::getPlayer;
    }

    private static int unlock(CommandContext<CommandSourceStack> ctx, Collection<ResourceKey<Research>> researches)
            throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ResearchTeam team = ResearchdTeamArgument.get(ctx, "targets");
        long completionTime = source.getLevel().getOverworldClockTime() * 50;

        int completed = 0;
        for (ResourceKey<Research> research : researches) {
            if (team.getResearches().get(research) == null
                    || team.getResearches().get(research).isResearched()) continue;
            team.setResearchCompleted(research, completionTime);
            team.onCompleteResearch(research, completionTime, true, onlinePlayers(source));
            completed++;
        }

        int count = completed;
        source.sendSuccess(
                () -> Component.literal("Completed %d research(es) for team %s".formatted(count, team.getName())),
                true);
        return count;
    }

    private static int remove(CommandContext<CommandSourceStack> ctx, Collection<ResourceKey<Research>> researches)
            throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ResearchTeam team = ResearchdTeamArgument.get(ctx, "targets");

        int removed = 0;
        for (ResourceKey<Research> research : researches) {
            if (team.getResearches().get(research) == null
                    || !team.getResearches().get(research).isResearched()) continue;
            team.onRemoveResearch(research, onlinePlayers(source));
            removed++;
        }

        int count = removed;
        source.sendSuccess(
                () -> Component.literal("Removed %d research(es) from team %s".formatted(count, team.getName())), true);
        return count;
    }
}
