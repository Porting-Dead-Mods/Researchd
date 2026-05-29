package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.*;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectList;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.impl.research.cache.CachedResearchRelations;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.UnlockItemEffectData;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.utils.TimeDifference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public final class ResearchHelperCommon {
    public static <T extends ResearchEffect> Collection<T> getResearchEffects(Class<T> clazz, Level level) {
        ResearchManager researchManager = ResearchdApi.getResearchManager();

        Collection<T> effects = new UniqueArray<>();

        for (ResourceKey<Research> research : researchManager.getResearches()) {
            ResearchEffect effect = researchManager.lookupResearch(research, level).researchEffect();
            _collectEffects(clazz, effect, effects);
        }

        return new ArrayList<>(effects.stream().filter(clazz::isInstance).toList());
    }

    public static List<ResourceKey<Research>> getAllChildrenForResearch(ResourceKey<Research> key, ResearchManager manager) {
        List<ResourceKey<Research>> list = new UniqueArray<>();
        _collectChildren(manager.getRelationsForResearch(key), list);

        return list;
    }

    public static List<ResourceKey<Research>> getAllParentsForResearch(ResourceKey<Research> key, ResearchManager manager) {
        List<ResourceKey<Research>> list = new UniqueArray<>();
        _collectParents(manager.getRelationsForResearch(key), list);

        return list;
    }

    public static List<ResearchInstance> getRecentResearches(ResearchTeam team) {
        Collection<ResearchInstance> researchInstances = team.getResearches().values();
        return researchInstances.stream()
                .filter(r -> r.getResearchStatus() == ResearchStatus.RESEARCHED)
                .sorted(Comparator.comparingLong(ResearchInstance::getResearchedTime))
                .toList();
    }

    // FIXME: This is pretty inefficient cuz we iterate through all Attachment types
    public static List<ResearchEffectData<?>> getResearchEffectData(ServerPlayer serverPlayer) {
        List<ResearchEffectData<?>> effData = new UniqueArray<>();

        for (Map.Entry<ResourceKey<AttachmentType<?>>, AttachmentType<?>> entry : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            Object data = serverPlayer.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                effData.add(effectData);
            }
        }

        return effData.stream().sorted(Comparator.comparing(a -> a.getClass().getName())).toList();
    }

    private static <T extends ResearchEffect> void _collectEffects(Class<T> clazz, ResearchEffect effect, Collection<T> effects) {
        if (effect instanceof ResearchEffectList list) {
            for (ResearchEffect subEffect : list.effects()) {
                _collectEffects(clazz, subEffect,  effects);
            }
        } else {
            if (clazz.isInstance(effect)) {
                effects.add(clazz.cast(effect));
            }
        }
    }

    private static void _collectChildren(CachedResearchRelations research, List<ResourceKey<Research>> list) {
        for (CachedResearchRelations child : research.getChildren()) {
            list.add(child.getResearchKey());
            if (!child.getChildren().isEmpty()) {
                _collectChildren(child, list);
            }
        }
    }

    private static void _collectParents(CachedResearchRelations research, List<ResourceKey<Research>> list) {
        for (CachedResearchRelations parent : research.getParents()) {
            list.add(parent.getResearchKey());
            if (!parent.getParents().isEmpty()) {
                _collectChildren(parent, list);
            }
        }
    }

    @Deprecated
    public static Map<ResourceKey<ResearchPack>, ResearchPack> getResearchPacks(Level level) {
        return ResearchdManagers.getResearchPacksManager(level).getLookup();
    }

    @Deprecated
    public static List<ResourceKey<ResearchPack>> getResearchPackKeys(Level level) {
        Map<ResourceKey<ResearchPack>, ResearchPack> lookup = ResearchdManagers.getResearchPacksManager(level).getLookup();
        return lookup.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().sortingValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    public static Component getResearchName(ResourceKey<Research> key, Research research) {
        if (research instanceof RegistryDisplay<?> registryDisplay) {
            return registryDisplay.getDisplayNameUnsafe(key);
        } else {
            return Research.getLangName(key);
        }
    }

    public static void refreshResearches(ServerPlayer player) {
        ServerLevel level;
        MinecraftServer server = player.server;
        level = server.overworld();

        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeamImpl team = researchData.getTeamByPlayerId(player.getUUID());
	    Researchd.LOGGER.info("Refreshing effect data for player {}", player.getName().getString());
	    for (Supplier<? extends AttachmentType<? extends ResearchEffectData<?>>> entry : Researchd.RESEARCH_EFFECT_DATA_TYPES) {
		    AttachmentType<ResearchEffectData<?>> attachment = (AttachmentType<ResearchEffectData<?>>) entry.get();
			ResearchEffectData<?> effectData = player.getData(attachment);
            player.setData(attachment, effectData.getDefault(level));
            Researchd.debug("Effect Data", "Refreshing " + effectData.getClass().getSimpleName() + ": ");
            if (effectData.getDefault(level) instanceof DimensionUnlockEffectData(Set<ResourceKey<DimensionType>> blockedDimensions)) {
                for (ResourceKey<DimensionType> dim : blockedDimensions) {
                    Researchd.debug("Effect Data", " - " + dim);
                }
            }

            if (effectData.getDefault(level) instanceof RecipeUnlockEffectData(Set<ResourceLocation> blockedRecipes)) {
                for (ResourceLocation rec : blockedRecipes) {
                    Researchd.debug("Effect Data", " - " + rec);

                }
            }

            if (effectData.getDefault(level) instanceof UnlockItemEffectData(Set<ResourceKey<Item>> blockedItems)) {
                for (ResourceKey<Item> item : blockedItems) {
                    Researchd.debug("Effect Data", " - " + item.location());
                }
            }
        }

        for (ResearchInstance res : team.getResearches().values()) {
            if (res.getResearchStatus() == ResearchStatus.RESEARCHED) {
                ResearchEffect effect = res.lookup(level).researchEffect();
                effect.onUnlock(level, player, res.getResearch());
            }
        }
    }

    public static String getResearchCompletionTime(long teamCreationTime, long time) {
        return new TimeDifference(teamCreationTime, time).getFormatted();
    }

    public static ResearchPack getResearchPack(ResourceKey<ResearchPack> key, Level level) {
        return ResearchdManagers.getResearchPacksManager(level).getLookup().get(key);
    }
}
