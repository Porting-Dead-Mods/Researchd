package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record IgnoreTeamPayload(UUID memberOfTeam) implements CustomPacketPayload {
    public static final Type<IgnoreTeamPayload> TYPE = new Type<>(Researchd.rl("ignore_team_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IgnoreTeamPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            IgnoreTeamPayload::memberOfTeam,
            IgnoreTeamPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void enterTeamAction(IgnoreTeamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp)
                ResearchTeamHelper.handleIgnoreTeam(sp, payload.memberOfTeam());
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}