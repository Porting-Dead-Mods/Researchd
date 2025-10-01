package com.portingdeadmods.researchd.api.team;

import com.portingdeadmods.researchd.api.ValueEffect;

import java.util.function.Supplier;

public interface ValueEffectsHolder {
    float getEffectValue(ValueEffect effect);

    default float getEffectValue(Supplier<ValueEffect> effect) {
        return this.getEffectValue(effect.get());
    }

    void setEffectValue(ValueEffect effect, float value);

    default void setEffectValue(Supplier<ValueEffect> effect, float value) {
        this.setEffectValue(effect.get(), value);
    }
}
