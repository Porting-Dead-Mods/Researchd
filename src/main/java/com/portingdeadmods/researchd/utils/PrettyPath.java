package com.portingdeadmods.researchd.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public record PrettyPath(Path fullPath, Path shortPath) {
    public static final Codec<PrettyPath> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResearchdCodecUtils.PATH_CODEC.fieldOf("full_path").forGetter(PrettyPath::fullPath),
            ResearchdCodecUtils.PATH_CODEC.fieldOf("short_path").forGetter(PrettyPath::shortPath)
    ).apply(inst, PrettyPath::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PrettyPath> STREAM_CODEC = StreamCodec.composite(
            ResearchdCodecUtils.PATH_STREAM_CODEC,
            PrettyPath::fullPath,
            ResearchdCodecUtils.PATH_STREAM_CODEC,
            PrettyPath::shortPath,
            PrettyPath::new
    );

    @Override
    public @NotNull String toString() {
        return this.fullPath().toString();
    }
}
