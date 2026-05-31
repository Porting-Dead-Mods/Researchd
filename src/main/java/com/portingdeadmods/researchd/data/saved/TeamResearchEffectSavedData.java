package com.portingdeadmods.researchd.data.saved;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.TeamResearchEffectDataMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class TeamResearchEffectSavedData extends SavedData {
    private static final Factory<TeamResearchEffectSavedData> FACTORY = new Factory<>(TeamResearchEffectSavedData::new, TeamResearchEffectSavedData::load);
    private final TeamResearchEffectDataMap map;

    public TeamResearchEffectSavedData() {
        this(new TeamResearchEffectDataMap());
    }

    public TeamResearchEffectSavedData(TeamResearchEffectDataMap map) {
        this.map = map;
    }

    public static TeamResearchEffectDataMap getData(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(FACTORY, "team_research_effect_data").map;
    }

    private static TeamResearchEffectSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        if (compoundTag.contains("map")) {
            DataResult<Pair<TeamResearchEffectDataMap, Tag>> result = TeamResearchEffectDataMap.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("map"));
            return switch (result) {
                case DataResult.Success<Pair<TeamResearchEffectDataMap, Tag>> v -> new TeamResearchEffectSavedData(v.value().getFirst());
                case DataResult.Error<Pair<TeamResearchEffectDataMap, Tag>> v -> {
                    Researchd.LOGGER.error(v.messageSupplier().get());
                    yield new TeamResearchEffectSavedData();
                }
            };
        }
        return new TeamResearchEffectSavedData();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        DataResult<Tag> result = TeamResearchEffectDataMap.CODEC.encodeStart(NbtOps.INSTANCE, this.map);
        switch (result) {
            case DataResult.Success<Tag> v -> compoundTag.put("map", v.value());
            case DataResult.Error<Tag> v -> Researchd.LOGGER.error(v.messageSupplier().get());
        }
        return compoundTag;
    }

}
