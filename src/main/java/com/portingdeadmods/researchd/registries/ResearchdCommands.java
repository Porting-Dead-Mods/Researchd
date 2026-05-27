package com.portingdeadmods.researchd.registries;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.content.commands.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public final class ResearchdCommands {
	private static final List<String> ALIASES = List.of("researchd", "rd");

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		List<LiteralCommandNode<CommandSourceStack>> rootCommands = ALIASES.stream().map(
				alias -> Commands.literal(alias).requires(source -> source.hasPermission(2)).build()
		).toList();

		List<LiteralCommandNode<CommandSourceStack>> subCommands = List.of(
				ResearchCommands.build(context),
				DebugCommands.build(),
				DevCommands.build(context),
                TeamCommands.build(),
                ExampleCommands.build()
		);

        for (LiteralCommandNode<CommandSourceStack> root : rootCommands) {
            subCommands.forEach(root::addChild);
            dispatcher.getRoot().addChild(root);
        }
    }

}
