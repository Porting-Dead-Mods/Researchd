package com.portingdeadmods.researchd.api.data.team;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.data.ResearchQueue;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.research.ValueEffect;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import com.portingdeadmods.researchd.data.helper.ResearchTeamRole;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ResearchTeam {
    public static final GameProfile DEBUG_MEMBER = new GameProfile(UUID.fromString("b7c3f3ac-09b3-4e3c-b788-6f30594b34c6"), "Test player");

    private String name;
    private final List<TeamMember> members;
    private final List<UUID> sentInvites; // Invites sent by this team to other players
    private final List<UUID> receivedInvites; // Invites received by this team from other players
    private UUID owner;

    private final TeamMetadata metadata;

    public static final Codec<ResearchTeam> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(ResearchTeam::getName),
            Codec.list(TeamMember.CODEC).fieldOf("members").forGetter(ResearchTeam::getMembers),
            Codec.list(UUIDUtil.CODEC).fieldOf("sent_invites").forGetter(ResearchTeam::getSentInvites),
            Codec.list(UUIDUtil.CODEC).fieldOf("received_invites").forGetter(ResearchTeam::getReceivedInvites),
            UUIDUtil.CODEC.fieldOf("owner").forGetter(ResearchTeam::getOwner),
            TeamMetadata.CODEC.fieldOf("metadata").forGetter(ResearchTeam::getMetadata)
    ).apply(builder, ResearchTeam::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTeam> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ResearchTeam::getName,
            TeamMember.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ResearchTeam::getMembers,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ResearchTeam::getSentInvites,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ResearchTeam::getReceivedInvites,
            UUIDUtil.STREAM_CODEC,
            ResearchTeam::getOwner,
            TeamMetadata.STREAM_CODEC,
            ResearchTeam::getMetadata,
            ResearchTeam::new
    );

    private ResearchTeam(String name, List<TeamMember> members, List<UUID> sentInvites, List<UUID> receivedInvites, UUID owner, TeamResearchProgress researchProgress) {
        this.name = name;
        this.members = new ArrayList<>(members);
        this.sentInvites = new ArrayList<>(sentInvites);
        this.receivedInvites = new ArrayList<>(receivedInvites);
        this.owner = owner;
        this.metadata = new TeamMetadata(researchProgress);
    }

    private ResearchTeam(String name, List<TeamMember> members, List<UUID> sentInvites, List<UUID> receivedInvites, UUID owner, TeamMetadata metadata) {
        this.name = name;
        this.members = new ArrayList<>(members);
        this.sentInvites = new ArrayList<>(sentInvites);
        this.receivedInvites = new ArrayList<>(receivedInvites);
        this.owner = owner;
        this.metadata = metadata;
    }

    /**
     * Creates a default Research Team with the given owner
     *
     * @param player The Owner
     */
    public static ResearchTeam createDefaultTeam(ServerPlayer player) {
        Researchd.debug("Research Team", "Creating default team for player: " + player.getDisplayName().getString());

        ResearchTeam team = new ResearchTeam(player.getDisplayName().getString() + "'s Team", List.of(new TeamMember(player.getUUID(), ResearchTeamRole.OWNER), new TeamMember(DEBUG_MEMBER.getId(), ResearchTeamRole.MEMBER)), List.of(), List.of(), player.getUUID(), TeamResearchProgress.EMPTY);
        team.setCreationTime(player.getServer().getTickCount() * 50);
        team.init(player.registryAccess());
        return team;
    }

    /**
     * Creates a Research Team with the given name and owner UUID.
     *
     * @param uuid The Owner
     * @param name The Name of the Team
     */
    private ResearchTeam(UUID uuid, String name) {
        this(name, List.of(new TeamMember(uuid, ResearchTeamRole.OWNER)), List.of(), List.of(), uuid, TeamResearchProgress.EMPTY);
    }

    public String getName() {
        return this.name;
    }

    public List<TeamMember> getMembers() {
        return this.members;
    }

    public List<UUID> getSentInvites() {
        return this.sentInvites;
    }

    public List<UUID> getReceivedInvites() {
        return this.receivedInvites;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public TeamMetadata getMetadata() {
        return this.metadata;
    }

    public Map<ResourceKey<Research>, ResearchInstance> getResearches() {
        return this.metadata.researchProgress.researches();
    }

    /**
     * Fetches the team's total research progress
     *
     * @return {@link TeamResearchProgress} of the team, which contains the research queue and progress.
     */
    public TeamResearchProgress getResearchProgress() {
        return this.metadata.researchProgress;
    }

    public @Nullable ResourceKey<Research> getFirstQueueResearch() {
        ResearchQueue queue = this.metadata.researchProgress.researchQueue();
        if (queue.getEntries() == null || queue.getEntries().isEmpty()) return null;

        return queue.getEntries().getFirst();
    }

    /**
     * Fetches the progress of the research that is currently researching.
     *
     * @return {@link ResearchMethodProgress} of the research or null if no research is currently in progress.
     */
    @Nullable
    public ResearchMethodProgress<?> getResearchProgressInQueue() {
        ResearchQueue queue = this.metadata.researchProgress.researchQueue();
        if (queue.getEntries() == null) return null;
        if (queue.getEntries().isEmpty()) return null;

        return this.metadata.researchProgress.getProgress(queue.getEntries().getFirst());
    }

    public void addMember(UUID uuid) {
        TeamMember member = new TeamMember(uuid, ResearchTeamRole.MEMBER);
        if (!members.contains(member))
            members.add(member);
    }

    public void removeMember(UUID uuid) {
        members.removeIf(m -> m.player().equals(uuid));
    }

    public void addModerator(UUID uuid) {
        TeamMember member = new TeamMember(uuid, ResearchTeamRole.MODERATOR);
        if (!members.contains(member)) {
            members.add(member);
        }
    }

    public void removeModerator(UUID uuid) {
        members.removeIf(m -> m.player().equals(uuid));
    }

    public void addSentInvite(UUID uuid) {
        if (!sentInvites.contains(uuid))
            sentInvites.add(uuid);
    }

    public void removeSentInvite(UUID uuid) {
        sentInvites.remove(uuid);
    }

    public void addReceivedInvite(UUID uuid) {
        if (!receivedInvites.contains(uuid))
            receivedInvites.add(uuid);
    }

    public void removeReceivedInvite(UUID uuid) {
        receivedInvites.remove(uuid);
    }

    public void setOwner(UUID uuid) {
        owner = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamMember getMemberByUUID(UUID uuid) {
        for (TeamMember member : this.members) {
            if (member.player().equals(uuid)) {
                return member;
            }
        }
        return null;
    }

    public int getPermissionLevel(UUID uuid) {
        return getMemberByUUID(uuid).role().getPermissionLevel();
    }

    public boolean isOwner(UUID uuid) {
        return uuid.equals(owner);
    }

    public boolean isModerator(UUID uuid) {
        return this.members.stream().anyMatch(m -> m.player().equals(uuid) && m.role() == ResearchTeamRole.MODERATOR);
    }

    public MutableComponent parseMembers(Level level) {
        MutableComponent[] components = new MutableComponent[this.members.size() + 2];
        MutableComponent teamName = Component.literal(this.name).withStyle(ChatFormatting.AQUA);
        components[0] = teamName;

        components[1] = members.size() == 1
                ? Component.literal(" has " + this.members.size() + " player: ").withStyle(ChatFormatting.WHITE)
                : Component.literal(" has " + this.members.size() + " members: ").withStyle(ChatFormatting.WHITE);

        // TODO: What about da member roles :(
        for (TeamMember member : this.members) {
            components[this.members.indexOf(member) + 2] = Component.literal(level.getPlayerByUUID(member.player()).getName().getString() + " ").withStyle(ChatFormatting.AQUA);
        }

        MutableComponent ret = Component.empty();
        for (MutableComponent component : components) {
            ret.append(component);
        }

        return ret;
    }

    /**
     * Sets the creation time of the team. This should be correctly relative to further calculations.
     *
     * @param time The time in ticks when the team was created.
     */
    public void setCreationTime(int time) {
        this.metadata.creationTime.initialize(time);
    }

    public String getResearchCompletionTime(int time) {
        return new TimeUtils.TimeDifference(this.metadata.creationTime.get(), time).getFormatted();
    }

    public Map<ResourceLocation, Float> getTeamEffectList() {
        return this.metadata.teamEffectList;
    }

    public float getTeamEffect(ValueEffect eff) {
        return this.getTeamEffectList().computeIfAbsent(ResearchdRegistries.VALUE_EFFECT.getKey(eff), k -> 1f);
    }

    public float getTeamEffect(Supplier<ValueEffect> eff) {
        return this.getTeamEffectList().computeIfAbsent(ResearchdRegistries.VALUE_EFFECT.getKey(eff.get()), k -> 1f);
    }

    public void init(HolderLookup.Provider lookup) {
        Map<ResourceKey<Research>, ResearchInstance> researchInstances = CommonResearchCache.GLOBAL_RESEARCHES.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), new ResearchInstance(e.getValue(), CommonResearchCache.ROOT_RESEARCH.is(e.getKey())
                        ? ResearchStatus.RESEARCHABLE
                        : ResearchStatus.LOCKED)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        this.getResearchProgress().researches().putAll(researchInstances);

        Map<ResourceKey<Research>, ResearchMethodProgress<?>> rmps = new HashMap<>();
        for (ResourceKey<Research> key : CommonResearchCache.GLOBAL_RESEARCHES.keySet()) {
            rmps.put(key, ResearchMethodProgress.fromResearch(lookup, key));
        }
        this.getResearchProgress().progress().putAll(rmps);
    }

    // Codecs are too small.
    public static class TeamMetadata {
        private final TeamResearchProgress researchProgress;
        private final LazyFinal<Integer> creationTime;
        private final Map<ResourceLocation, Float> teamEffectList;

        public TeamMetadata(TeamResearchProgress progress) {
            this.researchProgress = progress;
            this.creationTime = LazyFinal.create();
            this.teamEffectList = new HashMap<>();
        }

        public TeamMetadata(TeamResearchProgress researchProgress, int creationTime, Map<ResourceLocation, Float> simpleEffectList) {
            this.researchProgress = researchProgress;
            this.creationTime = LazyFinal.create();
            this.creationTime.initialize(creationTime);
            this.teamEffectList = new HashMap<>(simpleEffectList);
        }

        private static TeamMetadata newFromStringMap(TeamResearchProgress researchProgress, int creationTime, Map<String, Float> simpleEffectList) {
            return new TeamMetadata(researchProgress, creationTime, simpleEffectList.entrySet().stream()
                    .map(e -> new AbstractMap.SimpleEntry<>(ResourceLocation.parse(e.getKey()), e.getValue()))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        }

        public static final Codec<TeamMetadata> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                TeamResearchProgress.CODEC.fieldOf("research_progress").forGetter(TeamMetadata::getResearchProgress),
                Codec.INT.fieldOf("creation_time").forGetter(TeamMetadata::getCreationTime),
                Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("simple_effect_list").forGetter(ResearchTeam.TeamMetadata::getTeamMetadataEffectMap)
        ).apply(builder, TeamMetadata::newFromStringMap));

        private static @NotNull Map<String, Float> getTeamMetadataEffectMap(TeamMetadata metadata) {
            return metadata.teamEffectList.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getValue() != null)
                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().toString(), e.getValue()))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, TeamMetadata> STREAM_CODEC = StreamCodec.composite(
                TeamResearchProgress.STREAM_CODEC,
                TeamMetadata::getResearchProgress,
                ByteBufCodecs.INT,
                TeamMetadata::getCreationTime,
                ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.FLOAT),
                TeamMetadata::getTeamEffectList,
                TeamMetadata::new
        );

        public TeamResearchProgress getResearchProgress() {
            return researchProgress;
        }

        public void setCreationTime(int time) {
            this.creationTime.initialize(time);
        }

        public int getCreationTime() {
            return creationTime.get();
        }

        public Map<ResourceLocation, Float> getTeamEffectList() {
            return teamEffectList;
        }
    }
}
