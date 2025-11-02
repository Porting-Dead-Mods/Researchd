package com.portingdeadmods.researchd.networking.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.utils.researches.ResearchdManagers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public record UpdateResearchPacksPayload(HashMap<ResourceLocation, ResearchPack> researchPacks) implements CustomPacketPayload {
    public static final StreamCodec<? super RegistryFriendlyByteBuf, UpdateResearchPacksPayload> STREAM_CODEC = ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ResearchPackImpl.STREAM_CODEC)
            .map(UpdateResearchPacksPayload::new, UpdateResearchPacksPayload::researchPacks);
    public static final Type<UpdateResearchPacksPayload> TYPE = new Type<>(Researchd.rl("update_research_packs"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchdManagers.getResearchPacksManager(context.player().level()).replaceContents(this.researchPacks);
        }).exceptionally(err -> {
           Researchd.LOGGER.error("Failed to handle UpdateResearchPacksPayload", err);
           return null;
        });
    }

}
