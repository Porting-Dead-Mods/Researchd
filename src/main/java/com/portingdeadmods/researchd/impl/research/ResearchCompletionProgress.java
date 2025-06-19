package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * An utility class to track the progress of a single research. Has utility methods for client rendering
 */
public class ResearchCompletionProgress {
	private float progress;
	private float maxProgress;

	public float getProgress() {
		return this.progress;
	}

	public float getMaxProgress() {
		return this.maxProgress;
	}

	public static final Codec<ResearchCompletionProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("progress").forGetter(ResearchCompletionProgress::getProgress),
			Codec.FLOAT.fieldOf("max_progress").forGetter(ResearchCompletionProgress::getMaxProgress)
	).apply(instance, ResearchCompletionProgress::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchCompletionProgress> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			ResearchCompletionProgress::getProgress,
			ByteBufCodecs.FLOAT,
			ResearchCompletionProgress::getMaxProgress,
			ResearchCompletionProgress::new
	);


	/**
	 * Creates a new ResearchCompletionProgress with 0 progress and max progress of 1.0f.
	 */
	public static ResearchCompletionProgress ONE() { return new ResearchCompletionProgress(0f, 1.0f); }

	public ResearchCompletionProgress(float maxProgress) {
		this.progress = 0f;
		this.maxProgress = maxProgress;
	}

	public ResearchCompletionProgress(float progress, float maxProgress) {
		this.progress = progress;
		this.maxProgress = maxProgress;
	}


	/**
	 * Progresses the research by the given amount.
	 *
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

	public float getProgressPercent() {
		return this.progress / this.maxProgress;
	}

	public boolean isComplete() {
		return this.progress >= this.maxProgress;
	}
}
