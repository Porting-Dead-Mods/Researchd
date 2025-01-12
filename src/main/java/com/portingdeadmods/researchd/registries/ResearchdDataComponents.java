package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchPack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ResearchdDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Researchd.MODID);

    // TODO: Sync component
    public static final Supplier<DataComponentType<ResearchPack>> RESEARCH_PACK = COMPONENTS.registerComponentType("research_pack", builder -> builder
            .persistent(ResearchPack.CODEC));
}
