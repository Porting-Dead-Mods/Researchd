package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

// Will be merged with research manager eventually
public final class ResearchHelper {
    public static void initResearches(Player player) {
        Level level = player.level();
        RegistryAccess registryAccess = level.registryAccess();
        EntityResearch capability = player.getCapability(ResearchdCapabilities.ENTITY);
        Set<ResearchInstance> researches = capability.researches();
        Set<Holder<Research>> playerResearches = researches.stream()
                .map(instance -> registryAccess.holderOrThrow(instance.getResearch()))
                .collect(Collectors.toSet());
        Set<Holder<Research>> levelResearches = getLevelResearches(level);
        Researchd.LOGGER.debug("player: {}", playerResearches);
        Researchd.LOGGER.debug("level: {}", levelResearches);
        levelResearches.removeAll(playerResearches);
        for (Holder<Research> research : levelResearches) {
            capability.addResearch(new ResearchInstance(research.getKey(), EntryType.LOCKED));
        }
        Researchd.LOGGER.debug("player: {}", player.getCapability(ResearchdCapabilities.ENTITY).researches());
    }

    public static Set<ResearchInstance> getPlayerResearches(Player player) {
        return player.getCapability(ResearchdCapabilities.ENTITY).researches();
    }

    public static Set<Holder<Research>> getLevelResearches(Level level) {
        RegistryAccess registryAccess = level.registryAccess();
        HolderLookup.RegistryLookup<Research> registry = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_KEY);
        return registry.listElements().collect(Collectors.toSet());
    }

    public static Research getResearch(ResourceKey<Research> resourceKey, HolderLookup.Provider lookup) {
        return lookup.holderOrThrow(resourceKey).value();
    }

    public static @Nullable ResearchInstance getInstanceByResearch(Set<ResearchInstance> researches, ResourceKey<Research> key) {
        for (ResearchInstance instance : researches) {
            if (instance.getResearch().compareTo(key) == 0) {
                return instance;
            }
        }
        return null;
    }

}
