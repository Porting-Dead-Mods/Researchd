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
    public static final Type<LeaveTeamPayload> TYPE = new Type<>(Researchd.rl("leave_team_payload"));
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
            ResearchTeamUtil.handleLeaveTeam(context.player(), payload.nextToLead());
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}