package com.portingdeadmods.researchd.utils;

public record Search(boolean stripExcessWhitespaces, boolean caseSensitive) {
    public Search() {
        this(true, false);
    }

    public boolean matches(String a, String b) {
        if (a == null || b == null) return false;

        String finalA = a;
        String finalB = b;
        if (!this.caseSensitive()) {
            finalA = finalA.toLowerCase();
            finalB = finalB.toLowerCase();
        }

        if (this.stripExcessWhitespaces()) {
            finalA = finalA.strip();
            finalB = finalB.strip();
        }

        return finalA.contains(finalB);
    }
}
