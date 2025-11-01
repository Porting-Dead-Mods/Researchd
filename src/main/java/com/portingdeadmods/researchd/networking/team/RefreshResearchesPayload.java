package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RefreshResearchesPayload() implements CustomPacketPayload {
    public static final Type<RefreshResearchesPayload> TYPE = new Type<>(Researchd.rl("refresh_researches_payload"));
    public static final RefreshResearchesPayload INSTANCE = new RefreshResearchesPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, RefreshResearchesPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RefreshResearchesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchHelperClient.refreshResearches(context.player());
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}