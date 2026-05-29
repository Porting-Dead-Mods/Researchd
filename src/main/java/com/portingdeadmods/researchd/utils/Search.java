package com.portingdeadmods.researchd.utils;

public record Search(boolean stripExcessWhitespaces, boolean caseSensitive) {
    public Search() {
        this(true, false);
    }

    public boolean matches(String a, String b) {
        if (a == null || b == null) return false;

        a = preprocessString(a);
        b = preprocessString(b);

        return a.contains(b);
    }

    public boolean matchesExactly(String a, String b) {
        if (a == null || b == null) return false;

        a = preprocessString(a);
        b = preprocessString(b);

        return a.equals(b);
    }

    private String preprocessString(String string) {
        if (!this.caseSensitive()) {
            string = string.toLowerCase();
        }

        if (this.stripExcessWhitespaces()) {
            string = string.strip();
        }

        return string;
    }

}
