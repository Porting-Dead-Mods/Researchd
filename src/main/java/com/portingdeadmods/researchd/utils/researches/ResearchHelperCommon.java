package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectList;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.SimpleResearchTeam;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
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

    @SuppressWarnings("unchecked")
    private static <T extends ResearchEffect> void _collectEffects(ResearchEffect effect, Collection<T> effects) {
        if (effect instanceof ResearchEffectList list) {
            for (ResearchEffect subEffect : list.effects()) {
                _collectEffects(subEffect,  effects);
            }
        } else {
            effects.add((T) effect);
        }
    }

    public static <T extends ResearchEffect> Collection<T> getResearchEffects(Class<T> clazz, Level level) {
        Collection<T> effects = new UniqueArray<>();

        for (Holder<Research> research : getLevelResearches(level)) {
            ResearchEffect effect = research.value().researchEffect();
            _collectEffects(effect, effects);
        }

        return new ArrayList<>(effects.stream().filter(clazz::isInstance).toList());
    }

    public static List<ResearchInstance> getRecentResearches(SimpleResearchTeam team) {
        Collection<ResearchInstance> researchInstances = team.getResearches().values();
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

        for (Map.Entry<ResourceKey<AttachmentType<?>>, AttachmentType<?>> entry : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            Object data = serverPlayer.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                effData.add(effectData);
            }
        }

        return effData.stream().sorted(Comparator.comparing(a -> a.getClass().getName())).toList();
    }

    public static List<ResourceKey<SimpleResearchPack>> getResearchPacks(HolderLookup.Provider lookup) {
        return lookup.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY).listElements()
                .sorted(Comparator.comparingInt(h -> h.value().sorting_value()))
                .map(Holder::getKey)
                .toList();
    }

    public static void refreshResearches(ServerPlayer player) {
        ServerLevel level;
        MinecraftServer server = player.server;
        level = server.overworld();

        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        SimpleResearchTeam team = researchData.getTeamByMember(player.getUUID());
        for (Map.Entry<ResourceKey<AttachmentType<?>>, AttachmentType<?>> entry : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            Object data = player.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                player.setData((AttachmentType<ResearchEffectData<?>>) entry.getValue(), effectData.getDefault(level));
                Researchd.debug("Effect Data", "Refreshing " + data.getClass().getSimpleName() + ": ");
                if (effectData.getDefault(level) instanceof DimensionUnlockEffectData(Set<ResourceKey<DimensionType>> blockedDimensions)) {
                    for (ResourceKey<DimensionType> dim : blockedDimensions) {
                        Researchd.debug("Effect Data", " - " + dim);
                    }
                }

                if (effectData.getDefault(level) instanceof RecipeUnlockEffectData(Set<RecipeHolder<?>> blockedRecipes)) {
                    for (RecipeHolder<?> rec : blockedRecipes) {
                        Researchd.debug("Effect Data", " - " + rec.id());

                    }
                }
            }
        }

        for (ResearchInstance res : team.getResearches().values()) {
            if (res.getResearchStatus() == ResearchStatus.RESEARCHED) {
                ResearchEffect effect = res.lookup(level.registryAccess()).researchEffect();
                effect.onUnlock(level, player, res.getKey());
            }
        }
    }

}
