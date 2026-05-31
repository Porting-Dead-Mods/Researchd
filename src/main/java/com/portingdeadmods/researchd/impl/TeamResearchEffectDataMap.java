package com.portingdeadmods.researchd.impl;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectManager;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import com.portingdeadmods.researchd.data.saved.SavedDataMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class TeamResearchEffectDataMap implements ResearchEffectManager, SavedDataMap {
    public static final Codec<TeamResearchEffectDataMap> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.unboundedMap(ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE.byNameCodec(), ResearchEffectData.CODEC))
            .xmap(TeamResearchEffectDataMap::new, m -> m.map);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TeamResearchEffectDataMap> STREAM_CODEC = ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, ByteBufCodecs.map(HashMap::new, CodecUtils.registryStreamCodec(ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE), ResearchEffectData.STREAM_CODEC))
            .map(TeamResearchEffectDataMap::new, m -> (HashMap) m.map);

    private final Map<UUID, Map<ResearchEffectDataType<?>, ResearchEffectData<?>>> map;
    private Runnable onChangedFunction;

    public TeamResearchEffectDataMap(HashMap<UUID, HashMap<ResearchEffectDataType<?>, ResearchEffectData<?>>> map) {
        this.map = (Map) map;
    }

    public TeamResearchEffectDataMap(Map<UUID, Map<ResearchEffectDataType<?>, ResearchEffectData<?>>> map) {
        this.map = map;
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
                .computeIfAbsent(type, k -> {
                    ResearchEffectData<?> data = k.create();
                    data.initDefault(level);
                    return data;
                });
    }

}
