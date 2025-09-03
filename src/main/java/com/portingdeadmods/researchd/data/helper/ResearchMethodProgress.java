package com.portingdeadmods.researchd.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodList;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * A utility class to track the progress of a single research. Has utility methods for client rendering
 */
public class ResearchMethodProgress {
	private final ResearchMethod method;
	private final float maxProgress;
	private float progress;
	private @Nullable ResearchMethodProgress parent;
	public final List<ResearchMethodProgress> children;

	public ResearchMethod getMethod() {
		return this.method;
	}

	public float getProgress() {
		return this.progress;
	}

	public float getMaxProgress() {
		return this.maxProgress;
	}

	public @Nullable ResearchMethodProgress getParent() { return this.parent; };

	public @NotNull Optional<ResearchMethodProgress> getParrentAsOptional() { return Optional.ofNullable(this.parent); };

	public List<ResearchMethodProgress> getChildren() { return this.children; }


	public static final Codec<ResearchMethodProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResearchMethod.CODEC.fieldOf("method").forGetter(ResearchMethodProgress::getMethod),
			Codec.FLOAT.fieldOf("progress").forGetter(ResearchMethodProgress::getProgress),
			Codec.FLOAT.fieldOf("max_progress").forGetter(ResearchMethodProgress::getMaxProgress),
			ResearchMethodProgress.CODEC.optionalFieldOf("parent").forGetter(ResearchMethodProgress::getParrentAsOptional),
			ResearchMethodProgress.CODEC.listOf().fieldOf("children").forGetter(ResearchMethodProgress::getChildren)
	).apply(instance, ResearchMethodProgress::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchMethodProgress> STREAM_CODEC = StreamCodec.composite(
			ResearchMethod.STREAM_CODEC,
			ResearchMethodProgress::getMethod,
			ByteBufCodecs.FLOAT,
			ResearchMethodProgress::getProgress,
			ByteBufCodecs.FLOAT,
			ResearchMethodProgress::getMaxProgress,
			ByteBufCodecs.optional(ResearchMethodProgress.STREAM_CODEC),
			ResearchMethodProgress::getParrentAsOptional,
			ResearchMethodProgress.STREAM_CODEC.apply(ByteBufCodecs.list()),
			ResearchMethodProgress::getChildren,
			ResearchMethodProgress::new
	);

	/**
	 * Creates a new ResearchMethodProgress with 0 progress and max progress of 1.0f.
	 */
	public static ResearchMethodProgress one(ResearchMethod method) { return ResearchMethodProgress.empty(method, 1.0f); }

	public static ResearchMethodProgress empty(ResearchMethod method, float maxProgress) {
		return new ResearchMethodProgress(method, 0f, maxProgress);
	}

	public ResearchMethodProgress(ResearchMethod method, float progress, float maxProgress) {
		this.method = method;
		this.progress = progress;
		this.maxProgress = maxProgress;
		this.children = new UniqueArray<>();

		if (method instanceof OrResearchMethod || method instanceof AndResearchMethod) {
			for (ResearchMethod childMethod : ((ResearchMethodList) method).methods()) {
				ResearchMethodProgress childProgress = childMethod.getDefaultProgress();
				childProgress.parent = this;
				this.children.add(childProgress);
			}
		}
	}

	public ResearchMethodProgress(ResearchMethod method, float progress, float maxProgress, @Nullable Optional<ResearchMethodProgress> parent, List<ResearchMethodProgress> children) {
		this.method = method;
		this.progress = progress;
		this.maxProgress = maxProgress;
		this.parent = parent.get();
		this.children = children;
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
}
