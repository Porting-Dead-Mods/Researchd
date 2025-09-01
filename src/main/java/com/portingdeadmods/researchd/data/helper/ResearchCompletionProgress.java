package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A utility class to track the progress of a single research. Has utility methods for client rendering
 */
public class ResearchCompletionProgress {
	private final ResourceLocation methodId;

	private final float maxProgress;
	private float progress;

	public ResourceLocation getMethodId() {
		return this.methodId;
	}

	public final boolean hasChildren;
	public final @Nullable List<ResearchCompletionProgress> children;

	public float getProgress() {
		return this.progress;
	}

	public float getMaxProgress() {
		return this.maxProgress;
	}

	public static final Codec<ResearchCompletionProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("method").forGetter(ResearchCompletionProgress::getMethodId),
			Codec.FLOAT.fieldOf("progress").forGetter(ResearchCompletionProgress::getProgress),
			Codec.FLOAT.fieldOf("max_progress").forGetter(ResearchCompletionProgress::getMaxProgress)
	).apply(instance, ResearchCompletionProgress::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchCompletionProgress> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC,
			ResearchCompletionProgress::getMethodId,
			ByteBufCodecs.FLOAT,
			ResearchCompletionProgress::getProgress,
			ByteBufCodecs.FLOAT,
			ResearchCompletionProgress::getMaxProgress,
			ResearchCompletionProgress::new
	);

	/**
	 * Creates a new ResearchCompletionProgress with 0 progress and max progress of 1.0f.
	 */
	public static ResearchCompletionProgress one(ResourceLocation methodId) { return new ResearchCompletionProgress(methodId, 1.0f); }

	public ResearchCompletionProgress(ResourceLocation methodId, float maxProgress) {
		if (methodId != OrResearchMethod.ID && methodId != AndResearchMethod.ID) {
			this.children = null;
			this.hasChildren = false;
		} else {
			this.children = new UniqueArray<>();
			this.hasChildren = true;
		}

		this.methodId = methodId;
		this.progress = 0f;
		this.maxProgress = maxProgress;
	}

	public ResearchCompletionProgress(ResourceLocation methodId, float progress, float maxProgress) {
		if (methodId != OrResearchMethod.ID && methodId != AndResearchMethod.ID) {
			this.children = null;
			this.hasChildren = false;
		} else {
			this.children = new UniqueArray<>();
			this.hasChildren = true;
		}

		this.methodId = methodId;
		this.progress = progress;
		this.maxProgress = maxProgress;
	}

	/**
	 * Progresses the research by the given amount.
	 * <br> <br>
	 * If the progress exceeds the max progress, it will be clamped to the max progress
	 * and return true, indicating that the research is complete, returns false otherwise.
	 * @param amount
	 */
	public boolean progress(float amount) {
		if (this.progress > this.maxProgress) {
			this.progress = this.maxProgress;
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

	public float getProgressPercent() {
		return this.progress / this.maxProgress;
	}

	public boolean isComplete() {
		return this.progress >= this.maxProgress;
	}

}
