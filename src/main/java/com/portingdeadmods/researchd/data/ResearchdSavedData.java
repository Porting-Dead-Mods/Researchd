package com.portingdeadmods.researchd.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.helper.ResearchTeam;
import com.portingdeadmods.researchd.data.helper.ResearchTeamMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;


public class ResearchdSavedData extends SavedData {
	public static final String ID = "researchd_saved_data";

	private final ResearchTeamMap researchTeamMap;

	public ResearchdSavedData(ResearchTeamMap researchTeamMap) {
		this.researchTeamMap = researchTeamMap;
	}
	public ResearchdSavedData() {
		this(new ResearchTeamMap());
	}

	public ResearchTeamMap getResearchTeamMap() {
		return this.researchTeamMap;
	}

	public @NotNull ResearchTeam getOrCreateTeamForUUID(UUID uuid) {
		ResearchTeam researchTeam = getTeamForUUID(uuid);
		if (researchTeam == null) {
			researchTeam = new ResearchTeam(uuid);
			this.researchTeamMap.getResearchTeams().put(uuid, researchTeam);
			setDirty();
		}
		return researchTeam;
	}

	public @Nullable ResearchTeam getTeamForUUID(UUID uuid) {
		return this.researchTeamMap.getResearchTeams().get(uuid);
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
		DataResult<Tag> tagDataResult = ResearchTeamMap.CODEC.encodeStart(NbtOps.INSTANCE, this.researchTeamMap);
		tagDataResult
				.resultOrPartial(err -> Researchd.LOGGER.error("Encoding error: {}", err))
				.ifPresent(tag -> compoundTag.put(ID, tag));
		return compoundTag;
	}

	private static ResearchdSavedData load(CompoundTag compoundTag) {
		DataResult<Pair<ResearchTeamMap, Tag>> dataResult = ResearchTeamMap.CODEC.decode(NbtOps.INSTANCE, compoundTag.get(ID));
		Optional<Pair<ResearchTeamMap, Tag>> mapTagPair = dataResult
				.resultOrPartial(err -> Researchd.LOGGER.error("Decoding error: {}", err));

		if (mapTagPair.isPresent()) {
			ResearchTeamMap researchTeamMap1 = mapTagPair.get().getFirst();
			return new ResearchdSavedData(researchTeamMap1);
		}
		return new ResearchdSavedData();
	}

	public static ResearchdSavedData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new IllegalArgumentException("ClientSide handling logic not yet implemented...");
		}
		return level.getServer().overworld().getDataStorage().computeIfAbsent(factory(), ID);
	}

	private static SavedData.Factory<ResearchdSavedData> factory() {
		return new SavedData.Factory<>(ResearchdSavedData::new, (tag, provider) -> load(tag));
	}

	@Override
	public String toString() {
		return "ResearchdSavedData{" +
				"ResearchTeamMap=" + researchTeamMap +
				'}';
	}
}
