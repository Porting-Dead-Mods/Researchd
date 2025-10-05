package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResearchCacheReloadPayload() implements CustomPacketPayload {
    public static final ResearchCacheReloadPayload INSTANCE = new ResearchCacheReloadPayload();
    public static final Type<ResearchCacheReloadPayload> TYPE = new Type<>(Researchd.rl("research_cache_reload"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchCacheReloadPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            CommonResearchCache.initialize(level);

            ResearchHelperClient.initIconRenderers(level);
            ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
            if (data != null) {
                for (SimpleResearchTeam team : data.researchTeams().values()) {
                    ClientResearchTeamHelper.resolveInstances(team);
                }
            }
            ResearchGraphCache.clearCache();
            ClientResearchTeamHelper.refreshResearchScreenData();
        }).exceptionally(err -> {
           Researchd.LOGGER.error("Encountered error while handling ResearchCacheReloadPayload", err);
           return null;
        });
    }

}
