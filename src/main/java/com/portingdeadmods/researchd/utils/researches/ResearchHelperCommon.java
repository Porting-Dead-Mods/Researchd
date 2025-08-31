package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.ResearchTeamMap;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class ResearchHelperCommon {
    public static Set<Holder<Research>> getLevelResearches(LevelAccessor level) {
        RegistryAccess registryAccess = level.registryAccess();
        HolderLookup.RegistryLookup<Research> registry = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_KEY);
        return registry.listElements().collect(Collectors.toSet());
    }

    public static <T extends ResearchEffect> Collection<T> getResearchEffects(Class<T> clazz, Level level) {
        Collection<T> effects = new UniqueArray<>();

        getLevelResearches(level).forEach(research -> {
            research.value().researchEffects().forEach(effect -> {
                if (effect.getClass().equals(clazz)) {
                    effects.add(clazz.cast(effect));
                };
            });
        });

        return effects;
    }

    public static List<ResearchInstance> getRecentResearches(ResearchTeam team) {
        Collection<ResearchInstance> researchInstances = team.getResearchProgress().researches().values();
        return researchInstances.stream()
                .filter(r -> r.getResearchStatus() == ResearchStatus.RESEARCHED)
                .sorted(Comparator.comparingLong(ResearchInstance::getResearchedTime))
                .toList();
    }

    public static Research getResearch(ResourceKey<Research> resourceKey, HolderLookup.Provider lookup) {
        return lookup.holderOrThrow(resourceKey).value();
    }

    public static @Nullable ResearchInstance getInstanceByResearch(Set<ResearchInstance> researches, ResourceKey<Research> key) {
        for (ResearchInstance instance : researches) {
            if (instance.is(key)) {
                return instance;
            }
        }
        return null;
    }

    public static List<ResearchEffectData<?>> getResearchEffectData(ServerPlayer serverPlayer) {
        MinecraftServer server = serverPlayer.server;
        ServerLevel level = server.overworld();
        List<ResearchEffectData<?>> effData = new UniqueArray<>();

        NeoForgeRegistries.ATTACHMENT_TYPES.entrySet().forEach(entry -> {
            Object data = serverPlayer.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                effData.add(effectData);
            }
        });

        return effData.stream().sorted(Comparator.comparing(a -> a.getClass().getName())).toList();
    }

    public static void refreshResearches(ServerPlayer player) {
        ServerLevel level;
        MinecraftServer server = player.server;
        level = server.overworld();

        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team = researchData.getTeamByMember(player.getUUID());
        for (Map.Entry<ResourceKey<AttachmentType<?>>, AttachmentType<?>> entry : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            Object data = player.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                player.setData((AttachmentType<ResearchEffectData<?>>) entry.getValue(), effectData.getDefault(level));
            }
        }

        for (ResearchInstance res : team.getResearchProgress().researches().values()) {
            if (res.getResearchStatus() == ResearchStatus.RESEARCHED) {
                res.lookup(level.registryAccess()).researchEffects().forEach(
                        eff -> eff.onUnlock(level, player, res.getKey())
                );
            }
        }
    }
}
