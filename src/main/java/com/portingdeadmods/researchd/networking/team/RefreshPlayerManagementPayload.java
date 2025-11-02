package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RefreshPlayerManagementPayload() implements CustomPacketPayload {
    public static final Type<RefreshPlayerManagementPayload> TYPE = new Type<>(Researchd.rl("refresh_player_management_payload"));
    public static final RefreshPlayerManagementPayload INSTANCE = new RefreshPlayerManagementPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, RefreshPlayerManagementPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RefreshPlayerManagementPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientResearchTeamHelper.refreshResearchTeamScreenData();
        }).exceptionally(e -> {
            Researchd.LOGGER.error("Failed to handle RefreshPlayerManagementPayload", e);
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });
    }
}
