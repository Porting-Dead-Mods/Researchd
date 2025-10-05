package com.portingdeadmods.researchd.compat.kubejs.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class ResearchdEvents {
    public static final EventGroup GROUP = EventGroup.of("ResearchdEvents");

    public static final EventHandler REGISTER_RESEARCHES = GROUP.server("registerResearches", 
            () -> RegisterResearchesKubeEvent.class);
    
    public static final EventHandler REGISTER_RESEARCH_PACKS = GROUP.server("registerResearchPacks", 
            () -> RegisterResearchPacksKubeEvent.class);
    
    public static final EventHandler RESEARCH_COMPLETED = GROUP.server("researchCompleted", 
            () -> ResearchCompletedKubeEvent.class);
    
    public static final EventHandler RESEARCH_PROGRESS = GROUP.server("researchProgress", 
            () -> ResearchProgressKubeEvent.class);
}