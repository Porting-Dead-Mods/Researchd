package com.portingdeadmods.researchd.compat.kubejs;

import com.portingdeadmods.researchd.compat.kubejs.helpers.ResearchEffectHelper;
import com.portingdeadmods.researchd.compat.kubejs.helpers.ResearchMethodHelper;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public final class ResearchdKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(ResearchdKJSEvents.GROUP);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("ResearchEffectHelper", ResearchEffectHelper.class);
        bindings.add("ResearchMethodHelper", ResearchMethodHelper.class);
    }

}