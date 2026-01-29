package com.portingdeadmods.researchd.api.research.effects;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record ResearchEffectType(ResourceLocation id, ResearchIcon icon, boolean parentType) {
    public Component getName() {
        return Utils.registryTranslation(ResearchdRegistries.RESEARCH_EFFECT_TYPE, this);
    }

    public static ResearchEffectType single(ResourceLocation id, ResearchIcon icon) {
        return new ResearchEffectType(id, icon, false);
    }

    public static ResearchEffectType multiple(ResourceLocation id, ResearchIcon icon) {
        return new ResearchEffectType(id, icon, true);
    }

}
