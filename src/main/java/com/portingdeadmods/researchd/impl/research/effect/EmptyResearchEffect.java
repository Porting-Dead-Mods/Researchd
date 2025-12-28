package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptyResearchEffect implements ResearchEffect {
    public static final EmptyResearchEffect INSTANCE = new EmptyResearchEffect();
    public static final ResourceLocation ID = Researchd.rl("empty");
    public static final ResearchEffectSerializer<EmptyResearchEffect> SERIALIZER = ResearchEffectSerializer.simple(Codec.unit(INSTANCE).fieldOf("instance"), null);

    private EmptyResearchEffect() {
    }

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.EMPTY.get();
    }

    @Override
    public ResearchEffectSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
