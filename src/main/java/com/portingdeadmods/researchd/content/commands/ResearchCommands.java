package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.content.commands.arguments.ResearchdTeamArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.stream.Stream;

public final class ResearchCommands {
    public static LiteralCommandNode<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("research")
                .then(Commands.literal("unlock")
                        .then(Commands.argument("targets", ResearchdTeamArgument.teamArgument())
                                .then(Commands.literal("all")
                                        .executes(ResearchCommands::unlockAllResearches))
                                .then(Commands.argument("research-id", ResourceOrTagArgument.resourceOrTag(context, ResearchdRegistries.RESEARCH_KEY))
                                        .executes(ResearchCommands::unlockResearches))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", ResearchdTeamArgument.teamArgument())
                                .then(Commands.literal("all")
                                        .executes(ResearchCommands::removeAllResearches))
                                .then(Commands.argument("research-id", ResourceOrTagArgument.resourceOrTag(context, ResearchdRegistries.RESEARCH_KEY))
                                        .executes(ResearchCommands::removeResearch))))
                .build();
    }

    private static int unlockAllResearches(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        ResearchTeam team = ResearchdTeamArgument.get(context, "targets");

        Stream<ResourceKey<Research>> researches = context.getSource().registryAccess().lookupOrThrow(ResearchdRegistries.RESEARCH_KEY).listElementIds();

        researches.forEach(r -> {
            team.setResearchCompleted(r, level.getDayTime() * 50);
            team.onCompleteResearch(r, level.getDayTime() * 50, id -> id.equals(source.getPlayer().getUUID()) ? source.getPlayer() : null);
        });

        return 0;
    }

    private static int unlockResearches(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        ResearchTeam team = ResearchdTeamArgument.get(context, "targets");

        ResourceOrTagArgument.Result<Research> result = ResourceOrTagArgument.getResourceOrTag(context, "research-id", ResearchdRegistries.RESEARCH_KEY);
        List<? extends Holder<Research>> researches = result.unwrap().map(List::of, o -> o.stream().toList());

        for (Holder<Research> research : researches) {
            team.setResearchCompleted(research.getKey(), level.getDayTime() * 50);
            team.onCompleteResearch(research.getKey(), level.getDayTime() * 50, true, id -> id.equals(source.getPlayer().getUUID()) ? source.getPlayer() : null);
        }

        return 0;
    }

    private static int removeAllResearches(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    public static int removeResearch(CommandContext<CommandSourceStack> context) {
        return 0;
    }

}
