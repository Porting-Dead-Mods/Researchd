package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record TransferOwnershipPayload(UUID nextToLead) implements CustomPacketPayload {
    public static final Type<TransferOwnershipPayload> TYPE = new Type<>(Researchd.rl("transfer_ownership_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TransferOwnershipPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            TransferOwnershipPayload::nextToLead,
            TransferOwnershipPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void transferOwnershipAction(TransferOwnershipPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchTeamUtil.handleTransferOwnership(context.player(), payload.nextToLead());
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}