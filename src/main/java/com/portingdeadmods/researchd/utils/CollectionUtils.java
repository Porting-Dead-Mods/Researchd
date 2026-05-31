package com.portingdeadmods.researchd.utils;

import java.util.HashMap;
import java.util.Map;

public final class CollectionUtils {
    public static <K, V> Map<K, V> newMap(int capacity) {
        return new HashMap<>(capacity);
    }
}
