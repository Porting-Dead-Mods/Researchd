package com.portingdeadmods.researchd.utils;

import it.unimi.dsi.fastutil.Pair;

public record Search(boolean stripExcessWhitespaces, boolean caseSensitive) {
    public Search() {
        this(true, false);
    }

    public boolean matches(String a, String b) {
        if (a == null || b == null) return false;

        Pair<String, String> preprocessedString = preprocessStrings(a, b);

        return preprocessedString.first().contains(preprocessedString.second());
    }

    public boolean matchesExactly(String a, String b) {
        if (a == null || b == null) return false;

        Pair<String, String> preprocessedString = preprocessStrings(a, b);

        return preprocessedString.first().equals(preprocessedString.second());
    }

    private Pair<String, String> preprocessStrings(String a, String b) {
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

        return Pair.of(finalA, finalB);
    }

}
