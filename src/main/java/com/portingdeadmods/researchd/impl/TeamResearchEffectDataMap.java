package com.portingdeadmods.researchd.impl;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectManager;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import com.portingdeadmods.researchd.data.saved.SavedDataMap;
import com.portingdeadmods.researchd.utils.CollectionUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class TeamResearchEffectDataMap implements ResearchEffectManager, SavedDataMap {
    public static final Codec<TeamResearchEffectDataMap> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.unboundedMap(ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE.byNameCodec(), ResearchEffectData.CODEC))
            .xmap(TeamResearchEffectDataMap::new, m -> m.map);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TeamResearchEffectDataMap> STREAM_CODEC = ByteBufCodecs.map(
                    CollectionUtils::newMap,
                    UUIDUtil.STREAM_CODEC,
                    ByteBufCodecs.map(
                            CollectionUtils::newMap,
                            CodecUtils.registryStreamCodec(ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE),
                            ResearchEffectData.STREAM_CODEC
                    )
            ).map(TeamResearchEffectDataMap::new, m -> m.map);

    private final Map<UUID, Map<ResearchEffectDataType<?>, ResearchEffectData<?>>> map;
    private Runnable onChangedFunction;

    public TeamResearchEffectDataMap(Map<UUID, Map<ResearchEffectDataType<?>, ResearchEffectData<?>>> map) {
        this.map = new HashMap<>(map);
        this.map.forEach((k, v) -> {
            this.map.put(k, new HashMap<>(v));
        });
    }

    public TeamResearchEffectDataMap() {
        this.map = new HashMap<>();
    }

    @Override
    public void setOnChangedFunction(Runnable onChangedFunction) {
        this.onChangedFunction = onChangedFunction;
    }

    @Override
    public void setChanged() {
        if (this.onChangedFunction != null) {
            this.onChangedFunction.run();
        }
    }

    // TODO: In the future we might want to sync this only to players of the provided team
    @Override
    public <T extends ResearchEffectData<?>> void sync(UUID teamId, ResearchEffectDataType<T> type) {
        T effectData = this.getEffectData(teamId, type);
        if (effectData != null) {
            PacketDistributor.sendToAllPlayers(new SyncEffectDataPayload(teamId, effectData));
        }
    }

    @Override
    public <T extends ResearchEffectData<?>> @Nullable T getEffectData(UUID teamId, ResearchEffectDataType<T> type) {
        if (this.map.containsKey(teamId)) {
            return (T) this.map.get(teamId).get(type);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResearchEffectData<?>> @Nullable T computeIfAbsent(UUID teamId, ResearchEffectDataType<T> type, Level level) {
        return (T) this.map
                .computeIfAbsent(teamId, k -> new HashMap<>())
                .computeIfAbsent(type, k -> k.create());
    }

    public void setEffectData(UUID teamId, ResearchEffectData<?> effectData) {
        this.map.computeIfAbsent(teamId, k -> new HashMap<>()).put(effectData.type(), effectData);
    }

    @Override
    public void clearTeam(UUID teamId) {
        this.map.remove(teamId);
    }
}
