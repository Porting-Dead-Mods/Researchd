package com.portingdeadmods.researchd.api.data.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.ResearchdSavedData;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

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

    public @Nullable ResearchTeam getTeamByMember(UUID memberUuid) {
        for (ResearchTeam team : this.researchTeams.values()) {
            for (TeamMember member : team.getMembers()) {
                if (member.player().equals(memberUuid)) return team;
            }
        }
        return null;
    }

    public ResearchTeam getTeamByPlayer(Player player) {
        return getTeamByMember(player.getUUID());
    }

    public ResearchTeam getTeamByUUID(UUID teamUuid) {
		return getResearchTeams().get(teamUuid);
    }

    public static void onSync(Player player) {
        if (player.level().isClientSide) {
            // Usa
            ResearchHelperClient.refreshResearches(player);
            // TODO: Sync them from the server as soon as the player joins
            CommonResearchCache.initialize(player.level());
            ResearchTeamMap data = ResearchdSavedData.TEAM_RESEARCH.get().getData(player.level());
            for (ResearchTeam team : data.getResearchTeams().values()) {
                ClientResearchTeamHelper.resolveInstances(team);
            }
        } else {
            ResearchHelperCommon.refreshResearches((ServerPlayer) player);
        }
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

            researchTeams.put(player.getUUID(), ResearchTeam.createDefaultTeam(player));

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
