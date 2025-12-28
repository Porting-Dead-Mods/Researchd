package com.portingdeadmods.researchd.api.research.effects;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record ResearchEffectType(ResourceLocation id, ResearchIcon icon) {
    public Component getName() {
        return Utils.registryTranslation(ResearchdRegistries.RESEARCH_EFFECT_TYPE, this);
    }

    public static ResearchEffectType simple(ResourceLocation id, ResearchIcon icon) {
        return new ResearchEffectType(id, icon);
    }
}
