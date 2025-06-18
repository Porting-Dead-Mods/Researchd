package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.cache.ClientResearchCache;
import com.portingdeadmods.researchd.utils.researches.ResearchHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResearchTeamMap {
	public static final ResearchTeamMap EMPTY = new ResearchTeamMap();
	public static final Codec<ResearchTeamMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.unboundedMap(Codec.STRING, ResearchTeam.CODEC).fieldOf("research_teams").forGetter(ResearchTeamMap::teamMapToString)
	).apply(builder, ResearchTeamMap::teamMapFromString));
	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTeamMap> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(
				Object2ObjectOpenHashMap::new,
				ByteBufCodecs.STRING_UTF8,
				ResearchTeam.STREAM_CODEC
			),
			ResearchTeamMap::teamMapToString,
			ResearchTeamMap::teamMapFromString
	);

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

	public ResearchTeam getTeam(UUID uuid) {
		return getResearchTeams().computeIfAbsent(uuid, ResearchTeam::new);
	}

	public ResearchTeam getTeam(ServerPlayer player) {
		return getTeam(player.getUUID());
	}

	public static void onSync(Player player) {
		if (player instanceof LocalPlayer) {
			ResearchHelper.refreshResearches(player);
			ClientResearchCache.initialize(player);
		}
	}

	/**
	 * Creates a team for the player if it doesn't exist.
	 * Does nothing if the player is already in a team.
	 *
	 * Returns false if an error occurred, true otherwise.
	 *
	 * @return a map with team UUIDs as strings
	 */
	public boolean initPlayer(ServerPlayer player) {
		try {
			getTeam(player);
			return true;
		} catch (Exception e) {
			Researchd.LOGGER.error(e.getMessage());
			return false;
		}
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
