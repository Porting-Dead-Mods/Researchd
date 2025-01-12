package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResearchTeamMap {
	public static final Codec<ResearchTeamMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.unboundedMap(Codec.STRING, ResearchTeam.CODEC).fieldOf("research_teams").forGetter(ResearchTeamMap::teamMapToString)
	).apply(builder, ResearchTeamMap::teamMapFromString));

	private final Map<UUID, ResearchTeam> researchTeams;

	public ResearchTeamMap() {
		this.researchTeams = new HashMap<>();
	}

	public ResearchTeamMap(Map<UUID, ResearchTeam> researchTeams) {
		this.researchTeams = researchTeams;
	}

	public Map<UUID, ResearchTeam> getResearchTeams() {
		return this.researchTeams;
	}

	public Map<String, ResearchTeam> teamMapToString() {
		return getResearchTeams().entrySet().stream()
				.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey().toString(), entry.getValue()))
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
	}

	public static ResearchTeamMap teamMapFromString(Map<String, ResearchTeam> stringedMap) {
		return new ResearchTeamMap(stringedMap.entrySet().stream()
				.map(entry -> new AbstractMap.SimpleEntry<>(UUID.fromString(entry.getKey()), entry.getValue()))
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
	}
}
