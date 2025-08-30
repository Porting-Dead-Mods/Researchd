package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ResearchInstance {
    public static final Codec<ResearchInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Research.RESOURCE_KEY_CODEC.fieldOf("research").forGetter(ResearchInstance::getResearch),
            CodecUtils.enumCodec(ResearchStatus.class).fieldOf("research_status").forGetter(ResearchInstance::getResearchStatus),
            UUIDUtil.CODEC.optionalFieldOf("researched_player").forGetter(r -> Optional.ofNullable(r.getResearchedPlayer())),
            Codec.LONG.fieldOf("researched_time").forGetter(ResearchInstance::getResearchedTime)
    ).apply(instance, (r, s, p, t) -> new ResearchInstance(r, s, p.orElse(null), t)));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchInstance> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC,
            ResearchInstance::getResearch,
            CodecUtils.enumStreamCodec(ResearchStatus.class),
            ResearchInstance::getResearchStatus,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
            instance -> Optional.ofNullable(instance.getResearchedPlayer()),
            ByteBufCodecs.VAR_LONG,
            ResearchInstance::getResearchedTime,
            (r, s, p, t) -> new ResearchInstance(r, s, p.orElse(null), t)
    );

    private final ResourceKey<Research> research;
    private final Set<ResearchInstance> parents;
    private final Set<ResearchInstance> children;
    private ResearchStatus researchStatus;
    private @Nullable UUID researchedPlayer;
    private long researchedTime;

    private ResearchInstance(ResourceKey<Research> research, ResearchStatus researchStatus, UUID researchedPlayer, long researchedTime) {
        this.research = research;
        this.researchStatus = researchStatus;
        this.researchedPlayer = researchedPlayer;
        this.researchedTime = researchedTime;
        this.parents = new HashSet<>();
        this.children = new HashSet<>();
    }

    public ResearchInstance(ResourceKey<Research> research, ResearchStatus researchStatus) {
        this(research, researchStatus, null, -1);
    }

    public ResourceKey<Research> getResearch() {
        return research;
    }

    public ResearchStatus getResearchStatus() {
        return researchStatus;
    }

    public Set<ResearchInstance> getParents() {
        return parents;
    }

    public Set<ResearchInstance> getChildren() {
        return children;
    }

    public void setResearchStatus(ResearchStatus researchStatus) {
        this.researchStatus = researchStatus;
    }

    public @Nullable UUID getResearchedPlayer() {
        return researchedPlayer;
    }

    public void setResearchedPlayer(@Nullable UUID researchedPlayer) {
        this.researchedPlayer = researchedPlayer;
    }

    public long getResearchedTime() {
        return researchedTime;
    }

    public void setResearchedTime(long researchedTime) {
        this.researchedTime = researchedTime;
    }

    public ResearchInstance copy() {
        return new ResearchInstance(getResearch(), ResearchStatus.values()[getResearchStatus().ordinal()]);
    }

    // TODO: might want to compare the research time idk
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResearchInstance instance)) return false;
        return Objects.equals(research, instance.research) && researchStatus == instance.researchStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(research, researchStatus);
    }

    @Override
    public String toString() {
        return "ResearchInstance{" +
                "research=" + research +
                ", researchStatus=" + researchStatus +
                '}';
    }
}
