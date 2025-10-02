package com.portingdeadmods.researchd.impl.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.team.TeamSocialManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record SimpleTeamSocialManager(Set<UUID> receivedInvites, Set<UUID> sentInvites, Set<UUID> ignores) implements TeamSocialManager {
    public static final Codec<SimpleTeamSocialManager> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CodecUtils.set(UUIDUtil.CODEC).fieldOf("received_invites").forGetter(SimpleTeamSocialManager::receivedInvites),
            CodecUtils.set(UUIDUtil.CODEC).fieldOf("sent_invites").forGetter(SimpleTeamSocialManager::sentInvites),
            CodecUtils.set(UUIDUtil.CODEC).fieldOf("ignores").forGetter(SimpleTeamSocialManager::ignores)
    ).apply(inst, SimpleTeamSocialManager::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleTeamSocialManager> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, UUIDUtil.STREAM_CODEC),
            SimpleTeamSocialManager::sentInvites,
            ByteBufCodecs.collection(HashSet::new, UUIDUtil.STREAM_CODEC),
            SimpleTeamSocialManager::receivedInvites,
            ByteBufCodecs.collection(HashSet::new, UUIDUtil.STREAM_CODEC),
            SimpleTeamSocialManager::ignores,
            SimpleTeamSocialManager::new
    );
    public static final SimpleTeamSocialManager EMPTY = new SimpleTeamSocialManager(Set.of(), Set.of(), Set.of());

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
