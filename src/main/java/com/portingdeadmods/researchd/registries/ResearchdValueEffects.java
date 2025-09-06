package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ValueEffect;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ResearchdValueEffects {
    public static final DeferredRegister<ValueEffect> VALUE_EFFECTS = DeferredRegister.create(ResearchdRegistries.VALUE_EFFECT, Researchd.MODID);

    public static final Supplier<ValueEffect> RESEARCH_LAB_PRODUCTIVITY = register("research_lab_productivity");

    public static Supplier<ValueEffect> register(String name) {
        Supplier<ValueEffect> effect = () -> new ValueEffect(ResourceLocation.fromNamespaceAndPath(VALUE_EFFECTS.getNamespace(), name));
        VALUE_EFFECTS.register(name, effect);
        return effect;
    }
}
