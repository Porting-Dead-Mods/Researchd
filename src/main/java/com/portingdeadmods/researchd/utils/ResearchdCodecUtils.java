package com.portingdeadmods.researchd.utils;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ResearchdCodecUtils {
    public static final Codec<Path> PATH_CODEC = Codec.STRING.xmap(Path::of, Path::toString);
    public static final StreamCodec<ByteBuf, Path> PATH_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Path::of, Path::toString);

    public static <K, V> Map<String, V> encodeMap(Map<K, V> map, Function<K, String> encoder) {
        return map.entrySet().stream()
                .map(e -> Pair.of(encoder.apply(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(Pair::first, Pair::second));
    }

    public static <K, V> Map<String, V> encodeMap(Map<K, V> map) {
        return encodeMap(map, Object::toString);
    }

    public static <K, V> Map<K, V> decodeMap(Map<String, V> map, Function<String, K> decoder) {
        return map.entrySet().stream()
                .map(e -> Pair.of(decoder.apply(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(Pair::first, Pair::second));
    }

}
