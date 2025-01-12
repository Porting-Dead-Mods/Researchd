package com.portingdeadmods.researchd.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import com.portingdeadmods.researchd.utils.SuggestionUtils;
import com.sun.jdi.connect.Connector;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class TeamCommands {
	public static LiteralCommandNode<CommandSourceStack> build() {
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
				.then(Commands.literal("members")
						.executes(context -> {
							CommandSourceStack source = context.getSource();
							ServerPlayer player = source.getPlayer();

							if (player != null) {
								ResearchTeamUtil.handleListMembers(player);
							}

							return 1;
						}))
				.then(Commands.literal("invite")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests(SuggestionUtils::playerNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();

									if (player != null) {
										ResearchTeamUtil.handleSendInviteToPlayer(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "player")), false);
									}

									return 1;
								})
						))
				.then(Commands.literal("leave")
						.then(Commands.argument("nextToLead", StringArgumentType.string())
							.suggests(SuggestionUtils::teamMemberNames)
							.executes(context -> {
								CommandSourceStack source = context.getSource();
								ServerPlayer player = source.getPlayer();

								if (player != null) {
									if (StringArgumentType.getString(context, "nextToLead").equals("none"))
										ResearchTeamUtil.handleLeaveTeam(player, PlayerUtils.EmptyUUID);
									else {
										ResearchTeamUtil.handleLeaveTeam(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "nextToLead")));
									}
								}

								return 1;
							})
						))
				.then(Commands.literal("join")
					.then(Commands.argument("memberOfTeam", StringArgumentType.string())
						.suggests(SuggestionUtils::playerNames)
						.executes(context -> {
							CommandSourceStack source = context.getSource();
							ServerPlayer player = source.getPlayer();

							if (player != null) {
								ResearchTeamUtil.handleEnterTeam(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "memberOfTeam")));
							}

							return 1;
						})
					))
				.then(Commands.literal("promote")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests(SuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();
									Player otherPlayer = PlayerUtils.getPlayerFromString(source.getLevel(), StringArgumentType.getString(context, "player"));
									ServerLevel level = source.getLevel();

									if (player != null) {
										ResearchTeamUtil.handleManageModerator(player, otherPlayer.getUUID(), false);
									}

									return 1;
								})
						))
				.then(Commands.literal("demote")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests(SuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();
									Player otherPlayer = PlayerUtils.getPlayerFromString(source.getLevel(), StringArgumentType.getString(context, "player"));
									ServerLevel level = source.getLevel();

									if (player != null) {
										ResearchTeamUtil.handleManageModerator(player, otherPlayer.getUUID(), true);
									}

									return 1;
								})
						))
				.then(Commands.literal("kick")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests(SuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();
									Player otherPlayer = PlayerUtils.getPlayerFromString(source.getLevel(), StringArgumentType.getString(context, "player"));
									ServerLevel level = source.getLevel();

									if (player != null) {
										ResearchTeamUtil.handleManageMember(player, otherPlayer.getUUID(), true);
									}

									return 1;
								})
						)
				)
				.then(Commands.literal("transfer-ownership")
						.then(Commands.argument("nextToLead", StringArgumentType.string())
								.suggests(SuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();

									if (player != null) {
										if (StringArgumentType.getString(context, "nextToLead").equals("none"))
											source.sendSystemMessage(ResearchTeamUtil.getIllegalMessage());
										else
											ResearchTeamUtil.handleTransferOwnership(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "nextToLead")));
									}

									return 1;
								})
						)
				)
				.then(Commands.literal("set-name")
						.then(Commands.argument("name", StringArgumentType.string())
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();

									if (player != null) {
										ResearchTeamUtil.handleSetName(player, StringArgumentType.getString(context, "name"));
									}

									return 1;
								})
						)
				)
				.build();
	}
}
