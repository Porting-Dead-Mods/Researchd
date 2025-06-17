package com.portingdeadmods.researchd.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;

public class HelpCommand {
    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("help")
                .then(Commands.argument("page", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("team"), builder))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ResearchTeamHelper.handleHelpMessage(source.getPlayer(), StringArgumentType.getString(context, "page"));
                            return 1;
                        })
                )
                .build();
    }
}
