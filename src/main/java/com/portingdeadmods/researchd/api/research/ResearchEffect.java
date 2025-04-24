package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.client.research.ClientResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * A Research Effect can be used to unlock content when researching
 */
public interface ResearchEffect {
    Codec<ResearchEffect> CODEC =
            ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER.byNameCodec().dispatch(ResearchEffect::getSerializer, ResearchEffectSerializer::codec);

    void onUnlock(Level level, Player player, ResourceKey<Research> research);

    ResourceLocation id();

    default Component getTranslation() {
        ResourceLocation id = id();
        return Component.translatable("research_method." + id.getNamespace() + "." + id.getPath());
    }

    ClientResearchEffect<?> getClientResearchEffect();

    ResearchEffectSerializer<?> getSerializer();

}
