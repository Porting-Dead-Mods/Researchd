package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResearchTeam {
	private String name;
	private List<UUID> members;
	private List<UUID> moderators;
	private List<UUID> sentInvites; // Invites sent by this team to other players
	private List<UUID> receivedInvites; // Invites received by this team from other players
	private UUID leader;
	private boolean isFTJ;

	public static final Codec<ResearchTeam> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.STRING.fieldOf("name").forGetter(ResearchTeam::getName),
			Codec.list(UUIDUtil.CODEC).fieldOf("members").forGetter(ResearchTeam::getMembers),
			Codec.list(UUIDUtil.CODEC).fieldOf("moderators").forGetter(ResearchTeam::getModerators),
			Codec.list(UUIDUtil.CODEC).fieldOf("sent_invites").forGetter(ResearchTeam::getSentInvites),
			Codec.list(UUIDUtil.CODEC).fieldOf("received_invites").forGetter(ResearchTeam::getReceivedInvites),
			UUIDUtil.CODEC.fieldOf("leader").forGetter(ResearchTeam::getLeader),
			Codec.BOOL.fieldOf("is_ftj").forGetter(ResearchTeam::isFreeToJoin)
	).apply(builder, ResearchTeam::new));

	public ResearchTeam(String name, List<UUID> members, List<UUID> moderators, List<UUID> sentInvites, List<UUID> receivedInvites, UUID leader, boolean isFTJ) {
		this.name = name;
		this.members = new ArrayList<>(members);
		this.moderators = new ArrayList<>(moderators);
		this.sentInvites = new ArrayList<>(sentInvites);
		this.receivedInvites = new ArrayList<>(receivedInvites);
		this.leader = leader;
		this.isFTJ = isFTJ;
	}

	public ResearchTeam(UUID uuid) {
		this("New Research Team", List.of(uuid), List.of(), List.of(), List.of(), uuid, false);
	}

	public ResearchTeam(UUID uuid, String name) {
		this(name, List.of(uuid), List.of(), List.of(), List.of(), uuid, false);
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

	public UUID getLeader() {
		return this.leader;
	}

	public boolean isFreeToJoin() {
		return this.isFTJ;
	}

	public void switchFTJ() {
		isFTJ = !isFTJ;
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

	public void setLeader(UUID uuid) {
		leader = uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPermissionLevel(UUID uuid) {
		if (uuid.equals(leader)) {
			return 2;
		} else if (moderators.contains(uuid)) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean isLeader(UUID uuid) {
		return uuid.equals(leader);
	}

	public boolean isModerator(UUID uuid) {
		return moderators.contains(uuid);
	}

	public MutableComponent parseMembers(Level level) {
		MutableComponent[] components = new MutableComponent[this.members.size() + 1];
		components[0] = members.size() == 1 ?
				Component.literal("Team " + this.name + " has " + this.members.size() + " member: ").withStyle(ChatFormatting.WHITE)
				:
				Component.literal("Team " + this.name + " has " + this.members.size() + " members: ").withStyle(ChatFormatting.WHITE);

		for (UUID uuid : this.members) {
			components[this.members.indexOf(uuid) + 1] = Component.literal(level.getPlayerByUUID(uuid).getName().getString() + " ").withStyle(ChatFormatting.AQUA);
		}

		MutableComponent ret = Component.empty();
		for (MutableComponent component : components) {
			ret.append(component);
		}

		return ret;
	}
}
