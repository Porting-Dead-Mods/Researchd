package com.portingdeadmods.researchd.data.saved;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.team.ResearchTeamMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class TeamSavedData extends SavedData {
    private final ResearchTeamMap map;

    public TeamSavedData(ResearchTeamMap map) {
        this.map = map;
        this.map.setOnChangedFunction(this::setDirty);
    }

    public TeamSavedData() {
        this(new ResearchTeamMap());
    }

    public static ResearchTeamMap getData(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(TeamSavedData.factory(), "team_research").map;
    }

    private static Factory<TeamSavedData> factory() {
        return new Factory<>(TeamSavedData::new, TeamSavedData::load);
    }

    private static TeamSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        if (compoundTag.contains("map")) {
            DataResult<Pair<ResearchTeamMap, Tag>> result = ResearchTeamMap.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("map"));
            return switch (result) {
                case DataResult.Success<Pair<ResearchTeamMap, Tag>> v -> new TeamSavedData(v.value().getFirst());
                case DataResult.Error<Pair<ResearchTeamMap, Tag>> v -> {
                    Researchd.LOGGER.error(v.messageSupplier().get());
                    yield new TeamSavedData();
                }
            };
        }
        return new TeamSavedData();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        DataResult<Tag> result = ResearchTeamMap.CODEC.encodeStart(NbtOps.INSTANCE, this.map);
        switch (result) {
            case DataResult.Success<Tag> v -> compoundTag.put("map", v.value());
            case DataResult.Error<Tag> v -> Researchd.LOGGER.error(v.messageSupplier().get());
        }
        return compoundTag;
    }
}
