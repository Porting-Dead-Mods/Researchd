package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchEffect;
import com.portingdeadmods.researchd.api.research.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import com.portingdeadmods.researchd.utils.UniqueArray;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class ResearchHelper {
    public static Set<Holder<Research>> getLevelResearches(Level level) {
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

    public static void refreshResearches(ServerPlayer serverPlayer) {
        MinecraftServer server = serverPlayer.server;
        ServerLevel level = server.overworld();
        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team = researchData.getTeam(serverPlayer);
        NeoForgeRegistries.ATTACHMENT_TYPES.entrySet().forEach(entry -> {
            Object data = serverPlayer.getData(entry.getValue());
            if (data instanceof ResearchEffectData effectData) {
                serverPlayer.setData((AttachmentType<ResearchEffectData>) entry.getValue(), effectData.getDefault(level));
                Researchd.LOGGER.info("Refreshing Research Data for effect type {} for player {}", data.getClass().getSimpleName(), serverPlayer.getScoreboardName());
            }
        });

        team.getResearchProgress().completedResearches().forEach(res -> {
            ResearchHelper.getResearch(res.getResearch(), level.registryAccess()).researchEffects().forEach(
                    eff -> eff.onUnlock(level, serverPlayer, res.getResearch())
            );

            Researchd.LOGGER.info("Reunlocking research {} for player {}", res.getResearch(), serverPlayer.getScoreboardName());
        });
    }
}
