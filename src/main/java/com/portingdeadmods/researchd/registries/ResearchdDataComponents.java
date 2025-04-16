package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public final class ResearchdDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Researchd.MODID);

    public static final Supplier<DataComponentType<ResearchPackComponent>> RESEARCH_PACK = COMPONENTS.registerComponentType("research_pack", builder -> builder
            .persistent(ResearchPackComponent.CODEC)
            .networkSynchronized(ResearchPackComponent.STREAM_CODEC));
}
