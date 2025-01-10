package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResearchTeamMap {
	public static final Codec<ResearchTeamMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.unboundedMap(UUIDUtil.CODEC, ResearchTeam.CODEC).fieldOf("research_teams").forGetter(ResearchTeamMap::getResearchTeams)
	).apply(builder, ResearchTeamMap::new));

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

}
