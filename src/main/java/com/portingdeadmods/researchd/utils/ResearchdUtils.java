package com.portingdeadmods.researchd.utils;

import it.unimi.dsi.fastutil.chars.CharPredicate;

public final class ResearchdUtils {
    public static String trimSpecialCharacterAndConvertToSnake(String input) {
        return toSnakeCase(input, c -> Character.isLetterOrDigit(c) || c == '_');
    }

    public static String toSnakeCase(String input, CharPredicate validChar) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder newString = new StringBuilder(input.length());

        String trimmedInput = input.trim();

        int index = 0;
        while (index < trimmedInput.length()) {
            char c = trimmedInput.charAt(index);
            if (Character.isWhitespace(c) || !validChar.test(c)) {
                while ((Character.isWhitespace(c) || !validChar.test(c)) && index + 1 < trimmedInput.length()) {
                    c = trimmedInput.charAt(++index);
                }
                newString.append('_');
                newString.append(c);
            } else if (Character.isUpperCase(c)) {
                newString.append(Character.toLowerCase(c));
            } else if (validChar.test(c)) {
                newString.append(c);
            }
            index++;

        }

        return newString.toString();
    }

}
