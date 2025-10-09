package com.portingdeadmods.researchd.compat.kubejs;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.compat.kubejs.event.RegisterResearchPacksKubeEvent;
import com.portingdeadmods.researchd.compat.kubejs.event.RegisterResearchesKubeEvent;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchCompletedKubeEvent;
import com.portingdeadmods.researchd.compat.kubejs.event.ResearchProgressKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public final class ResearchdKJSEvents {
    public static final EventGroup GROUP = EventGroup.of("ResearchdEvents");

    public static final EventHandler REGISTER_RESEARCHES = GROUP.server("registerResearches", 
            () -> RegisterResearchesKubeEvent.class);
    
    public static final EventHandler REGISTER_RESEARCH_PACKS = GROUP.server("registerResearchPacks", 
            () -> RegisterResearchPacksKubeEvent.class);
    
    public static final EventHandler RESEARCH_COMPLETED = GROUP.server("researchCompleted", 
            () -> ResearchCompletedKubeEvent.class);
    
    public static final EventHandler RESEARCH_PROGRESS = GROUP.server("researchProgress", 
            () -> ResearchProgressKubeEvent.class);

    public static Map<ResourceLocation, Research> fireRegisterResearchesEvent() {
        RegisterResearchesKubeEvent event = new RegisterResearchesKubeEvent();
        ResearchdKJSEvents.REGISTER_RESEARCHES.post(ScriptType.SERVER, event);
        return event.getResearches();
    }

    public static Map<ResourceLocation, ResearchPack> fireRegisterResearchPacksEvent() {
        RegisterResearchPacksKubeEvent event = new RegisterResearchPacksKubeEvent();
        ResearchdKJSEvents.REGISTER_RESEARCH_PACKS.post(ScriptType.SERVER, event);
        return event.getResearchPacks();
    }

    public static void fireResearchCompleted(ServerPlayer player, ResourceKey<Research> research) {
        ResearchdKJSEvents.RESEARCH_COMPLETED.post(new ResearchCompletedKubeEvent(player, research));
    }

    public static void fireResearchProgress(ServerPlayer player, ResourceKey<Research> research, double progress) {
        ResearchdKJSEvents.RESEARCH_PROGRESS.post(new ResearchProgressKubeEvent(player, research, progress));
    }

}