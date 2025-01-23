package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.utils.Codecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public final class ResearchInstance {
    public static final Codec<ResearchInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Research.RESOURCE_KEY_CODEC.fieldOf("research").forGetter(ResearchInstance::getResearch),
            Codecs.enumCodec(ResearchStatus.class).fieldOf("research_status").forGetter(ResearchInstance::getResearchStatus)
    ).apply(instance, ResearchInstance::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchInstance> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC,
            ResearchInstance::getResearch,
            Codecs.enumStreamCodec(ResearchStatus.class),
            ResearchInstance::getResearchStatus,
            ResearchInstance::new
    );

    private final ResourceKey<Research> research;
    private ResearchStatus researchStatus;

    public ResearchInstance(ResourceKey<Research> research, ResearchStatus researchStatus) {
        this.research = research;
        this.researchStatus = researchStatus;
    }

    public ResourceKey<Research> getResearch() {
        return research;
    }

    public ResearchStatus getResearchStatus() {
        return researchStatus;
    }

    public void setResearchStatus(ResearchStatus researchStatus) {
        this.researchStatus = researchStatus;
    }

    public ResearchInstance copy() {
        return new ResearchInstance(getResearch(), ResearchStatus.values()[getResearchStatus().ordinal()]);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResearchInstance that)) return false;
        return Objects.equals(research, that.research);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(research);
    }

    @Override
    public String toString() {
        return "ResearchInstance{" +
                "research=" + research +
                ", researchStatus=" + researchStatus +
                '}';
    }
}
