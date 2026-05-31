package com.portingdeadmods.researchd.utils;

public final class NumberUtils {
    public static int parseIntOr(String str, int defaultValue) {
        if (TextUtils.isValidInt(str)) {
            return Integer.parseInt(str);
        }
        return defaultValue;
    }

    public static String getTimeDifferenceFormatted(long startMs, long endMs) {
        long diff = endMs - startMs;
        float seconds = (diff / 1000f) % 60;
        int minutes = Math.toIntExact((diff / (1000 * 60)) % 60);
        int hours = Math.toIntExact((diff / (1000 * 60 * 60)) % 24);
        int days = Math.toIntExact(diff / (1000 * 60 * 60 * 24));

        // Days < Hours < Minutes. Only gets displayed if they are greater than 0
        return days > 0 ? String.format("%02d:%02d:%02d:%.3f", days, hours, minutes, seconds) : (hours > 0 ? String.format("%02d:%02d:%.3f", hours, minutes, seconds) : String.format("%02d:%.3f", minutes, seconds));
    }

}
