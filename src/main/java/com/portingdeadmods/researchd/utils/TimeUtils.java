package com.portingdeadmods.researchd.utils;

public class TimeUtils {
    public static class TimeDifference {
        public final int diff;

        public TimeDifference(int startMs, int endMs) {
            diff = endMs - startMs;
        }

        public String getFormatted() {
            float seconds = (diff / 1000f) % 60;
            int minutes = (diff / (1000 * 60)) % 60;
            int hours = (diff / (1000 * 60 * 60)) % 24;
            int days = diff / (1000 * 60 * 60 * 24);

            // Days < Hours < Minutes. Only gets displayed if they are greater than 0
            return days > 0 ? String.format("%02d:%02d:%02d:%.3f", days, hours, minutes, seconds) : (hours > 0 ? String.format("%02d:%02d:%.3f", hours, minutes, seconds) : String.format("%02d:%.3f", minutes, seconds));
        }
    }
}
