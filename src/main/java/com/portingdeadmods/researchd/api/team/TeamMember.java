package com.portingdeadmods.researchd.api.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.cache.AllPlayersCache;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.EntityGetter;

import java.util.UUID;

public record TeamMember(UUID player, ResearchTeamRole role) {
    public static final Codec<TeamMember> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("player").forGetter(TeamMember::player),
            CodecUtils.enumCodec(ResearchTeamRole.class).fieldOf("role").forGetter(TeamMember::role)
    ).apply(instance, TeamMember::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TeamMember> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            TeamMember::player,
            CodecUtils.enumStreamCodec(ResearchTeamRole.class),
            TeamMember::role,
            TeamMember::new
    );

    public String getName() {
        return AllPlayersCache.getName(this.player);
    }

}
