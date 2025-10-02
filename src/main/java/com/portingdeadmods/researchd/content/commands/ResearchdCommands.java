package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class ResearchdCommands {
	private static final List<String> ALIASES = List.of("researchd", "rd", "research", "key");

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		List<LiteralCommandNode<CommandSourceStack>> rootCommands = ALIASES.stream().map(
				alias -> Commands.literal(alias).build()
		).toList();

		List<LiteralCommandNode<CommandSourceStack>> subCommands = List.of(
				TeamCommands.build(),
				DumpCommand.build(),
				HelpCommand.build(),
				HandCommand.build(),
				DataCommand.build()
		);

        for (LiteralCommandNode<CommandSourceStack> root : rootCommands) {
            subCommands.forEach(root::addChild);
            dispatcher.getRoot().addChild(root);
        }
    }
}
