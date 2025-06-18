package com.portingdeadmods.researchd.networking.team;

import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record EnterTeamPayload(UUID memberOfTeam) implements CustomPacketPayload {
    public static final Type<EnterTeamPayload> TYPE = new Type<>(Researchd.rl("enter_team_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnterTeamPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            EnterTeamPayload::memberOfTeam,
            EnterTeamPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void enterTeamAction(EnterTeamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp)
                ResearchTeamHelper.handleEnterTeam(sp, payload.memberOfTeam());
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}