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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

    public static List<ResearchInstance> getRecentResearches(ResearchTeam team) {
        UniqueArray<ResearchInstance> researchInstances = team.getResearchProgress().completedResearches();
        return researchInstances.stream().sorted(Comparator.comparingLong(ResearchInstance::getResearchedTime)).toList();
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

    public static void refreshResearches(Player player) {
        Level level;
        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.server;
            level = server.overworld();
        } else {
            level = Minecraft.getInstance().level;
        }

        ResearchTeamMap researchData = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

        ResearchTeam team = researchData.getTeamByMember(player.getUUID());
        for (Map.Entry<ResourceKey<AttachmentType<?>>, AttachmentType<?>> entry : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            Object data = player.getData(entry.getValue());
            if (data instanceof ResearchEffectData<?> effectData) {
                player.setData((AttachmentType<ResearchEffectData<?>>) entry.getValue(), effectData.getDefault(level));
            }
        }

        for (ResearchInstance res : team.getResearchProgress().completedResearches()) {
            ResearchHelper.getResearch(res.getResearch(), level.registryAccess()).researchEffects().forEach(
                    eff -> eff.onUnlock(level, player, res.getResearch())
            );
        }
    }
}
