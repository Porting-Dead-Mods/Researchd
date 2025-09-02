package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A utility class to track the progress of a single research. Has utility methods for client rendering
 */
public class ResearchMethodProgress {
	private final ResearchMethod method;

	private final float maxProgress;
	private float progress;

	public ResearchMethod getMethod() {
		return this.method;
	}

	public final boolean hasChildren;
	public final @Nullable List<ResearchMethodProgress> children;

	public float getProgress() {
		return this.progress;
	}

	public float getMaxProgress() {
		return this.maxProgress;
	}

	public static final Codec<ResearchMethodProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResearchMethod.CODEC.fieldOf("method").forGetter(ResearchMethodProgress::getMethod),
			Codec.FLOAT.fieldOf("progress").forGetter(ResearchMethodProgress::getProgress),
			Codec.FLOAT.fieldOf("max_progress").forGetter(ResearchMethodProgress::getMaxProgress)
	).apply(instance, ResearchMethodProgress::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchMethodProgress> STREAM_CODEC = StreamCodec.composite(
			ResearchMethod.STREAM_CODEC,
			ResearchMethodProgress::getMethod,
			ByteBufCodecs.FLOAT,
			ResearchMethodProgress::getProgress,
			ByteBufCodecs.FLOAT,
			ResearchMethodProgress::getMaxProgress,
			ResearchMethodProgress::new
	);

	/**
	 * Creates a new ResearchMethodProgress with 0 progress and max progress of 1.0f.
	 */
	public static ResearchMethodProgress one(ResearchMethod methodId) { return new ResearchMethodProgress(methodId, 1.0f); }

	public ResearchMethodProgress(ResearchMethod method, float maxProgress) {
		if (!(method instanceof OrResearchMethod) && !(method instanceof AndResearchMethod)) {
			this.children = null;
			this.hasChildren = false;
		} else {
			this.children = new UniqueArray<>();
			this.hasChildren = true;
		}

		this.method = method;
		this.progress = 0f;
		this.maxProgress = maxProgress;
	}

	public ResearchMethodProgress(ResearchMethod method, float progress, float maxProgress) {
		if (!(method instanceof OrResearchMethod) && !(method instanceof AndResearchMethod)) {
			this.children = null;
			this.hasChildren = false;
		} else {
			this.children = new UniqueArray<>();
			this.hasChildren = true;
		}

		this.method = method;
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
