package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record LeaveTeamPayload(UUID nextToLead) implements CustomPacketPayload {
    public static final Type<LeaveTeamPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Researchd.MODID, "leave_team_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LeaveTeamPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            LeaveTeamPayload::nextToLead,
            LeaveTeamPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void leaveTeamAction(LeaveTeamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            UUID senderId = sender.getUUID();

            ResearchdSavedData savedData = ResearchdSavedData.get(level);

            // Handle the case of transfering ownership
            if (ResearchTeamUtil.isResearchTeamLeader(sender)) {
                PacketDistributor.sendToServer(new TransferOwnershipPayload(payload.nextToLead()));
                savedData.setDirty();
                return;
            }

            if (ResearchTeamUtil.getPermissionLevel(sender) == 1) {
                ResearchTeamUtil.removeModFromTeam(sender);
            }

        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}