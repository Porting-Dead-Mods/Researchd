package com.portingdeadmods.researchd.networking.team;

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

public record ManageMemberPayload(UUID member, boolean remove) implements CustomPacketPayload {
    public static final Type<ManageMemberPayload> TYPE = new Type<>(Researchd.rl("manage_member_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManageMemberPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ManageMemberPayload::member,
            ByteBufCodecs.BOOL,
            ManageMemberPayload::remove,
            ManageMemberPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void manageMemberAction(ManageMemberPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchTeamUtil.handleManageMember(context.player(), payload.member(), payload.remove());
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action Failed:  " + e.getMessage()));
            return null;
        });

    }
}