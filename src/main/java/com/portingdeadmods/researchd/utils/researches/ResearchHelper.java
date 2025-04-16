package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.capabilties.ResearchdCapabilities;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.capabilities.EntityResearchImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public final class ResearchHelper {
    public static void initResearches(Player player) {
        Level level = player.level();
        RegistryAccess registryAccess = level.registryAccess();
        EntityResearchImpl savedData = ResearchdSavedData.PLAYER_RESEARCH.get().getData(level);
        Set<ResearchInstance> researches = savedData.researches();
        Set<Holder<Research>> playerResearches = researches.stream()
                .map(instance -> registryAccess.holderOrThrow(instance.getResearch()))
                .collect(Collectors.toSet());
        Set<Holder<Research>> levelResearches = getLevelResearches(level);
        levelResearches.removeAll(playerResearches);
        // TODO: Reenable this
        for (Holder<Research> research : levelResearches) {
            researches.add(new ResearchInstance(research.getKey(), ResearchStatus.LOCKED));
        }
        ResearchdSavedData.PLAYER_RESEARCH.get().setData(level, savedData);
    }

    public static Set<ResearchInstance> getPlayerResearches(Player player) {
        return ResearchdSavedData.PLAYER_RESEARCH.get().getData(player.level()).researches();
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
