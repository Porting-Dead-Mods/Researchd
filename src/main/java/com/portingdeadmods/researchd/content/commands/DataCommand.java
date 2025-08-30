package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public class DataCommand {
	public static LiteralCommandNode<CommandSourceStack> build() {
		return Commands.literal("data")
				.executes(context -> {
					CommandSourceStack source = context.getSource();
					ServerPlayer player = source.getPlayer();

					if (player != null) {
						displayPlayerPredicates(player);
					}

					return 1;
				})
				.build();
	}

	private static void displayPlayerPredicates(ServerPlayer player) {
		MutableComponent header = Component.literal("------ RESEARCHD PLAYER DATA ------")
				.withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
		player.sendSystemMessage(header);

		MutableComponent playerInfo = Component.literal("Player: ")
				.withStyle(ChatFormatting.WHITE)
				.append(Component.literal(player.getName().getString())
						.withStyle(ChatFormatting.YELLOW));
		player.sendSystemMessage(playerInfo);

		for (ResearchEffectData<?> data : ResearchHelperCommon.getResearchEffectData(player)) {
			MutableComponent effect = Component.literal(data.getClass().getSimpleName())
					.withStyle(ChatFormatting.GREEN);

			for (Object entry : data.getAll()) {
				effect.append(Component.literal("\n  - " + entry.toString()).withStyle(ChatFormatting.WHITE));
			}

			player.sendSystemMessage(effect);
		}

		MutableComponent footer = Component.literal("----------- END OF DATA -----------")
				.withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
		player.sendSystemMessage(footer);
	}
}