package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A utility class to track the progress of a single research. Has utility methods for client rendering
 */
public class ResearchMethodProgress<T extends ResearchMethod> {
    private final T method;
    private final float maxProgress;
    private float progress;
    private @Nullable ResearchMethodProgress<?> parent;
    private final UUID DEBUG_UUID;

    public T getMethod() {
        return this.method;
    }

    public float getProgress() {
        return this.progress;
    }

    public float getMaxProgress() {
        return this.maxProgress;
    }

    public UUID DEBUG_UUID() {
        return this.DEBUG_UUID;
    }

    public @Nullable ResearchMethodProgress<?> getParent() {
        return this.parent;
    }

    public ResearchMethodProgress<?> setParent(@Nullable ResearchMethodProgress<?> parent) {
        this.parent = parent;
        return this;
    }

    public @NotNull Optional<ResearchMethodProgress<?>> getParentAsOptional() {
        return Optional.ofNullable(this.parent);
    }

    public static final Codec<ResearchMethodProgress<?>> CODEC = Codec.recursive(
            "ResearchMethodProgress",
            self -> RecordCodecBuilder.create(instance -> instance.group(
                    ResearchMethod.CODEC.fieldOf("method").forGetter(ResearchMethodProgress::getMethod),
                    Codec.FLOAT.fieldOf("progress").forGetter(ResearchMethodProgress::getProgress),
                    Codec.FLOAT.fieldOf("max_progress").forGetter(ResearchMethodProgress::getMaxProgress),
                    self.optionalFieldOf("parent").forGetter(ResearchMethodProgress::getParentAsOptional)
            ).apply(instance, ResearchMethodProgress::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchMethodProgress<?>> STREAM_CODEC =
            StreamCodec.recursive(self -> StreamCodec.composite(
                    ResearchMethod.STREAM_CODEC,
                    ResearchMethodProgress::getMethod,
                    ByteBufCodecs.FLOAT,
                    ResearchMethodProgress::getProgress,
                    ByteBufCodecs.FLOAT,
                    ResearchMethodProgress::getMaxProgress,
                    ByteBufCodecs.optional(self),
                    ResearchMethodProgress::getParentAsOptional,
                    ResearchMethodProgress::new
            ));

    /**
     * Creates a new ResearchMethodProgress with 0 progress and max progress of 1.0f.
     */
    public static ResearchMethodProgress<?> one(ResearchMethod method) {
        return ResearchMethodProgress.empty(method, 1.0f);
    }

    /**
     * @param method      {@link ResearchMethod}
     * @param maxProgress The max progress for this method
     * @return A new ResearchMethodProgress with 0 progress
     */
    public static ResearchMethodProgress<?> empty(ResearchMethod method, float maxProgress) {
        return new ResearchMethodProgress<>(method, 0f, maxProgress);
    }

    private static void _backtrackCollect(List<ResearchMethodProgress<?>> list, ResearchMethodProgress<?> node) {
        list.add(node);

        if (node.getMethod() instanceof OrResearchMethod(List<ResearchMethod> methods)) {
            for (ResearchMethod childMethod : methods) {
                _backtrackCollect(list, childMethod.getDefaultProgress().setParent(node));
            }
        } else if (node.getMethod() instanceof AndResearchMethod(List<ResearchMethod> methods)) {
            for (ResearchMethod childMethod : methods) {
                _backtrackCollect(list, childMethod.getDefaultProgress().setParent(node));
            }
        }
    }

    public static List<ResearchMethodProgress<?>> collectFromRoot(ResearchMethod method) {
        List<ResearchMethodProgress<?>> progressList = new ArrayList<>();
        _backtrackCollect(progressList, method.getDefaultProgress());
        return progressList;
    }

    public static List<ResearchMethodProgress<?>> collectFromRoot(ResearchMethodProgress<?> method) {
        List<ResearchMethodProgress<?>> progressList = new ArrayList<>();
        _backtrackCollect(progressList, method);
        return progressList;
    }

    public ResearchMethodProgress(T method, float progress, float maxProgress) {
        this(method, progress, maxProgress, Optional.empty());
    }

    public ResearchMethodProgress(T method, float progress, float maxProgress, Optional<ResearchMethodProgress<?>> parent) {
        this.method = method;
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.parent = parent.orElse(null);
        this.DEBUG_UUID = UUID.randomUUID();
    }

    /**
     * Progresses the research by the given amount.
     * <br> <br>
     * If the progress exceeds the max progress, it will be clamped to the max progress
     * and return true, indicating that the research is complete, returns false otherwise.
     *
     * @param amount
     */
    public boolean progress(float amount) {
        if (this.progress + amount >= this.maxProgress) {
            this.progress = this.maxProgress;

            if (this.parent != null) this.parent.progress(1f);
            return true;
        } else {
            this.progress += amount;
            return false;
        }
    }

    /**
     * Sets the research progress to the given amount.
     * <br> <br>
     * If the progress exceeds the max progress, it will be clamped to the max progress
     * and return true, indicating that the research is complete, returns false otherwise.
     *
     * @param amount
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

    public float getRemainingProgress() {
        return this.maxProgress - this.progress;
    }

    public float getProgressPercent() {
        return this.progress / this.maxProgress;
    }

    public boolean isComplete() {
        return this.progress >= this.maxProgress;
    }

    @Override
    public String toString() {
        return "ResearchMethodProgress[" +
                "method=" + method + ", " +
                "progress=" + progress + "/" + maxProgress + ", " +
                "parent=" + (parent != null ? parent.method : "none") + ", " +
                "DEBUG_UUID=" + DEBUG_UUID +
                ']';
    }
}
