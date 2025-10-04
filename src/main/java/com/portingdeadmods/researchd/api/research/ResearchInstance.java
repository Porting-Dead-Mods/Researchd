package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class ResearchInstance {
    public static final Codec<ResearchInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalResearch.CODEC.fieldOf("research").forGetter(ResearchInstance::getResearch),
            ResearchStatus.CODEC.fieldOf("research_status").forGetter(ResearchInstance::getResearchStatus),
            UUIDUtil.CODEC.optionalFieldOf("researched_player").forGetter(r -> Optional.ofNullable(r.getResearchedPlayer())),
            Codec.LONG.fieldOf("researched_time").forGetter(ResearchInstance::getResearchedTime)
    ).apply(instance, (r, s, p, t) -> new ResearchInstance(r, s, p.orElse(null), t)));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchInstance> STREAM_CODEC = StreamCodec.composite(
            GlobalResearch.STREAM_CODEC,
            ResearchInstance::getResearch,
            ResearchStatus.STREAM_CODEC,
            ResearchInstance::getResearchStatus,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
            instance -> Optional.ofNullable(instance.getResearchedPlayer()),
            ByteBufCodecs.VAR_LONG,
            ResearchInstance::getResearchedTime,
            (r, s, p, t) -> new ResearchInstance(r, s, p.orElse(null), t)
    );

    private final GlobalResearch research;
    private ResearchStatus researchStatus;
    private @Nullable UUID researchedPlayer;
    private long researchedTime;

    public ResearchInstance(GlobalResearch research, ResearchStatus researchStatus, UUID researchedPlayer, long researchedTime) {
        this.research = research;
        this.researchStatus = researchStatus;
        this.researchedPlayer = researchedPlayer;
        this.researchedTime = researchedTime;
    }

    public ResearchInstance(GlobalResearch research, ResearchStatus researchStatus) {
        this(research, researchStatus, null, -1);
    }

    public boolean isResearched() {
        return this.researchStatus == ResearchStatus.RESEARCHED;
    }

    public boolean isResearchable() {
        return this.researchStatus == ResearchStatus.RESEARCHABLE || this.researchStatus == ResearchStatus.RESEARCHABLE_AFTER_QUEUE;
    }

    public boolean isLocked() {
        return this.researchStatus == ResearchStatus.LOCKED;
    }

    public ResearchInstance withResearch(GlobalResearch research) {
        return new ResearchInstance(research, researchStatus, researchedPlayer, researchedTime);
    }

    public Component getDisplayName() {
        return Utils.registryTranslation(this.getKey());
    }

    public Component getDisplayName(Level level) {
        Research r = this.research.getResearch(level);
        if (r instanceof SimpleResearch simple) {
            if (simple.literalName().isPresent()) {
                return Component.literal(simple.literalName().get());
            }
        }
        return Utils.registryTranslation(this.getKey());
    }

    public Component getDescription(Level level) {
        Research r = this.research.getResearch(level);
        if (r instanceof SimpleResearch simple) {
            if (simple.literalDescription().isPresent()) {
                return Component.literal(simple.literalDescription().get());
            }
        }
        ResourceKey<Research> key = this.getKey();
        return Component.translatable("research." + key.location().getNamespace() + "." + key.location().getPath() + ".description");
    }

    public GlobalResearch getResearch() {
        return research;
    }

    public ResourceKey<Research> getKey() {
        return this.research.getResearchKey();
    }

    public ResearchStatus getResearchStatus() {
        return researchStatus;
    }

    public ResearchInstance setResearchStatus(ResearchStatus researchStatus) {
        this.researchStatus = researchStatus;
        return this;
    }

    public @Nullable UUID getResearchedPlayer() {
        return researchedPlayer;
    }

    public ResearchInstance setResearchedPlayer(@Nullable UUID researchedPlayer) {
        this.researchedPlayer = researchedPlayer;
        return this;
    }

    public long getResearchedTime() {
        return researchedTime;
    }

    public ResearchInstance setResearchedTime(long researchedTime) {
        this.researchedTime = researchedTime;
        return this;
    }

    public Research lookup(Level level) {
        return ResearchHelperCommon.getResearch(this.research.getResearchKey(), level);
    }

    public Set<GlobalResearch> getChildren() {
        return this.research.getChildren();
    }

    public Set<GlobalResearch> getParents() {
        return this.research.getParents();
    }

    public boolean is(ResearchInstance instance) {
        return this.is(instance.getResearch());
    }

    public boolean is(GlobalResearch research) {
        return this.is(research.getResearchKey());
    }

    public boolean is(ResourceKey<Research> key) {
        return this.research.is(key);
    }

    public ResearchInstance copy() {
        return new ResearchInstance(this.getResearch(), this.getResearchStatus(), this.getResearchedPlayer(), this.getResearchedTime());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResearchInstance instance)) return false;
        return researchedTime == instance.researchedTime && Objects.equals(research, instance.research) && researchStatus == instance.researchStatus && Objects.equals(researchedPlayer, instance.researchedPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(research, researchStatus, researchedPlayer, researchedTime);
    }

    @Override
    public String toString() {
        return "ResearchInstance{" +
                "research=" + research +
                ", researchStatus=" + researchStatus +
                '}';
    }
}
