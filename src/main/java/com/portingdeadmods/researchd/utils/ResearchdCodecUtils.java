package com.portingdeadmods.researchd.utils;

import it.unimi.dsi.fastutil.Pair;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ResearchdCodecUtils {
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
