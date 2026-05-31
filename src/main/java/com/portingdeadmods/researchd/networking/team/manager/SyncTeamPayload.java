package com.portingdeadmods.researchd.networking.team.manager;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncTeamPayload(ResearchTeamImpl team) implements CustomPacketPayload {
    public static final Type<SyncTeamPayload> TYPE = new Type<>(Researchd.rl("sync_team"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncTeamPayload> STREAM_CODEC = ResearchTeamImpl.STREAM_CODEC.map(SyncTeamPayload::new, SyncTeamPayload::team);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchTeamCache.researchTeamMap.updateTeam(team);
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle SyncTeamPayload", err);
            return null;
        });
    }

}
