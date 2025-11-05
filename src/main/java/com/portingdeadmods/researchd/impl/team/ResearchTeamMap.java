package com.portingdeadmods.researchd.impl.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.utils.ResearchdCodecUtils;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ResearchTeamMap(Map<UUID, SimpleResearchTeam> researchTeams) {
    public static final ResearchTeamMap EMPTY = new ResearchTeamMap();
    public static final Codec<ResearchTeamMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(Codec.STRING, SimpleResearchTeam.CODEC).fieldOf("research_teams").forGetter(t -> ResearchdCodecUtils.encodeMap(t.researchTeams))
    ).apply(builder, ResearchTeamMap::teamMapFromString));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTeamMap> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    SimpleResearchTeam.STREAM_CODEC
            ),
            t -> ResearchdCodecUtils.encodeMap(t.researchTeams),
            ResearchTeamMap::teamMapFromString
    );

    public ResearchTeamMap() {
        this(new HashMap<>());
    }

    public @Nullable SimpleResearchTeam getTeamByMember(UUID memberUuid) {
        return this.researchTeams.get(memberUuid);
    }

    public @NotNull ResearchTeam getTeamByMemberOrThrow(UUID memberUuid) {
        SimpleResearchTeam team = getTeamByMember(memberUuid);
		if (team != null) return team;
        throw new IllegalStateException("Player %s not in a team".formatted(AllPlayersCache.getName(memberUuid).equals("!Unknown Player!") ? memberUuid : AllPlayersCache.getName(memberUuid)));
    }

	@Nullable
    public SimpleResearchTeam getTeamByPlayer(Player player) {
        return this.researchTeams.get(player.getUUID());
    }

	@Nullable
    public SimpleResearchTeam getTeamByUUID(UUID teamUuid) {
        return this.researchTeams.get(teamUuid);
    }

    public static void afterSync(Player player) {
	    Level level = player.level();
	    if (level.isClientSide) {
            ResearchHelperClient.refreshResearches(player);
            ClientResearchTeamHelper.resolveInstances(ClientResearchTeamHelper.getTeam());
        } else {
            ResearchHelperCommon.refreshResearches((ServerPlayer) player);
        }

		// Resolve Map pointers to single team objects for all members
	    ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
	    Map<UUID, SimpleResearchTeam> temp = new HashMap<>();
	    Map<UUID, SimpleResearchTeam> memberToTeam = new HashMap<>();

	    for (Map.Entry<UUID, SimpleResearchTeam> entry : data.researchTeams().entrySet()) {
		    UUID uuid = entry.getKey();
		    SimpleResearchTeam team = entry.getValue();

		    // Check if this UUID is already associated with a team
		    SimpleResearchTeam existingTeam = memberToTeam.get(uuid);
		    if (existingTeam != null) {
			    temp.put(uuid, existingTeam);
			    continue;
		    }

		    // Otherwise, this is a new unique team
		    temp.put(uuid, team);
		    for (TeamMember member : team.getMembers()) {
			    memberToTeam.put(member.player(), team);
		    }
	    }

		if (temp.equals(data.researchTeams()))
			return;

	    data.researchTeams().clear();
	    data.researchTeams().putAll(temp);
		ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
    }

	public void setDefaultTeam(UUID uuid, Level level) {
		this.researchTeams.put(uuid, SimpleResearchTeam.createDefaultTeam(uuid, level));
	}

	public void setDefaultTeam(ServerPlayer player) {
		this.setDefaultTeam(player.getUUID(), player.level());
	}

    /**
     * Creates a team for the player if it doesn't exist.
     * Does nothing if the player is already in a team.
     * <p>
     * Returns true if a team was created, false if not.
     */
    public boolean initPlayer(ServerPlayer player) {
        try {
            if (getTeamByPlayer(player) != null) return false;

            researchTeams.put(player.getUUID(), SimpleResearchTeam.createDefaultTeam(player));

            return true;
        } catch (Exception e) {
            Researchd.LOGGER.error(e.getMessage());
            return false;
        }
    }

    public static ResearchTeamMap teamMapFromString(Map<String, SimpleResearchTeam> stringedMap) {
        return new ResearchTeamMap(ResearchdCodecUtils.decodeMap(stringedMap, UUID::fromString));
    }
}
