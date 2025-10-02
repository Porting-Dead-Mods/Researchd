package com.portingdeadmods.researchd.content.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.portingdeadmods.portingdeadlibs.utils.SuggestionUtils;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.utils.PlayerUtils;
import com.portingdeadmods.researchd.utils.ResearchdSuggestionUtils;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class TeamCommands {
	public static LiteralCommandNode<CommandSourceStack> build() {
		return Commands.literal("team")
                .then(Commands.literal("help")
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            ResearchTeamHelper.sendHelpMessage(source::sendSystemMessage);
                            return 1;
                        }))
				.then(Commands.literal("members")
						.executes(context -> {
							CommandSourceStack source = context.getSource();
							ServerPlayer player = source.getPlayer();

							if (player != null) {
                                ResearchTeam team = ResearchTeamHelper.getTeamByMember(player);
								player.sendSystemMessage(ResearchTeamHelper.formatMembers(team, player.level()));
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
										ResearchTeamHelper.handleSendInviteToPlayer(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "player")), false);
									}

									return 1;
								})
						))
				.then(Commands.literal("leave")
						.executes(context -> {
							CommandSourceStack source = context.getSource();
							ServerPlayer player = source.getPlayer();

							if (player != null) {
								ResearchTeamHelper.handleLeaveTeam(player, PlayerUtils.EMPTY_UUID);
							}

							return 1;
						})
						.then(Commands.argument("nextToLead", StringArgumentType.string())
							.suggests(ResearchdSuggestionUtils::teamMemberNames)
							.executes(context -> {
								CommandSourceStack source = context.getSource();
								ServerPlayer player = source.getPlayer();

								if (player != null) {
									if (StringArgumentType.getString(context, "nextToLead").equals("none"))
										ResearchTeamHelper.handleLeaveTeam(player, PlayerUtils.EMPTY_UUID);
									else {
										ResearchTeamHelper.handleLeaveTeam(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "nextToLead")));
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
								ResearchTeamHelper.handleEnterTeam(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "memberOfTeam")));
							}

							return 1;
						})
					))
				.then(Commands.literal("ignore")
						.then(Commands.argument("memberOfTeam", StringArgumentType.string())
								.suggests(SuggestionUtils::playerNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();

									if (player != null) {
										ResearchTeamHelper.handleIgnoreTeam(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "memberOfTeam")));
									}

									return 1;
								})
						))
				.then(Commands.literal("promote")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests(ResearchdSuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();
									Player otherPlayer = PlayerUtils.getPlayerFromName(source.getLevel(), StringArgumentType.getString(context, "player"));
									ServerLevel level = source.getLevel();

									if (player != null) {
										ResearchTeamHelper.handleManageModerator(player, otherPlayer.getUUID(), false);
									}

									return 1;
								})
						))
				.then(Commands.literal("demote")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests(ResearchdSuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();
									Player otherPlayer = PlayerUtils.getPlayerFromName(source.getLevel(), StringArgumentType.getString(context, "player"));
									ServerLevel level = source.getLevel();

									if (player != null) {
										ResearchTeamHelper.handleManageModerator(player, otherPlayer.getUUID(), true);
									}

									return 1;
								})
						))
				.then(Commands.literal("kick")
						.then(Commands.argument("player", StringArgumentType.string())
								.suggests(ResearchdSuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();
									Player otherPlayer = PlayerUtils.getPlayerFromName(source.getLevel(), StringArgumentType.getString(context, "player"));
									ServerLevel level = source.getLevel();

									if (player != null) {
										ResearchTeamHelper.handleManageMember(player, otherPlayer.getUUID(), true);
									}

									return 1;
								})
						)
				)
				.then(Commands.literal("transfer-ownership")
						.then(Commands.argument("nextToLead", StringArgumentType.string())
								.suggests(ResearchdSuggestionUtils::teamMemberNames)
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									ServerPlayer player = source.getPlayer();

									if (player != null) {
										if (StringArgumentType.getString(context, "nextToLead").equals("none"))
											source.sendSystemMessage(ResearchTeamHelper.getIllegalMessage());
										else
											ResearchTeamHelper.handleTransferOwnership(player, PlayerUtils.getPlayerUUIDFromName(context.getSource().getLevel(), StringArgumentType.getString(context, "nextToLead")));
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
										ResearchTeamHelper.handleSetName(player, StringArgumentType.getString(context, "name"));
									}

									return 1;
								})
						)
				)
				.build();
	}


}
