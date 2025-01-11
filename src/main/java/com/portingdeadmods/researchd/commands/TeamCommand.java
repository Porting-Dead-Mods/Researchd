package com.portingdeadmods.researchd.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.ResearchTeamUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class TeamCommand implements CommandBase {

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return Commands.literal("team")
				.then(Commands.literal("create")
						.then(Commands.argument("name", StringArgumentType.string())
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();

									if (player != null) {
										ResearchTeamUtil.handleCreateTeam(player, StringArgumentType.getString(context, "name"));
									}

									return 1;
								})
						))
				.then(Commands.literal("members"))
						.executes(context -> {
							CommandSourceStack source = context.getSource();
							ServerPlayer player = source.getPlayer();

							if (player != null) {
								ResearchTeamUtil.handleListMembers(player);
							}

							return 1;
						})
				.then(Commands.literal("invite")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests((context, builder) -> SharedSuggestionProvider.suggest(context.getSource().getLevel().players().stream().map(player -> player.getName().getString()).toList(), builder))
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();

									if (player != null) {
										// TO CONTINUE ResearchTeamUtil.handleSendInviteToPlayer(player, context.getSource().getLevel().getPlayerByUUID().getString(context, "player"), false);
									}

									return 1;
								})
						))
				.build();

	}
}
