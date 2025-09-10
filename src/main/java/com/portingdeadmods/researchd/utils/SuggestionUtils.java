package com.portingdeadmods.researchd.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SuggestionUtils {
    public static CompletableFuture<Suggestions> playerNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(context.getSource().getLevel().players().stream().map(player -> player.getName().getString()).toList(), builder);
    }

    public static CompletableFuture<Suggestions> teamMemberNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        List<String> members = ResearchTeamHelper.getTeamMemberNames(context.getSource().getLevel(), context.getSource().getPlayer());
        members.add("none");
        return SharedSuggestionProvider.suggest(members, builder);
    }
}
