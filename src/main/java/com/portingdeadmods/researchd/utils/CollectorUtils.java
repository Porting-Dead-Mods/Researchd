package com.portingdeadmods.researchd.utils;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorUtils {
    // TODO: Move to pdl
    public static <K, V> Collector<? extends Map.Entry<K, V>, ?, Map<K, V>> toMapFromEntry() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
