package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ValueEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record MultiplyValueEffect(ValueEffect value, Float multiplier) implements ResearchEffect {
    private static final MapCodec<MultiplyValueEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ValueEffect.CODEC.fieldOf("value").forGetter(MultiplyValueEffect::value),
            Codec.FLOAT.fieldOf("decrement").forGetter(MultiplyValueEffect::multiplier)
    ).apply(inst, MultiplyValueEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, MultiplyValueEffect> STREAM_CODEC = StreamCodec.composite(
            ValueEffect.STREAM_CODEC,
            MultiplyValueEffect::value,
            ByteBufCodecs.FLOAT,
            MultiplyValueEffect::multiplier,
            MultiplyValueEffect::new
    );
    
    public static final ResearchEffectSerializer<MultiplyValueEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final ResourceLocation ID = Researchd.rl("multiply_value");

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        ResearchTeamHelper.getResearchTeam(player).getMetadata().getTeamEffectList()
                .computeIfAbsent(value.getKey(), k -> 1f * multiplier());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectSerializer<MultiplyValueEffect> getSerializer() {
        return SERIALIZER;
    }
}


