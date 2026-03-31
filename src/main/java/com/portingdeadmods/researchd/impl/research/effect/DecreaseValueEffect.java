package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ValueEffectsHolder;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record DecreaseValueEffect(ValueEffect value, float amount) implements ValueEffectModifierEffect {
    private static final MapCodec<DecreaseValueEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ValueEffect.CODEC.fieldOf("value").forGetter(DecreaseValueEffect::value),
            Codec.FLOAT.fieldOf("amount").forGetter(DecreaseValueEffect::amount)
    ).apply(inst, DecreaseValueEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, DecreaseValueEffect> STREAM_CODEC = StreamCodec.composite(
            ValueEffect.STREAM_CODEC,
            DecreaseValueEffect::value,
            ByteBufCodecs.FLOAT,
            DecreaseValueEffect::amount,
            DecreaseValueEffect::new
    );

    public static final ResearchEffectSerializer<DecreaseValueEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final ResourceLocation ID = Researchd.rl("decrease_value");

    @Override
    public String operator() {
        return "-";
    }

    @Override
    public Component desc() {
        return makeDescription("Decrease");
    }

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        ResearchTeam researchTeam = ResearchTeamHelper.getTeamByMember(player);
        if (researchTeam instanceof ValueEffectsHolder effectsHolder) {
            float oldValue = effectsHolder.getEffectValue(value);
            effectsHolder.setEffectValue(value, oldValue - this.amount());
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.DECREASE_VALUE.get();
    }

    @Override
    public ResearchEffectSerializer<DecreaseValueEffect> getSerializer() {
        return SERIALIZER;
    }
}

