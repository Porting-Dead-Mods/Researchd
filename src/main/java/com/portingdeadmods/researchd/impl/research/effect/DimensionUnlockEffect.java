package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record DimensionUnlockEffect(ResourceLocation dimension,
                                    ResourceLocation dimensionIconSprite) implements ResearchEffect {
    public static final ResourceLocation ID = Researchd.rl("dimension_unlock");
    public static final ResourceLocation DEFAULT_SPRITE = Researchd.rl("dimension_icons/default");
    public static final MapCodec<DimensionUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(DimensionUnlockEffect::dimension),
            ResourceLocation.CODEC.optionalFieldOf("dimension_icon_sprite", DEFAULT_SPRITE).forGetter(DimensionUnlockEffect::dimensionIconSprite)
    ).apply(instance, DimensionUnlockEffect::new));
    public static final ResearchEffectSerializer<DimensionUnlockEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, null);

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        DimensionUnlockEffectData data = player.getData(ResearchdAttachments.DIMENSION_PREDICATE.get());
        player.setData(ResearchdAttachments.DIMENSION_PREDICATE.get(), data.remove(this, level));
    }

    @Override
    public ResourceLocation id() {
        return null;
    }

    public ResourceKey<DimensionType> getDimension() {
        return ResourceKey.create(Registries.DIMENSION_TYPE, this.dimension());
    }

    @Override
    public ResearchEffectSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
