package com.portingdeadmods.researchd.data.helper;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.LazyFinal;
import com.portingdeadmods.researchd.utils.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO: Move to api
public class ResearchTeam {
	public static final GameProfile DEBUG_MEMBER = new GameProfile(UUID.fromString("b7c3f3ac-09b3-4e3c-b788-6f30594b34c6"), "Test player");

	private String name;
	private final List<UUID> members;
	private final List<UUID> moderators;
	private final List<UUID> sentInvites; // Invites sent by this team to other players
	private final List<UUID> receivedInvites; // Invites received by this team from other players
	private UUID owner;

	private TeamMetadata metadata;

	public static final Codec<ResearchTeam> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.STRING.fieldOf("name").forGetter(ResearchTeam::getName),
			Codec.list(UUIDUtil.CODEC).fieldOf("members").forGetter(ResearchTeam::getMembers),
			Codec.list(UUIDUtil.CODEC).fieldOf("moderators").forGetter(ResearchTeam::getModerators),
			Codec.list(UUIDUtil.CODEC).fieldOf("sent_invites").forGetter(ResearchTeam::getSentInvites),
			Codec.list(UUIDUtil.CODEC).fieldOf("received_invites").forGetter(ResearchTeam::getReceivedInvites),
			UUIDUtil.CODEC.fieldOf("owner").forGetter(ResearchTeam::getOwner),
			TeamMetadata.CODEC.fieldOf("metadata").forGetter(ResearchTeam::getMetadata)
	).apply(builder, ResearchTeam::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTeam> STREAM_CODEC = NeoForgeStreamCodecs.composite(
			ByteBufCodecs.STRING_UTF8,
			ResearchTeam::getName,
			UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
			ResearchTeam::getMembers,
			UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
			ResearchTeam::getModerators,
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

	public ResearchTeam(String name, List<UUID> members, List<UUID> moderators, List<UUID> sentInvites, List<UUID> receivedInvites, UUID leader, @Nullable ResearchProgress researchProgress) {
		this.name = name;
		this.members = new ArrayList<>(members);
		this.moderators = new ArrayList<>(moderators);
		this.sentInvites = new ArrayList<>(sentInvites);
		this.receivedInvites = new ArrayList<>(receivedInvites);
		this.owner = leader;
		this.metadata = new TeamMetadata(researchProgress);
	}

	public ResearchTeam(String name, List<UUID> members, List<UUID> moderators, List<UUID> sentInvites, List<UUID> receivedInvites, UUID leader, TeamMetadata metadata) {
		this.name = name;
		this.members = new ArrayList<>(members);
		this.moderators = new ArrayList<>(moderators);
		this.sentInvites = new ArrayList<>(sentInvites);
		this.receivedInvites = new ArrayList<>(receivedInvites);
		this.owner = leader;
		this.metadata = metadata;
	}

	/**
	 * Creates a default Research Team with the given owner
	 * @param player The Owner
	 */
	public static ResearchTeam createDefaultTeam(ServerPlayer player) {
		Researchd.debug("Research Team", "Creating default team for player: " + player.getDisplayName().getString());

		ResearchTeam team = new ResearchTeam(player.getDisplayName().getString() + "'s Team", List.of(player.getUUID(), DEBUG_MEMBER.getId()), List.of(), List.of(), List.of(), player.getUUID(), ResearchProgress.EMPTY);
		team.setCreationTime(player.getServer().getTickCount() * 50);
		return team;
	}

	/**
	 * Creates a Research Team with the given name and owner UUID.
	 * @param uuid The Owner
	 * @param name The Name of the Team
	 */
	public ResearchTeam(UUID uuid, String name) {
		this(name, List.of(uuid), List.of(), List.of(), List.of(), uuid, ResearchProgress.EMPTY);
	}

	public String getName() {
		return this.name;
	}

	public List<UUID> getMembers() {
		return this.members;
	}

	public List<UUID> getModerators() {
		return this.moderators;
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

	public ResearchProgress getResearchProgress() {
		return this.metadata.researchProgress;
	}

	public void addMember(UUID uuid) {
		if (!members.contains(uuid))
			members.add(uuid);
	}

	public void removeMember(UUID uuid) {
		if (members.contains(uuid))
			members.remove(uuid);
	}

	public void addModerator(UUID uuid) {
		if (!moderators.contains(uuid))
			moderators.add(uuid);
	}

	public void removeModerator(UUID uuid) {
		if (moderators.contains(uuid))
			moderators.remove(uuid);
	}

	public void addSentInvite(UUID uuid) {
		if (!sentInvites.contains(uuid))
			sentInvites.add(uuid);
	}

	public void removeSentInvite(UUID uuid) {
		if (sentInvites.contains(uuid))
			sentInvites.remove(uuid);
	}

	public void addReceivedInvite(UUID uuid) {
		if (!receivedInvites.contains(uuid))
			receivedInvites.add(uuid);
	}

	public void removeReceivedInvite(UUID uuid) {
		if (receivedInvites.contains(uuid))
			receivedInvites.remove(uuid);
	}

	public void setOwner(UUID uuid) {
		owner = uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPermissionLevel(UUID uuid) {
		if (uuid.equals(owner)) {
			return 2;
		} else if (moderators.contains(uuid)) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean isOwner(UUID uuid) {
		return uuid.equals(owner);
	}

	public boolean isModerator(UUID uuid) {
		return moderators.contains(uuid);
	}

	public MutableComponent parseMembers(Level level) {
		MutableComponent[] components = new MutableComponent[this.members.size() + 2];
		MutableComponent teamName = Component.literal(this.name).withStyle(ChatFormatting.AQUA);
		components[0] = teamName;

		components[1] = members.size() == 1 ?
				Component.literal(" has " + this.members.size() + " member: ").withStyle(ChatFormatting.WHITE)
				:
				Component.literal(" has " + this.members.size() + " members: ").withStyle(ChatFormatting.WHITE);

		for (UUID uuid : this.members) {
			components[this.members.indexOf(uuid) + 2] = Component.literal(level.getPlayerByUUID(uuid).getName().getString() + " ").withStyle(ChatFormatting.AQUA);
		}

		MutableComponent ret = Component.empty();
		for (MutableComponent component : components) {
			ret.append(component);
		}

		return ret;
	}

	/**
	 * Sets the creation time of the team. This should be correctly relative to further calculations.
	 * @param time The time in ticks when the team was created.
	 */
	public void setCreationTime(int time) {
		this.metadata.creationTime.initialize(time);
	}

	public String getResearchCompletionTime(int time) {
		return new TimeUtils.TimeDifference(this.metadata.creationTime.get(), time).getFormatted();
	}

	// Codecs are too small.
	public static class TeamMetadata {
		private final ResearchProgress researchProgress;
		private final LazyFinal<Integer> creationTime;

		public TeamMetadata(ResearchProgress progress) {
			this.researchProgress = progress;
			this.creationTime = LazyFinal.create();
		}

		public TeamMetadata(ResearchProgress researchProgress, int creationTime) {
			this.researchProgress = researchProgress;
			this.creationTime = LazyFinal.create();
			this.creationTime.initialize(creationTime);
		}

		public static Codec<TeamMetadata> CODEC = RecordCodecBuilder.create(builder -> builder.group(
					ResearchProgress.CODEC.fieldOf("research_progress").forGetter(TeamMetadata::getResearchProgress),
					Codec.INT.fieldOf("creation_time").forGetter(TeamMetadata::getCreationTime)
			).apply(builder, TeamMetadata::new));

		public static StreamCodec<RegistryFriendlyByteBuf, TeamMetadata> STREAM_CODEC = StreamCodec.composite(
			ResearchProgress.STREAM_CODEC,
			TeamMetadata::getResearchProgress,
			ByteBufCodecs.INT,
			TeamMetadata::getCreationTime,
			TeamMetadata::new
		);

		public ResearchProgress getResearchProgress() {
			return researchProgress;
		}

		public void setCreationTime(int time) {
			this.creationTime.initialize(time);
		}

		public int getCreationTime() {
			return creationTime.get();
		}
	}
}
