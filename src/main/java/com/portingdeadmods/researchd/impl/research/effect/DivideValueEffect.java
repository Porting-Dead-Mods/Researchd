package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record DivideValueEffect(ValueEffect value, Float divisor) implements ResearchEffect {
    private static final MapCodec<DivideValueEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ValueEffect.CODEC.fieldOf("value").forGetter(DivideValueEffect::value),
            Codec.FLOAT.fieldOf("decrement").forGetter(DivideValueEffect::divisor)
    ).apply(inst, DivideValueEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, DivideValueEffect> STREAM_CODEC = StreamCodec.composite(
            ValueEffect.STREAM_CODEC,
            DivideValueEffect::value,
            ByteBufCodecs.FLOAT,
            DivideValueEffect::divisor,
            DivideValueEffect::new
    );
    
    public static final ResearchEffectSerializer<DivideValueEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final ResourceLocation ID = Researchd.rl("divide_value");

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        SimpleResearchTeam researchTeam = ResearchTeamHelper.getResearchTeam(player);
        float oldValue = researchTeam.getEffectValue(value);
        researchTeam.setEffectValue(value, oldValue / this.divisor());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectSerializer<DivideValueEffect> getSerializer() {
        return SERIALIZER;
    }
}


