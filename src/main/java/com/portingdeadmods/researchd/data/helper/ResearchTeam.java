package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.UUID;

public class ResearchTeam {
	private String name;
	private List<UUID> members;
	private List<UUID> moderators;
	private List<UUID> invites;
	private UUID leader;
	private boolean isFTJ;

	public static final Codec<ResearchTeam> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.STRING.fieldOf("name").forGetter(ResearchTeam::getName),
			Codec.list(UUIDUtil.CODEC).fieldOf("members").forGetter(ResearchTeam::getMembers),
			Codec.list(UUIDUtil.CODEC).fieldOf("moderators").forGetter(ResearchTeam::getModerators),
			Codec.list(UUIDUtil.CODEC).fieldOf("invites").forGetter(ResearchTeam::getInvites),
			UUIDUtil.CODEC.fieldOf("leader").forGetter(ResearchTeam::getLeader),
			Codec.BOOL.fieldOf("is_ftj").forGetter(ResearchTeam::isFreeToJoin)
	).apply(builder, ResearchTeam::new));

	public ResearchTeam(String name, List<UUID> members, List<UUID> moderators, List<UUID> invites, UUID leader, boolean isFTJ) {
		this.name = name;
		this.members = members;
		this.moderators = moderators;
		this.invites = invites;
		this.leader = leader;
		this.isFTJ = isFTJ;
	}

	public ResearchTeam(UUID uuid) {
		this("New Research Team", List.of(uuid), List.of(), List.of(), uuid, false);
	}

	public String getName() {
		return name;
	}

	public List<UUID> getMembers() {
		return members;
	}

	public List<UUID> getModerators() {
		return moderators;
	}

	public List<UUID> getInvites() {
		return invites;
	}

	public UUID getLeader() {
		return leader;
	}

	public boolean isFreeToJoin() {
		return isFTJ;
	}

	public void switchFTJ() {
		isFTJ = !isFTJ;
	}

	public void addMember(UUID uuid) {
		members.add(uuid);
	}

	public void removeMember(UUID uuid) {
		members.remove(uuid);
	}

	public void addModerator(UUID uuid) {
		moderators.add(uuid);
	}

	public void removeModerator(UUID uuid) {
		moderators.remove(uuid);
	}

	public void addInvite(UUID uuid) {
		invites.add(uuid);
	}

	public void removeInvite(UUID uuid) {
		invites.remove(uuid);
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
}
