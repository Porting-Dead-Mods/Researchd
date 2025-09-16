package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * A utility class to track the progress of a single research. Has utility methods for client rendering
 */
public class ResearchMethodProgress<T extends ResearchMethod> {
    public static final Codec<ResearchMethodProgress<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResearchMethod.CODEC.fieldOf("method").forGetter(ResearchMethodProgress::getMethod),
            Codec.FLOAT.fieldOf("progress").forGetter(ResearchMethodProgress::getProgress),
            Codec.FLOAT.fieldOf("max_progress").forGetter(ResearchMethodProgress::getMaxProgress)
    ).apply(instance, ResearchMethodProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchMethodProgress<?>> STREAM_CODEC = StreamCodec.composite(
            ResearchMethod.STREAM_CODEC,
            ResearchMethodProgress::getMethod,
            ByteBufCodecs.FLOAT,
            ResearchMethodProgress::getProgress,
            ByteBufCodecs.FLOAT,
            ResearchMethodProgress::getMaxProgress,
            ResearchMethodProgress::new
    );

    private final T method;
    private final float maxProgress;
    private float progress;

    public static ResearchMethodProgress<?> fromResearch(HolderLookup.Provider lookup, ResourceKey<Research> key) {
        return new ResearchMethodProgress<>(ResearchHelperCommon.getResearch(key, lookup).researchMethod());
    }

    public ResearchMethodProgress(T method) {
        this(method, 0f, method.getMaxProgress());
    }

    public ResearchMethodProgress(T method, float progress, float maxProgress) {
        this.method = method;
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    public void checkProgress(Level level, ResearchTeam team, ResourceKey<Research> research) {
        if (this.getMethod().shouldCheckProgress()) {
            this.getMethod().checkProgress(level, research, this, new ResearchMethod.SimpleMethodContext(team, null));
        }
    }

    public T getMethod() {
        return this.method;
    }

    public float getProgress() {
        return this.progress;
    }

    public float getMaxProgress() {
        return this.maxProgress;
    }

    /**
     * Progresses the research by the given amount.
     * <br>
     * If the progress exceeds the max progress, it will be clamped to the max progress
     * and return true, indicating that the research is complete, returns false otherwise.
     */
    public boolean addProgress(float amount) {
        if (this.progress + amount >= this.maxProgress) {
            this.progress = this.maxProgress;
            return true;
        } else {
            this.progress += amount;
            return false;
        }
    }

    /**
     * Sets the research progress to the given amount.
     * <br>
     * If the progress exceeds the max progress, it will be clamped to the max progress
     * and return true, indicating that the research is complete, returns false otherwise.
     */
    public boolean setProgress(float amount) {
        if (amount > this.maxProgress) {
            this.progress = this.maxProgress;
            return true;
        } else {
            this.progress = amount;
            return false;
        }
    }

    public boolean isComplete() {
        return this.progress >= this.maxProgress;
    }

    public float getRemainingProgress() {
        return this.maxProgress - this.progress;
    }

    public float getProgressPercent() {
        return this.progress / this.maxProgress;
    }

}
