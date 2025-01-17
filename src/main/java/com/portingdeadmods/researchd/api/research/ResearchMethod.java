package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchMethodSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

public interface ResearchMethod {
    Codec<ResearchMethod> CODEC =
            ResearchdRegistries.RESEARCH_METHOD_SERIALIZER.byNameCodec().dispatch("method", ResearchMethod::getSerializer, ResearchMethodSerializer::codec);

    boolean canResearch(Player player, ResourceKey<Research> research);

    void onResearchStart(Player player, ResourceKey<Research> research);

    ResearchMethodSerializer<?> getSerializer();
}
