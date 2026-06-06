package com.portingdeadmods.researchd.api.research.effects;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public interface ResearchEffectManager {
    <T extends ResearchEffectData<?>> @Nullable T getEffectData(UUID teamId, ResearchEffectDataType<T> type);

    default <T extends ResearchEffectData<?>> @Nullable T getEffectData(UUID teamId, Supplier<ResearchEffectDataType<T>> type) {
        return this.getEffectData(teamId, type.get());
    }

    <T extends ResearchEffectData<?>> @Nullable T computeIfAbsent(UUID teamId, ResearchEffectDataType<T> type, Level level);

    default <T extends ResearchEffectData<?>> @Nullable T computeIfAbsent(UUID teamId, Supplier<ResearchEffectDataType<T>> type, Level level) {
        return this.computeIfAbsent(teamId, type.get(), level);
    }

    /** Removes all effect data entries for the given team. Used when reinitializing on reload. */
    void clearTeam(UUID teamId);

    void setChanged();

    <T extends ResearchEffectData<?>> void sync(UUID teamId, ResearchEffectDataType<T> type);

    default <T extends ResearchEffectData<?>> void sync(UUID teamId, Supplier<ResearchEffectDataType<T>> type) {
        sync(teamId, type.get());
    }

    default Iterable<ResearchEffectDataType<?>> getDataTypes() {
        return ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE;
    }
}
