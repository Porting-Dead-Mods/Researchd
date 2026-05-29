package com.portingdeadmods.researchd.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

// TODO: Remove research progresses for removed researches
public record ResearchProgress(List<Task> tasks, Type type) {
    public static final Codec<ResearchProgress> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Task.CODEC.listOf().fieldOf("tasks").forGetter(ResearchProgress::tasks),
            Type.CODEC.fieldOf("type").forGetter(ResearchProgress::type)
    ).apply(inst, ResearchProgress::new));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchProgress> STREAM_CODEC = StreamCodec.composite(
            Task.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ResearchProgress::tasks,
            Type.STREAM_CODEC,
            ResearchProgress::type,
            ResearchProgress::new
    );

    public boolean isComplete() {
        return this.type.isComplete(this.tasks);
    }

    public float getProgress() {
        return this.type.getProgress(this.tasks);
    }

    public float getMaxProgress() {
        return this.type.getMaxProgress(this.tasks);
    }

    public void checkProgress(ResourceKey<Research> research, Level level, ResearchMethod.MethodContext context) {
        this.type.checkProgress(research, level, this.tasks, context);
    }

    public @Nullable Task getTask(ResearchMethod method) {
        for (Task task : this.tasks) {
            if (task.method.equals(method)) {
                return task;
            }
        }
        return null;
    }

    public static ResearchProgress forResearch(ResourceKey<Research> key, Level level) {
        Research research = ResearchdApi.getResearchManager().lookupResearch(key, level);
        ResearchMethod method = research.researchMethod();
        return method.createProgress();
    }

    public static ResearchProgress single(ResearchMethod method) {
        return new ResearchProgress(Collections.singletonList(new Task(method)), Type.SINGLE);
    }

    public static ResearchProgress and(List<ResearchMethod> methods) {
        return new ResearchProgress(methods.stream().map(Task::new).toList(), Type.AND);
    }

    public static ResearchProgress or(List<ResearchMethod> methods) {
        return new ResearchProgress(methods.stream().map(Task::new).toList(), Type.OR);
    }

    public enum Type implements StringRepresentable {
        OR("or"),
        AND("and"),
        SINGLE("single");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        public static final StreamCodec<? super RegistryFriendlyByteBuf, Type> STREAM_CODEC = CodecUtils.enumStreamCodec(Type.class);

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public void checkProgress(ResourceKey<Research> research, Level level, List<Task> tasks, ResearchMethod.MethodContext context) {
            switch (this) {
                case AND, OR -> {
                    for (Task task : tasks) {
                        task.checkProgress(research, level, context);
                    }
                }
                case SINGLE -> tasks.getFirst().checkProgress(research, level, context);
            }
        }

        public boolean isComplete(List<Task> tasks) {
            return switch (this) {
                case OR -> tasks.stream().anyMatch(Task::isComplete);
                case AND -> tasks.stream().allMatch(Task::isComplete);
                case SINGLE -> tasks.getFirst().isComplete();
            };
        }

        public float getProgress(List<Task> tasks) {
            return (float) switch (this) {
                case OR -> this.getProgresses(tasks).firstDouble();
                case AND -> tasks.stream().mapToDouble(Task::getProgress).sum();
                case SINGLE -> tasks.getFirst().getProgress();
            };
        }

        public float getMaxProgress(List<Task> tasks) {
            return (float) switch (this) {
                case OR -> this.getProgresses(tasks).secondDouble();
                case AND -> tasks.stream().mapToDouble(Task::getMaxProgress).sum();
                case SINGLE -> tasks.getFirst().getMaxProgress();
            };
        }

        private DoubleDoublePair getProgresses(List<Task> tasks) {
            double curProgress = 0;
            double curMaxProgress = 0;
            for (Task task : tasks) {
                if (curMaxProgress == 0 || (task.getProgress() / task.getMaxProgress() > curProgress / curMaxProgress)) {
                    curProgress = task.getProgress();
                    curMaxProgress = task.getMaxProgress();
                }
            }
            return DoubleDoublePair.of(curProgress, curMaxProgress);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

    }

    // TODO: Check if encoded method still exists
    public static class Task {
        public static final Codec<Task> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResearchMethod.CODEC.fieldOf("method").forGetter(Task::getMethod),
                Codec.FLOAT.fieldOf("progress").forGetter(Task::getProgress),
                Codec.FLOAT.fieldOf("max_progress").forGetter(Task::getMaxProgress)
        ).apply(inst, Task::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Task> STREAM_CODEC = StreamCodec.composite(
                ResearchMethod.STREAM_CODEC,
                Task::getMethod,
                ByteBufCodecs.FLOAT,
                Task::getProgress,
                ByteBufCodecs.FLOAT,
                Task::getMaxProgress,
                Task::new
        );
        private final ResearchMethod method;
        private float progress;
        private final float maxProgress;

        public Task(ResearchMethod method) {
            this(method, 0, method.getMaxProgress());
        }

        public Task(ResearchMethod method, float maxProgress) {
            this(method, 0, maxProgress);
        }

        public Task(ResearchMethod method, float progress, float maxProgress) {
            this.method = method;
            this.progress = progress;
            this.maxProgress = maxProgress;
        }

        public ResearchMethod getMethod() {
            return method;
        }

        public float getProgress() {
            return progress;
        }

        public float getMaxProgress() {
            return maxProgress;
        }

        public boolean isComplete() {
            return this.progress >= this.maxProgress;
        }

        public void addProgress(float progress) {
            if (this.progress + progress > this.maxProgress) {
                this.progress = this.maxProgress;
            } else {
                this.progress += progress;
            }
        }

        public void checkProgress(ResourceKey<Research> research, Level level, ResearchMethod.MethodContext context) {
            if (!this.isComplete()) {
                this.method.checkProgress(level, research, this, context);
            }
        }

    }
}
