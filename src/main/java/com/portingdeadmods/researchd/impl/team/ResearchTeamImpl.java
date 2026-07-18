package com.portingdeadmods.researchd.impl.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.ValueEffect;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import com.portingdeadmods.researchd.api.team.*;
import com.portingdeadmods.researchd.compat.KubeJSCompat;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.networking.research.ClientResearchCompletedPayload;
import com.portingdeadmods.researchd.networking.team.manager.SyncTeamPayload;
import com.portingdeadmods.researchd.utils.ResearchdCodecUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResearchTeamImpl implements ResearchTeam, ValueEffectsHolder {
    private String name;
    private final UUID id;
    private final LazyFinal<Long> creationTime;
    private final LinkedHashMap<UUID, TeamMember> members;
    private final TeamSocialManagerImpl socialManager;

    private final TeamResearches researches;
    private final Map<ResourceLocation, Float> effects;

    private Runnable onChangedFunction;

    public static final Codec<ResearchTeamImpl> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(ResearchTeamImpl::getName),
            UUIDUtil.CODEC.fieldOf("id").forGetter(ResearchTeamImpl::getId),
            Codec.unboundedMap(Codec.STRING, TeamMember.CODEC).fieldOf("members").forGetter(t -> ResearchdCodecUtils.encodeMap(t.members)),
            TeamSocialManagerImpl.CODEC.fieldOf("sent_invites").forGetter(t -> t.socialManager),
            TeamResearches.CODEC.fieldOf("researchPacks").forGetter(t -> t.researches),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("effects").forGetter(t -> ResearchdCodecUtils.encodeMap(t.effects))
    ).apply(builder, ResearchTeamImpl::newTeamStringMaps));

    private static @NotNull ResearchTeamImpl newTeamStringMaps(String n, UUID i, Map<String, TeamMember> m, TeamSocialManagerImpl socialManager, TeamResearches tr, Map<String, Float> e) {
        return new ResearchTeamImpl(n, i, ResearchdCodecUtils.decodeMap(m, UUID::fromString), socialManager, tr, ResearchdCodecUtils.decodeMap(e, ResourceLocation::parse));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTeamImpl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ResearchTeamImpl::getName,
            UUIDUtil.STREAM_CODEC,
            t -> t.id,
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, TeamMember.STREAM_CODEC),
            t -> t.members,
            TeamSocialManagerImpl.STREAM_CODEC,
            t -> t.socialManager,
            TeamResearches.STREAM_CODEC,
            t -> t.researches,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.FLOAT),
            t -> t.effects,
            ResearchTeamImpl::new
    );

    private ResearchTeamImpl(String name, UUID id, Map<UUID, TeamMember> members, TeamSocialManagerImpl socialManager, TeamResearches teamResearches, Map<ResourceLocation, Float> effects) {
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
     * @param teamId The Owner
     * @param teamName The Name of the Team
     */
    //private ResearchTeamImpl(UUID uuid, String name) {
    //    this(name, UUID.randomUUID(), Map.of(uuid, new TeamMember(uuid, ResearchTeamRole.OWNER)), TeamSocialManagerImpl.EMPTY, TeamResearches.EMPTY, new HashMap<>());
    //}

    public ResearchTeamImpl(UUID teamId, String teamName) {
        this(teamName, teamId, new HashMap<>(), TeamSocialManagerImpl.EMPTY, TeamResearches.EMPTY, new HashMap<>());
    }

    public void setOnChangedFunction(Runnable onChangedFunction) {
        this.onChangedFunction = onChangedFunction;
    }

    public void setChanged() {
        if (this.onChangedFunction != null) {
            this.onChangedFunction.run();
        }
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull UUID getId() {
        return this.id;
    }

    @Override
    public @Nullable TeamMember getOwner() {
        for (TeamMember member : this.members.values()) {
            if (member.role() == ResearchTeamRole.OWNER) {
                return member;
            }
        }
        return null;
    }

    @Override
    public @NotNull SequencedCollection<TeamMember> getMembers() {
        return this.members.sequencedValues().stream().sorted().toList();
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

        this.setChanged();
    }

    @Override
    public long getCreationTime() {
        return this.creationTime.getOrDefault(0L);
    }

    @Override
    public @NotNull ResearchQueue getQueue() {
        return this.researches.researchQueue();
    }

    @Override
    public @NotNull Map<ResourceKey<Research>, ResearchInstance> getResearches() {
        return this.researches.researches();
    }

    @Override
    public @NotNull Map<ResourceKey<Research>, ResearchProgress> getResearchProgresses() {
        return this.researches.progress();
    }

    @Override
    public void setResearchCompleted(ResourceKey<Research> research, long completionTime) {
        this.researches.setResearchFinished(research, completionTime);

        this.setChanged();
    }

    @Override
    public void onCompleteResearch(ResourceKey<Research> researchKey, long completionTime, boolean forced, Function<UUID, Player> playerGetter) {
        ResearchInstance instance = this.getResearches().get(researchKey);
        if (instance == null || instance.getResearchedTime() != completionTime) {
            return;
        }

        Level level = null;
        Research research = null;
        for (TeamMember member : this.getMembers()) {
            Player player = playerGetter.apply(member.player());
            if (player == null) continue;

            level = player.level();
            if (level.isClientSide()) return;

            if (research == null)  {
                research = ResearchdApi.getResearchManager().lookupResearch(researchKey, level);
            }

            PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientResearchCompletedPayload(researchKey, (int) completionTime, forced));

            KubeJSCompat.fireResearchCompletedEvent((ServerPlayer) player, researchKey);
        }
        if (level == null || research == null) return;

        research.researchEffect().onUnlock(level, this, researchKey);
    }

    @Override
    public void onRemoveResearch(ResourceKey<Research> researchKey, Function<UUID, Player> playerGetter) {
        ResearchInstance instance = this.getResearches().get(researchKey);
        if (instance == null || !instance.isResearched()) return;

        this.researches.setResearchUnfinished(researchKey);
        this.setChanged();

        Level level = null;
        Research research = null;
        for (TeamMember member : this.getMembers()) {
            Player player = playerGetter.apply(member.player());
            if (player == null) continue;

            level = player.level();
            if (level.isClientSide()) return;

            if (research == null) {
                research = ResearchdApi.getResearchManager().lookupResearch(researchKey, level);
            }
        }
        if (level == null || research == null) return;

        research.researchEffect().onLock(level, this, researchKey);
        PacketDistributor.sendToAllPlayers(new SyncTeamPayload(this));
    }

    @Override
    public void refreshResearchStatus() {
        this.researches.refreshResearchStatus();

        this.setChanged();
    }

    @Override
    public void addMember(UUID uuid) {
        this.members.put(uuid, new TeamMember(uuid, ResearchTeamRole.MEMBER));

        this.setChanged();
    }

    @Override
    public void removeMember(UUID uuid) {
        this.members.remove(uuid);

        this.setChanged();
    }

    @Override
    public void setRole(UUID member, ResearchTeamRole role) {
        this.members.put(member, new TeamMember(member, role));

        this.setChanged();
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

        this.setChanged();
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return this.members.containsKey(uuid) && this.members.get(uuid).role() == ResearchTeamRole.OWNER;
    }

    @Override
    public @NotNull TeamSocialManager getSocialManager() {
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

        this.setChanged();
    }

    @Override
    public void clearAllEffectValues() {
        this.effects.clear();
        this.setChanged();
    }

    // TODO: Merge this with refreshResearches
    public void init(Level level) {
        ResearchManager researchManager = ResearchdApi.getResearchManager();
        Map<ResourceKey<Research>, ResearchInstance> researchInstances = researchManager.getResearches().stream()
                .map(key -> new AbstractMap.SimpleEntry<>(key, new ResearchInstance(key, researchManager.getRootsForPage(researchManager.getPageByResearch(key).id()).contains(key)
                        ? ResearchStatus.RESEARCHABLE
                        : ResearchStatus.LOCKED)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        this.getResearches().putAll(researchInstances);

        Map<ResourceKey<Research>, ResearchProgress> rps = new HashMap<>();
        for (ResourceKey<Research> key : researchManager.getResearches()) {
            rps.put(key, ResearchProgress.forResearch(key, level));
        }
        this.getResearchProgresses().putAll(rps);

        this.setChanged();
    }

    public TeamResearches getTeamResearches() {
        return this.researches;
    }

    public boolean hasOnChangedFunction() {
        return this.onChangedFunction != null;
    }
}
