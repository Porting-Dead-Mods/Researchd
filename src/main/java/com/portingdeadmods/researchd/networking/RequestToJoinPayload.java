package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RequestToJoinPayload(UUID toJoin, boolean remove) implements CustomPacketPayload {
    public static final Type<RequestToJoinPayload> TYPE = new Type<>(Researchd.rl("request_to_join_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestToJoinPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            RequestToJoinPayload::toJoin,
            ByteBufCodecs.BOOL,
            RequestToJoinPayload::remove,
            RequestToJoinPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void requestToJoinAction(RequestToJoinPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchTeamUtil.handleRequestToJoin(context.player(), payload.toJoin, payload.remove);
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}