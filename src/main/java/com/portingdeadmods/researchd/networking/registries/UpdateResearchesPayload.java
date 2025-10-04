package com.portingdeadmods.researchd.networking.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.utils.researches.ResearchdManagers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public record UpdateResearchesPayload(HashMap<ResourceLocation, Research> researches) implements CustomPacketPayload {
    public static final StreamCodec<? super RegistryFriendlyByteBuf, UpdateResearchesPayload> STREAM_CODEC = ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, Research.STREAM_CODEC)
            .map(UpdateResearchesPayload::new, UpdateResearchesPayload::researches);
    public static final Type<UpdateResearchesPayload> TYPE = new Type<>(Researchd.rl("update_researches"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ResearchdManagers.getResearchesManager(context.player().level()).replaceContents(this.researches);
        }).exceptionally(err -> {
           Researchd.LOGGER.error("Encountered error while handling UpdateResearchesPayload", err);
           return null;
        });
    }

}
