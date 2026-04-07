package com.portingdeadmods.researchd.translations;

import com.portingdeadmods.researchd.utils.TextUtils;

public final class NumberUtils {
    public static int parseIntOr(String str, int defaultValue) {
        if (TextUtils.isValidInt(str)) {
            return Integer.parseInt(str);
        }
        return defaultValue;
    }
}
