package com.portingdeadmods.researchd.impl.team;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.team.ResearchQueue;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.TeamMember;
import com.portingdeadmods.researchd.api.team.ValueEffectsHolder;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
import com.portingdeadmods.researchd.utils.ResearchdCodecUtils;
import com.portingdeadmods.researchd.utils.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleResearchTeam implements ResearchTeam, ValueEffectsHolder {
    public static final GameProfile DEBUG_MEMBER = new GameProfile(UUID.fromString("b7c3f3ac-09b3-4e3c-b788-6f30594b34c6"), "Test player");

    private String name;
    private final UUID id;
    private UUID owner;
    private final LazyFinal<Long> creationTime;
    private final Map<UUID, TeamMember> members;
    private final List<UUID> sentInvites; // Invites sent by this team to other players
    private final List<UUID> receivedInvites; // Invites received by this team from other players
	private final List<UUID> ignores;

    private final TeamResearches researches;
    private final Map<ResourceLocation, Float> effects;

    public static final Codec<SimpleResearchTeam> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(SimpleResearchTeam::getName),
            UUIDUtil.CODEC.fieldOf("id").forGetter(SimpleResearchTeam::getId),
            Codec.unboundedMap(Codec.STRING, TeamMember.CODEC).fieldOf("members").forGetter(t -> ResearchdCodecUtils.encodeMap(t.members)),
            Codec.list(UUIDUtil.CODEC).fieldOf("sent_invites").forGetter(SimpleResearchTeam::getSentInvites),
            Codec.list(UUIDUtil.CODEC).fieldOf("received_invites").forGetter(SimpleResearchTeam::getReceivedInvites),
		    Codec.list(UUIDUtil.CODEC).fieldOf("ignores").forGetter(SimpleResearchTeam::getIgnores),
            UUIDUtil.CODEC.fieldOf("owner").forGetter(t -> t.owner),
            TeamResearches.CODEC.fieldOf("researches").forGetter(t -> t.researches),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("effects").forGetter(t -> ResearchdCodecUtils.encodeMap(t.effects))
    ).apply(builder, SimpleResearchTeam::newTeamStringMaps));

    private static @NotNull SimpleResearchTeam newTeamStringMaps(String n, UUID i, Map<String, TeamMember> m, List<UUID> s, List<UUID> r, List<UUID> ig, UUID o, TeamResearches tr, Map<String, Float> e) {
        return new SimpleResearchTeam(n, i, ResearchdCodecUtils.decodeMap(m, UUID::fromString), s, r, ig, o, tr, ResearchdCodecUtils.decodeMap(e, ResourceLocation::parse));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleResearchTeam> STREAM_CODEC = CodecUtils.streamCodecComposite(
            ByteBufCodecs.STRING_UTF8,
            SimpleResearchTeam::getName,
            UUIDUtil.STREAM_CODEC,
            t -> t.id,
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, TeamMember.STREAM_CODEC),
            SimpleResearchTeam::getMembers,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SimpleResearchTeam::getSentInvites,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SimpleResearchTeam::getReceivedInvites,
		    UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
		    SimpleResearchTeam::getIgnores,
            UUIDUtil.STREAM_CODEC,
            t -> t.owner,
            TeamResearches.STREAM_CODEC,
            t -> t.researches,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.FLOAT),
            t -> t.effects,
            SimpleResearchTeam::new
    );

    private SimpleResearchTeam(String name, UUID id, Map<UUID, TeamMember> members, List<UUID> sentInvites, List<UUID> receivedInvites, List<UUID> ignores, UUID owner, TeamResearches teamResearches, Map<ResourceLocation, Float> effects) {
        this.name = name;
        this.id = id;
        this.owner = owner;
        this.creationTime = LazyFinal.create();
        this.members = new HashMap<>(members);
        this.sentInvites = new ArrayList<>(sentInvites);
        this.receivedInvites = new ArrayList<>(receivedInvites);
		this.ignores = new ArrayList<>(ignores);
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
        this(name, UUID.randomUUID(), Map.of(uuid, new TeamMember(uuid, ResearchTeamRole.OWNER)), List.of(), List.of(), List.of(), uuid, TeamResearches.EMPTY, new HashMap<>());
    }

    /**
     * Creates a default Research Team with the given owner
     *
     * @param player The Owner
     */
    public static SimpleResearchTeam createDefaultTeam(ServerPlayer player) {
        Researchd.debug("Research Team", "Creating default team for player: " + player.getDisplayName().getString());

        SimpleResearchTeam team = new SimpleResearchTeam(player.getUUID(), player.getDisplayName().getString() + "'s Team");
        team.setCreationTime(player.getServer().getTickCount() * 50);
        team.init(player.registryAccess());

        return team;
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
        return this.members.get(this.owner);
    }

    @Override
    public Map<UUID, TeamMember> getMembers() {
        return this.members;
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
    public Map<ResourceKey<Research>, ResearchMethodProgress<?>> getResearchProgresses() {
        return this.researches.progress();
    }

    @Override
    public void completeResearch(ResourceKey<Research> research, long completionTime, Level level) {
        this.researches.completeResearch(research, completionTime, level);
    }

    public List<UUID> getSentInvites() {
        return this.sentInvites;
    }

    public List<UUID> getReceivedInvites() {
        return this.receivedInvites;
    }

	public List<UUID> getIgnores() {
		return this.ignores;
	}

	public void addIgnore(UUID uuid) {
		if (!ignores.contains(uuid))
			ignores.add(uuid);
	}

    public void addMember(UUID uuid) {
        this.members.put(uuid, new TeamMember(uuid, ResearchTeamRole.MEMBER));
    }

    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
    }

    public void addModerator(UUID uuid) {
        TeamMember member = new TeamMember(uuid, ResearchTeamRole.MODERATOR);
        this.members.put(uuid, member);
    }

    public void removeModerator(UUID uuid) {
        this.members.remove(uuid);
    }

    public void addSentInvite(UUID uuid) {
        if (!this.sentInvites.contains(uuid))
            this.sentInvites.add(uuid);
    }

    public void removeSentInvite(UUID uuid) {
        this.sentInvites.remove(uuid);
    }

    public void addReceivedInvite(UUID uuid) {
        if (!this.receivedInvites.contains(uuid))
            this.receivedInvites.add(uuid);
    }

    public void removeReceivedInvite(UUID uuid) {
        this.receivedInvites.remove(uuid);
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamMember getMemberByUUID(UUID uuid) {
        return this.members.get(uuid);
    }

    public int getPermissionLevel(UUID uuid) {
        return getMemberByUUID(uuid).role().getPermissionLevel();
    }

    public boolean isOwner(UUID uuid) {
        return uuid.equals(owner);
    }

    public boolean isModerator(UUID uuid) {
        TeamMember member = this.members.get(uuid);
        return member != null && member.role() == ResearchTeamRole.MODERATOR;
    }

    public boolean isPresentInTeam(UUID uuid) {
        return this.members.containsKey(uuid);
    }

    public MutableComponent parseMembers(Level level) {
        MutableComponent[] components = new MutableComponent[this.members.size() + 2];
        MutableComponent teamName = Component.literal(this.name).withStyle(ChatFormatting.AQUA);
        components[0] = teamName;

        components[1] = members.size() == 1
                ? Component.literal(" has " + this.members.size() + " player: ").withStyle(ChatFormatting.WHITE)
                : Component.literal(" has " + this.members.size() + " members: ").withStyle(ChatFormatting.WHITE);

        // TODO: What about da member roles :(
        int i = 0;
        for (TeamMember member : this.members.values()) {
            Player player = level.getPlayerByUUID(member.player());
            if (player != null)
                components[i + 2] = Component.literal(player.getName().getString() + " ").withStyle(ChatFormatting.AQUA);
            i++;
        }

        MutableComponent ret = Component.empty();
        for (MutableComponent component : components) {
            ret.append(component);
        }

        return ret;
    }


    public String getResearchCompletionTime(int time) {
        return new TimeUtils.TimeDifference(this.creationTime.get(), time).getFormatted();
    }

    public void init(HolderLookup.Provider lookup) {
        Map<ResourceKey<Research>, ResearchInstance> researchInstances = CommonResearchCache.GLOBAL_RESEARCHES.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), new ResearchInstance(e.getValue(), CommonResearchCache.ROOT_RESEARCH.is(e.getKey())
                        ? ResearchStatus.RESEARCHABLE
                        : ResearchStatus.LOCKED)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        this.getResearches().putAll(researchInstances);

        Map<ResourceKey<Research>, ResearchMethodProgress<?>> rmps = new HashMap<>();
        for (ResourceKey<Research> key : CommonResearchCache.GLOBAL_RESEARCHES.keySet()) {
            rmps.put(key, ResearchMethodProgress.fromResearch(lookup, key));
        }
        this.getResearchProgresses().putAll(rmps);
    }

    @Override
    public float getEffectValue(ValueEffect effect) {
        return this.effects.computeIfAbsent(ResearchdRegistries.VALUE_EFFECT.getKey(effect), k -> 1f);
    }

    @Override
    public void setEffectValue(ValueEffect effect, float value) {
        this.effects.put(ResearchdRegistries.VALUE_EFFECT.getKey(effect), value);
    }

    public TeamResearches getTeamResearches() {
        return this.researches;
    }

}
