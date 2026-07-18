package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.*;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectList;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.research.ResearchRelations;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import com.portingdeadmods.researchd.impl.team.ResearchTeamImpl;
import com.portingdeadmods.researchd.utils.NumberUtils;
import com.portingdeadmods.researchd.utils.registries.ResearchdManagers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;

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

    private static void _collectChildren(ResearchRelations research, List<ResourceKey<Research>> list) {
        for (ResearchRelations child : research.getChildren()) {
            list.add(child.getResearchKey());
            if (!child.getChildren().isEmpty()) {
                _collectChildren(child, list);
            }
        }
    }

    private static void _collectParents(ResearchRelations research, List<ResourceKey<Research>> list) {
        for (ResearchRelations parent : research.getParents()) {
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

    public static void refreshResearches(ResearchTeamMap teamMap, Player player) {
        Level level = player.level();

        ResearchTeamImpl team = teamMap.getTeamByPlayerId(player.getUUID());
        if (team == null) return;

        // FIXME: WHY ARE CALLING UNLOCK HERE EVEN THOUGH IT IS ALREADY CALLED WHEN RESEARCH IS UNLOCKED
        for (ResearchInstance res : team.getResearches().values()) {
            if (res.getResearchStatus() == ResearchStatus.RESEARCHED) {
                Research research = res.lookup(level);
                if (research == null) continue;

                research.researchEffect().onUnlock(level, team, res.getResearch());
            }
        }
    }

    public static String getResearchCompletionTime(long teamCreationTime, long time) {
        return NumberUtils.getTimeDifferenceFormatted(teamCreationTime, time);
    }

    public static ResearchPack getResearchPack(ResourceKey<ResearchPack> key, Level level) {
        return ResearchdManagers.getResearchPacksManager(level).getLookup().get(key);
    }
}
