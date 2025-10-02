package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ManageModeratorPayload(UUID moderator, boolean remove) implements CustomPacketPayload {
    public static final Type<ManageModeratorPayload> TYPE = new Type<>(Researchd.rl("manage_moderator_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManageModeratorPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ManageModeratorPayload::moderator,
            ByteBufCodecs.BOOL,
            ManageModeratorPayload::remove,
            ManageModeratorPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void manageModeratorAction(ManageModeratorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp)
                ResearchTeamHelper.handleManageModerator(sp, payload.moderator(), payload.remove());
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}