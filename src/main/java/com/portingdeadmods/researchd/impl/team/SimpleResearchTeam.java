package com.portingdeadmods.researchd.impl.team;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.team.*;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.utils.ResearchdCodecUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleResearchTeam implements ResearchTeam, ValueEffectsHolder {
    public static final GameProfile DEBUG_MEMBER = new GameProfile(UUID.fromString("b7c3f3ac-09b3-4e3c-b788-6f30594b34c6"), "Test player");

    private String name;
    private final UUID id;
    private final LazyFinal<Long> creationTime;
    private final LinkedHashMap<UUID, TeamMember> members;
    private final SimpleTeamSocialManager socialManager;

    private final TeamResearches researches;
    private final Map<ResourceLocation, Float> effects;

    public static final Codec<SimpleResearchTeam> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(SimpleResearchTeam::getName),
            UUIDUtil.CODEC.fieldOf("id").forGetter(SimpleResearchTeam::getId),
            Codec.unboundedMap(Codec.STRING, TeamMember.CODEC).fieldOf("members").forGetter(t -> ResearchdCodecUtils.encodeMap(t.members)),
            SimpleTeamSocialManager.CODEC.fieldOf("sent_invites").forGetter(t -> t.socialManager),
            TeamResearches.CODEC.fieldOf("researchPacks").forGetter(t -> t.researches),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("effects").forGetter(t -> ResearchdCodecUtils.encodeMap(t.effects))
    ).apply(builder, SimpleResearchTeam::newTeamStringMaps));

    private static @NotNull SimpleResearchTeam newTeamStringMaps(String n, UUID i, Map<String, TeamMember> m, SimpleTeamSocialManager socialManager, TeamResearches tr, Map<String, Float> e) {
        return new SimpleResearchTeam(n, i, ResearchdCodecUtils.decodeMap(m, UUID::fromString), socialManager, tr, ResearchdCodecUtils.decodeMap(e, ResourceLocation::parse));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleResearchTeam> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SimpleResearchTeam::getName,
            UUIDUtil.STREAM_CODEC,
            t -> t.id,
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, TeamMember.STREAM_CODEC),
            t -> t.members,
            SimpleTeamSocialManager.STREAM_CODEC,
            t -> t.socialManager,
            TeamResearches.STREAM_CODEC,
            t -> t.researches,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.FLOAT),
            t -> t.effects,
            SimpleResearchTeam::new
    );

    private SimpleResearchTeam(String name, UUID id, Map<UUID, TeamMember> members, SimpleTeamSocialManager socialManager, TeamResearches teamResearches, Map<ResourceLocation, Float> effects) {
        this.name = name;
        this.id = id;
        this.creationTime = LazyFinal.create();
        this.members = new LinkedHashMap<>(members);
        this.socialManager = socialManager;
        this.researches = teamResearches;
        this.effects = effects;
    }


    /**
     * Creates a Research Team with the given name and owner UUID.
     *
     * @param uuid The Owner
     * @param name The Name of the Team
     */
    private SimpleResearchTeam(UUID uuid, String name) {
        this(name, UUID.randomUUID(), Map.of(uuid, new TeamMember(uuid, ResearchTeamRole.OWNER)), SimpleTeamSocialManager.EMPTY, TeamResearches.EMPTY, new HashMap<>());
    }

	/**
	 * Creates a default Research Team with the given owner
	 *
	 * @param player The Owner
	 */
	public static SimpleResearchTeam createDefaultTeam(UUID player, Level level) {
		Researchd.debug("Research Team", "Creating default team for player: " + AllPlayersCache.getName(player));

		SimpleResearchTeam team = new SimpleResearchTeam(player, AllPlayersCache.getName(player) + "'s Team");
		team.setCreationTime(level.getGameTime() * 50);
		team.init(level);

		return team;
	}

    /**
     * Creates a default Research Team with the given owner
     *
     * @param player The Owner
     */
    public static SimpleResearchTeam createDefaultTeam(ServerPlayer player) {
        return createDefaultTeam(player.getUUID(), player.level());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public TeamMember getOwner() {
        for (TeamMember member : this.members.values()) {
            if (member.role() == ResearchTeamRole.OWNER) {
                return member;
            }
        }
        return null;
    }

    @Override
    public SequencedCollection<TeamMember> getMembers() {
        return new LinkedList<>(this.members.sequencedValues());
    }

    @Override
    public int getMembersAmount() {
        return this.members.size();
    }

    @Override
    public @NotNull TeamMember getMember(UUID uuid) {
		TeamMember member = this.members.get(uuid);
		if (member == null) return new TeamMember(uuid, ResearchTeamRole.NOT_MEMBER);

		return member;
    }

    @Override
    public void setCreationTime(long creationTime) {
        this.creationTime.initialize(creationTime);
    }

    @Override
    public long getCreationTime() {
        return this.creationTime.getOrDefault(0L);
    }

    @Override
    public ResearchQueue getQueue() {
        return this.researches.researchQueue();
    }

    @Override
    public Map<ResourceKey<Research>, ResearchInstance> getResearches() {
        return this.researches.researches();
    }

    @Override
    public Map<ResourceKey<Research>, ResearchProgress> getResearchProgresses() {
        return this.researches.progress();
    }

    @Override
    public void completeResearch(ResourceKey<Research> research, long completionTime, Level level) {
        this.researches.completeResearch(research, completionTime, level);
    }

    @Override
    public void refreshResearchStatus() {
        this.researches.refreshResearchStatus();
    }

    @Override
    public void addMember(UUID uuid) {
        this.members.put(uuid, new TeamMember(uuid, ResearchTeamRole.MEMBER));
    }

    @Override
    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
    }

    @Override
    public void setRole(UUID member, ResearchTeamRole role) {
        this.members.put(member, new TeamMember(member, role));
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean hasMember(UUID uuid) {
        return this.members.containsKey(uuid);
    }

    @Override
    public void addMember(UUID member, ResearchTeamRole role) {
        this.members.put(member, new TeamMember(member, role));
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return this.members.containsKey(uuid) && this.members.get(uuid).role() == ResearchTeamRole.OWNER;
    }

    @Override
    public TeamSocialManager getSocialManager() {
        return this.socialManager;
    }

    @Override
    public boolean isModerator(UUID uuid) {
        TeamMember member = this.members.get(uuid);
        return member != null && member.role() == ResearchTeamRole.MODERATOR;
    }

    @Override
    public float getEffectValue(ValueEffect effect) {
        return this.effects.computeIfAbsent(ResearchdRegistries.VALUE_EFFECT.getKey(effect), k -> 1f);
    }

    @Override
    public void setEffectValue(ValueEffect effect, float value) {
        this.effects.put(ResearchdRegistries.VALUE_EFFECT.getKey(effect), value);
    }

    public void init(Level level) {
        Map<ResourceKey<Research>, ResearchInstance> researchInstances = CommonResearchCache.GLOBAL_RESEARCHES.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), new ResearchInstance(e.getValue(), CommonResearchCache.ROOT_RESEARCH.is(e.getKey())
                        ? ResearchStatus.RESEARCHABLE
                        : ResearchStatus.LOCKED)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        this.getResearches().putAll(researchInstances);

        Map<ResourceKey<Research>, ResearchProgress> rps = new HashMap<>();
        for (ResourceKey<Research> key : CommonResearchCache.GLOBAL_RESEARCHES.keySet()) {
            rps.put(key, ResearchProgress.forResearch(key, level));
        }
        this.getResearchProgresses().putAll(rps);
    }

    public TeamResearches getTeamResearches() {
        return this.researches;
    }

}
