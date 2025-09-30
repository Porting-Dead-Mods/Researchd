package com.portingdeadmods.researchd.cache;

import com.google.common.collect.ImmutableMap;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.GlobalResearch;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CommonResearchCache {
    public static Map<ResourceKey<Research>, GlobalResearch> GLOBAL_RESEARCHES;
    public static GlobalResearch ROOT_RESEARCH;
    private static boolean LOCKED;

    public static void initialize(LevelAccessor level) {
        if (LOCKED) return;

        RegistryAccess registryAccess = level.registryAccess();
        Set<Holder<Research>> researches = ResearchHelperCommon.getLevelResearches(level);
        Map<ResourceKey<Research>, GlobalResearch> globalResearchMap = new HashMap<>(researches.size());
        // Add the researches to GLOBAL_RESEARCHES
        for (Holder<Research> research : researches) {
            globalResearchMap.put(research.getKey(), new GlobalResearch(research.getKey()));
        }

        // CHILDREN
        for (GlobalResearch research : globalResearchMap.values()) {
            Holder<Research> researchHolder = registryAccess.holderOrThrow(research.getResearchKey());
            List<ResourceKey<Research>> parents = researchHolder.value().parents();
            for (ResourceKey<Research> parent : parents) {
                GlobalResearch parentGlobalResearch = globalResearchMap.get(parent);
                parentGlobalResearch.getChildren().add(research);
            }
        }

        // PARENTS
        for (GlobalResearch research : globalResearchMap.values()) {
            Holder<Research> researchHolder = registryAccess.holderOrThrow(research.getResearchKey());
            List<ResourceKey<Research>> parents = researchHolder.value().parents();

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

        Researchd.LOGGER.debug("Researches: {}", researches);
        Researchd.LOGGER.debug("Research map: {}", globalResearchMap);

        // Lock global researches
        for (GlobalResearch research : globalResearchMap.values()) {
            research.lock();
        }

        if (ROOT_RESEARCH == null) {
            throw new IllegalStateException("Failed to find a root research (Research without parents)");
        }

        GLOBAL_RESEARCHES = ImmutableMap.copyOf(globalResearchMap);
        LOCKED = true;

        if (level.isClientSide()) {
            ResearchHelperClient.initIconRenderers(level);
        }

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
            LOCKED = false;
        }
    }
}
