package com.portingdeadmods.researchd.impl.research.effect;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public interface ValueEffectModifierEffect extends ResearchEffect {
    ValueEffect value();

    float amount();

    String operator();

    Component desc();

    default Component makeDescription(String type) {
        String valueEffect = Utils.registryTranslation(ResearchdRegistries.VALUE_EFFECT, this.value()).getString();
        return Component.literal(type + " value ")
                .append(Component.literal("'" + valueEffect + "'").withStyle(ChatFormatting.GOLD))
                .append(Component.literal(" by %.1f".formatted(this.amount())));
    }

}
