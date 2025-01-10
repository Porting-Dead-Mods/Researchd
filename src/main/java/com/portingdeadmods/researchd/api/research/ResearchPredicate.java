package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ResearchPredicate {
    Codec<ResearchPredicate> CODEC =
            ResearchdRegistries.RESEARCH_PREDICATE_SERIALIZER.byNameCodec().dispatch(ResearchPredicate::getSerializer, ResearchPredicateSerializer::codec);

    void onUnlock(Level level, Player player, ResourceKey<Research> research);

    ResearchPredicateSerializer<?> getSerializer();
}
