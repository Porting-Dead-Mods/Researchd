package com.portingdeadmods.researchd.compat.kubejs.event;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.compat.kubejs.ResearchBuilder;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterResearchesKubeEvent implements KubeEvent {
    private final Map<ResourceLocation, Research> researches = new HashMap<>();
    private final List<ResearchBuilder> builders = new ArrayList<>();

    public ResearchBuilder create(String id) {
        ResourceLocation location = ResourceLocation.parse(id);
        ResearchBuilder builder = new ResearchBuilder(location);
        builder.sourceLine = SourceLine.UNKNOWN;
        builders.add(builder);
        return builder;
    }

    public Map<ResourceLocation, Research> getResearches() {
        for (ResearchBuilder builder : builders) {
            try {
                Research research = builder.createObject();
                researches.put(builder.id, research);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create research " + builder.id, e);
            }
        }
        return researches;
    }
}
