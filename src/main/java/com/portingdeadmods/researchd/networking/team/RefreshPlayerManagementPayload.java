package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamSettingsScreen;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementDraggableWidget;
import com.portingdeadmods.researchd.client.screens.team.widgets.PlayerManagementList;
import com.portingdeadmods.researchd.client.screens.team.widgets.TeamMembersList;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record RefreshPlayerManagementPayload() implements CustomPacketPayload {
    public static final Type<RefreshPlayerManagementPayload> TYPE = new Type<>(Researchd.rl("refresh_player_management_payload"));
    public static final RefreshPlayerManagementPayload INSTANCE = new RefreshPlayerManagementPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, RefreshPlayerManagementPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void refreshPlayerManagementAction(RefreshPlayerManagementPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Screen currentScreen = mc.screen;
            if (currentScreen instanceof ResearchTeamScreen screen) {
                PlayerManagementDraggableWidget inviteWidget = screen.getInviteWidget();
                if (inviteWidget != null && !inviteWidget.getManagementList().getItems().isEmpty()) {
                    List<PlayerManagementList.Entry> entries = new ArrayList<>();
                    ClientResearchTeamHelper.getPlayersNotInTeam().forEach(member -> entries.add(new PlayerManagementList.Entry(member, inviteWidget.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
                    inviteWidget.getManagementList().refreshEntries(entries);
                }
                TeamMembersList teamMembersList = screen.getTeamMembersList();
                teamMembersList.getItems().clear();
                teamMembersList.getItems().addAll(ClientResearchTeamHelper.getTeamMembers());
                teamMembersList.resort();
            } else if (currentScreen instanceof ResearchTeamSettingsScreen screen) {
                PlayerManagementDraggableWidget playerManagementWindow = screen.getPlayerManagementWindow();
                if (playerManagementWindow != null && !playerManagementWindow.getManagementList().getItems().isEmpty()) {
                    List<PlayerManagementList.Entry> entries = new ArrayList<>();
                    ClientResearchTeamHelper.getTeamMembers().forEach(member -> entries.add(new PlayerManagementList.Entry(member, playerManagementWindow.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
                    playerManagementWindow.getManagementList().refreshEntries(entries);
                }
                PlayerManagementDraggableWidget transferOwnershipWindow = screen.getTransferOwnershipWindow();
                if (transferOwnershipWindow != null && !transferOwnershipWindow.getManagementList().getItems().isEmpty()) {
                    List<PlayerManagementList.Entry> entries = new ArrayList<>();
                    ClientResearchTeamHelper.getTeamMembers().forEach(member -> entries.add(new PlayerManagementList.Entry(member, transferOwnershipWindow.getManagementList().getItems().stream().findFirst().get().buttonSettings())));
                    transferOwnershipWindow.getManagementList().refreshEntries(entries);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });
    }
}
