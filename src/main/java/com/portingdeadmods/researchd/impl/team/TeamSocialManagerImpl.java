package com.portingdeadmods.researchd.impl.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.team.TeamSocialManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record TeamSocialManagerImpl(UniqueArray<UUID> receivedInvites, UniqueArray<UUID> sentInvites, UniqueArray<UUID> ignores) implements TeamSocialManager {
    public static final Codec<TeamSocialManagerImpl> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UniqueArray.CODEC(UUIDUtil.CODEC).fieldOf("received_invites").forGetter(TeamSocialManagerImpl::receivedInvites),
		    UniqueArray.CODEC(UUIDUtil.CODEC).fieldOf("sent_invites").forGetter(TeamSocialManagerImpl::sentInvites),
		    UniqueArray.CODEC(UUIDUtil.CODEC).fieldOf("ignores").forGetter(TeamSocialManagerImpl::ignores)
    ).apply(inst, TeamSocialManagerImpl::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TeamSocialManagerImpl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(UniqueArray::new, UUIDUtil.STREAM_CODEC),
            TeamSocialManagerImpl::sentInvites,
            ByteBufCodecs.collection(UniqueArray::new, UUIDUtil.STREAM_CODEC),
            TeamSocialManagerImpl::receivedInvites,
            ByteBufCodecs.collection(UniqueArray::new, UUIDUtil.STREAM_CODEC),
            TeamSocialManagerImpl::ignores,
            TeamSocialManagerImpl::new
    );
    public static final TeamSocialManagerImpl EMPTY = new TeamSocialManagerImpl(new UniqueArray<>(), new UniqueArray<>(), new UniqueArray<>());

    @Override
    public void addReceivedInvite(UUID uuid) {
        this.receivedInvites.add(uuid);
    }

    @Override
    public void removeReceivedInvite(UUID uuid) {
        this.receivedInvites.remove(uuid);
    }

    @Override
    public boolean containsReceivedInvite(UUID uuid) {
        return this.receivedInvites.contains(uuid);
    }

    @Override
    public void addSentInvite(UUID uuid) {
        this.sentInvites.add(uuid);
    }

    @Override
    public void removeSentInvite(UUID uuid) {
        this.sentInvites.remove(uuid);
    }

    @Override
    public boolean containsSentInvite(UUID uuid) {
        return this.sentInvites.contains(uuid);
    }

    @Override
    public void addIgnore(UUID uuid) {
        this.ignores.add(uuid);
    }

    @Override
    public void removeIgnore(UUID uuid) {
        this.ignores.remove(uuid);
    }

    @Override
    public boolean containsIgnore(UUID uuid) {
        return this.ignores.contains(uuid);
    }
}
