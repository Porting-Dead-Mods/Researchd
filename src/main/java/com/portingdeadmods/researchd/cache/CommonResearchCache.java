package com.portingdeadmods.researchd.cache;

import com.google.common.collect.ImmutableMap;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommonResearchCache {
    public static Map<ResourceKey<Research>, GlobalResearch> GLOBAL_RESEARCHES;
    public static @Nullable GlobalResearch ROOT_RESEARCH;

    public static void initialize(Level level) {
        reset();

        Map<ResourceKey<Research>, Research> researchLookup = ResearchHelperCommon.getLevelResearches(level);
        Map<ResourceKey<Research>, GlobalResearch> globalResearchMap = new HashMap<>(researchLookup.size());
        // Add the researchPacks to GLOBAL_RESEARCHES
        for (ResourceKey<Research> key : researchLookup.keySet()) {
            globalResearchMap.put(key, new GlobalResearch(key));
        }

        // CHILDREN
        for (GlobalResearch research : globalResearchMap.values()) {
            Research research1 = researchLookup.get(research.getResearchKey());
            List<ResourceKey<Research>> parents = research1.parents();
            for (ResourceKey<Research> parent : parents) {
                GlobalResearch parentGlobalResearch = globalResearchMap.get(parent);
                parentGlobalResearch.getChildren().add(research);
            }
        }

        // PARENTS
        for (GlobalResearch research : globalResearchMap.values()) {
            Research research1 = researchLookup.get(research.getResearchKey());
            List<ResourceKey<Research>> parents = research1.parents();

            if (parents.isEmpty()) {
                if (ROOT_RESEARCH == null) {
                    ROOT_RESEARCH = research;
                    continue;
                } else {
                    // Found multiple root instances
                    throw new IllegalStateException("Multiple research roots (Researches without parents), prev root research: %s, other root research: %s".formatted(ROOT_RESEARCH.getResearchKey().location(), research.getResearchKey().location()));
                }
            }

            for (ResourceKey<Research> parent : parents) {
                research.getParents().add(globalResearchMap.get(parent));
            }
        }

        Researchd.LOGGER.debug("Researches: {}", researchLookup);
        Researchd.LOGGER.debug("Research map: {}", globalResearchMap);

        // Lock global researchLookup
        for (GlobalResearch research : globalResearchMap.values()) {
            research.lock();
        }

        GLOBAL_RESEARCHES = ImmutableMap.copyOf(globalResearchMap);

    }

    private static void _collectChildren(GlobalResearch research, List<GlobalResearch> list) {
        for (GlobalResearch child : research.getChildren()) {
            list.add(child);
            if (!child.getChildren().isEmpty()) {
                _collectChildren(child, list);
            }
        }
    }

    public static List<GlobalResearch> allChildrenOf(ResourceKey<Research> key) {
        List<GlobalResearch> list = new UniqueArray<>();
        _collectChildren(GLOBAL_RESEARCHES.get(key), list);

        return list;
    }

    private static void _collectParents(GlobalResearch research, List<GlobalResearch> list) {
        for (GlobalResearch parent : research.getParents()) {
            list.add(parent);
            if (!parent.getParents().isEmpty()) {
                _collectChildren(parent, list);
            }
        }
    }

    public static List<GlobalResearch> allParentsOf(ResourceKey<Research> key) {
        List<GlobalResearch> list = new UniqueArray<>();
        _collectParents(GLOBAL_RESEARCHES.get(key), list);

        return list;
    }

    public static void reset() {
        if (GLOBAL_RESEARCHES != null) {
            ROOT_RESEARCH = null;
            GLOBAL_RESEARCHES = null;
        }
    }
}
