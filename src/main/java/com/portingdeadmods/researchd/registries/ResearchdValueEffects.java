package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.impl.SimpleValueEffect;
import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchdValueEffects {
    public static final DeferredRegister<ValueEffect> VALUE_EFFECTS =
            DeferredRegister.create(ResearchdRegistries.VALUE_EFFECT, Researchd.MODID);

    // TODO: Implement this
    public static final Supplier<ValueEffect> RESEARCH_LAB_PRODUCTIVITY = register("research_lab_productivity");

    public static Supplier<ValueEffect> register(String name) {
        return VALUE_EFFECTS.register(name, SimpleValueEffect::new);
    }
}
