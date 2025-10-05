package com.portingdeadmods.researchd.impl.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.utils.ResearchdCodecUtils;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
        for (SimpleResearchTeam team : this.researchTeams.values()) {
            if (team.hasMember(memberUuid)) return team;
        }
        return null;
    }

    public @NotNull ResearchTeam getTeamByMemberOrThrow(UUID memberUuid) {
        for (ResearchTeam team : this.researchTeams.values()) {
            if (team.hasMember(memberUuid)) return team;
        }
        throw new IllegalStateException("Player %s not in a team".formatted(AllPlayersCache.getName(memberUuid).equals("!Unknown Player!") ? memberUuid : AllPlayersCache.getName(memberUuid)));
    }

    public SimpleResearchTeam getTeamByPlayer(Player player) {
        return getTeamByMember(player.getUUID());
    }

    public SimpleResearchTeam getTeamByUUID(UUID teamUuid) {
        return researchTeams().get(teamUuid);
    }

    public static void onSync(Player player) {
        if (player.level().isClientSide) {
            ResearchHelperClient.refreshResearches(player);
            ClientResearchTeamHelper.resolveInstances(ClientResearchTeamHelper.getTeam());
        } else {
            // TODO: Reenable this
            //ResearchHelperCommon.refreshResearches((ServerPlayer) player);
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
