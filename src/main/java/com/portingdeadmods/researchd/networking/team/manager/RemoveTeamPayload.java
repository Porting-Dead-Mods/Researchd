package com.portingdeadmods.researchd.networking.team.manager;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RemoveTeamPayload(UUID teamId) implements CustomPacketPayload {
    public static final Type<RemoveTeamPayload> TYPE = new Type<>(Researchd.rl("remove_team"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RemoveTeamPayload> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(RemoveTeamPayload::new, RemoveTeamPayload::teamId);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchTeamCache.researchTeamMap.removeTeam(teamId);
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle RemoveTeamPayload", err);
            return null;
        });
    }

}
