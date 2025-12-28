package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectList;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public record AndResearchEffect(List<ResearchEffect> effects) implements ResearchEffectList {
    private static final MapCodec<AndResearchEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResearchEffect.CODEC.listOf().fieldOf("effects").forGetter(AndResearchEffect::effects)
    ).apply(inst, AndResearchEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AndResearchEffect> STREAM_CODEC = StreamCodec.composite(
            ResearchEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AndResearchEffect::effects,
            AndResearchEffect::new
    );

    public static final ResearchEffectSerializer<AndResearchEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final ResourceLocation ID = Researchd.rl("and");

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        for (ResearchEffect effect : this.effects) {
            effect.onUnlock(level, player, research);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.AND.get();
    }

    @Override
    public ResearchEffectSerializer<AndResearchEffect> getSerializer() {
        return SERIALIZER;
    }

}
