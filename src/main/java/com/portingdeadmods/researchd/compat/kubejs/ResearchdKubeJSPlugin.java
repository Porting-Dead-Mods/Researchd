package com.portingdeadmods.researchd.compat.kubejs;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchdEvents;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class ResearchdKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.of(ResearchdRegistries.RESEARCH_PACK_KEY, reg -> {
            reg.addDefault(ResearchPackBuilder.class, ResearchPackBuilder::new);
        });
        
        registry.of(ResearchdRegistries.RESEARCH_KEY, reg -> {
            reg.addDefault(ResearchBuilder.class, ResearchBuilder::new);
        });
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(ResearchdEvents.GROUP);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("ResearchEffectHelper", ResearchEffectHelper.class);
        bindings.add("ResearchMethodHelper", ResearchMethodHelper.class);
    }
}