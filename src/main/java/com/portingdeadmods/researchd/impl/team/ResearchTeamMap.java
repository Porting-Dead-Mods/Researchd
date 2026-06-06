package com.portingdeadmods.researchd.impl.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.api.team.ResearchTeamRole;
import com.portingdeadmods.researchd.data.saved.SavedDataMap;
import com.portingdeadmods.researchd.data.saved.TeamSavedData;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperServer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ResearchTeamMap implements ResearchTeamManager, SavedDataMap {
    public static final ResearchTeamMap EMPTY = new ResearchTeamMap();
    public static final Codec<ResearchTeamMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, ResearchTeamImpl.CODEC).fieldOf("research_teams").forGetter(t -> t.researchTeams)
    ).apply(builder, ResearchTeamMap::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTeamMap> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    UUIDUtil.STREAM_CODEC,
                    ResearchTeamImpl.STREAM_CODEC
            ),
            t -> t.researchTeams,
            ResearchTeamMap::new
    );
    private final @NotNull Map<UUID, ResearchTeamImpl> researchTeams;
    private final @NotNull List<UUID> teamIds;
    private Runnable onChangedFunction;

	/**
	 * Declared as nullable just to declare null safety. This shouldn't be called with a null map (usually)
	 * <br>
	 * Constructor for codec. Shouldn't really be used outside of that
	 */
    public ResearchTeamMap(@Nullable Map<UUID, ResearchTeamImpl> researchTeams) {
		if (researchTeams == null) {
			Researchd.debug("Research Team Map", "Received null researchTeams map, initializing with empty map. Beware as this might not be intentional.");
			researchTeams = new HashMap<>();
		}

        this.researchTeams = new HashMap<>(researchTeams); // Ensure Mutability
        this.teamIds = new ArrayList<>(researchTeams.keySet());
    }

    public ResearchTeamMap() {
        this(new HashMap<>());
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
    public void addTeam(ResearchTeam team) {
        if (team instanceof ResearchTeamImpl teamImpl) {
            this.researchTeams.put(team.getId(), teamImpl);
            this.teamIds.add(team.getId());

            this.setChanged();
        } else {
            throw new UnsupportedOperationException("Cannot add team of type" + team.getClass().getName() + " to " + this.getClass().getName());
        }
    }

    @Override
    public void removeTeam(UUID teamId) {
        this.researchTeams.remove(teamId);
        this.teamIds.remove(teamId);

        this.setChanged();
    }

    public void updateTeam(ResearchTeamImpl team) {
        this.researchTeams.put(team.getId(), team);

        this.setChanged();
    }

    @Override
    public ResearchTeamImpl getTeamById(UUID uuid) {
        ResearchTeamImpl team = this.researchTeams.get(uuid);
        if (!team.hasOnChangedFunction()) {
            team.setOnChangedFunction(this::setChanged);
        }
        return team;
    }

    @Override
    public ResearchTeamImpl getTeamByName(String name) {
        for (UUID teamId : this.teamIds) {
            ResearchTeamImpl team = this.getTeamById(teamId);
            if (team.getName().equals(name)) {
                if (!team.hasOnChangedFunction()) {
                    team.setOnChangedFunction(this::setChanged);
                }
                return team;
            }
        }
        return null;
    }

    @Override
    public @NotNull ResearchTeamImpl getTeamByPlayerId(UUID uuid) {
        for (UUID teamId : this.teamIds) {
            ResearchTeamImpl team = this.getTeamById(teamId);
            if (team.hasMember(uuid)) {
                if (!team.hasOnChangedFunction()) {
                    team.setOnChangedFunction(this::setChanged);
                }
                return team;
            }
        }
        return null;
    }

    @Override
    public @NotNull Collection<UUID> getTeamIds() {
        return this.teamIds;
    }

    private UUID createUniqueTeamId() {
        UUID teamId = UUID.randomUUID();
        while (this.researchTeams.containsKey(teamId)) {
            teamId = UUID.randomUUID();
        }
        return teamId;
    }

    @Override
    public ResearchTeam createEmptyTeam(String name) {
        UUID teamId = this.createUniqueTeamId();

        ResearchTeamImpl team = new ResearchTeamImpl(teamId, name);
        team.setOnChangedFunction(this::setChanged);
        return team;
    }

    @Override
    public ResearchTeam createDefaultTeam(UUID playerId, Level level) {
        Researchd.debug("Research Team", "Creating default team for player: " + AllPlayersCache.getName(playerId));

        ResearchTeam team = this.createEmptyTeam(AllPlayersCache.getName(playerId) + "'s Team");

        team.addMember(playerId, ResearchTeamRole.OWNER);

        team.setCreationTime(level.getGameTime() * 50);
        team.init(level);

        ResearchTeamHelperServer.initializeTeamEffects(team, level);

        return team;
    }

    public static void initServer(ServerLevel level) {
        ResearchTeamMap map = TeamSavedData.getData(level);
        //ResearchHelperCommon.refreshResearches(map, ull);
    }

    public static void afterSync(Player player) {
//        Level level = player.level();
//        if (level.isClientSide) {
//            ResearchHelperClient.refreshResearches(player);
//            ClientResearchTeamHelper.resolveInstances(ClientResearchTeamHelper.getTeam());
//        } else {
//            ResearchHelperCommon.refreshResearches((ServerPlayer) player);
//        }

        // TODO: Can probably remove this in the future
        // Resolve Map pointers to single team objects for all members
//        ResearchTeamMap data = TeamSavedData.getData(level);
//        Map<UUID, ResearchTeamImpl> temp = new HashMap<>();
//        Map<UUID, ResearchTeamImpl> memberToTeam = new HashMap<>();
//
//        for (Map.Entry<UUID, ResearchTeamImpl> entry : data.researchTeams().entrySet()) {
//            UUID uuid = entry.getKey();
//            ResearchTeamImpl team = entry.getValue();
//
//            // Check if this UUID is already associated with a team
//            ResearchTeamImpl existingTeam = memberToTeam.get(uuid);
//            if (existingTeam != null) {
//                temp.put(uuid, existingTeam);
//                continue;
//            }
//
//            // Otherwise, this is a new unique team
//            temp.put(uuid, team);
//            for (TeamMember member : team.getMembers()) {
//                memberToTeam.put(member.player(), team);
//            }
//        }
//
//        if (temp.equals(data.researchTeams()))
//            return;

//        data.researchTeams().clear();
//        data.researchTeams().putAll(temp);
//        ResearchdSavedData.TEAM_RESEARCH.get().setData(level, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ResearchTeamMap) obj;
        return Objects.equals(this.researchTeams, that.researchTeams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(researchTeams);
    }

    @Override
    public String toString() {
        return "ResearchTeamMap[" +
                "researchTeams=" + researchTeams + ']';
    }

}
