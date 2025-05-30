package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.ResearchTeamUtil;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetNamePayload(String name) implements CustomPacketPayload {
    public static final Type<SetNamePayload> TYPE = new Type<>(Researchd.rl("set_name_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetNamePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetNamePayload::name,
            SetNamePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void setNameAction(SetNamePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchTeamUtil.handleSetName(context.player(), payload.name);
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}