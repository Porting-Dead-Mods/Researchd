package com.portingdeadmods.researchd.translations;

import com.portingdeadmods.portingdeadlibs.api.translations.TranslatableConstant;
import com.portingdeadmods.researchd.Researchd;

import java.util.HashMap;
import java.util.Map;

public final class ResearchdTranslations {
    public static final Map<String, String> TRANSLATIONS = new HashMap<>();

    public static final class Team {
        public static final TranslatableConstant OWNER = create("role.owner", "Owner");
        public static final TranslatableConstant MEMBER = create("role.member", "Member");
        public static final TranslatableConstant MODERATOR = create("role.moderator", "Moderator");

        private static TranslatableConstant create(String key, String defaultValue) {
            TranslatableConstant constant = create(key);
            TRANSLATIONS.put(constant.key(Researchd.MODID), defaultValue);
            return constant;
        }

        private static TranslatableConstant create(String key) {
            return new TranslatableConstant(key, "team");
        }

        private static void init() {
        }
    }
}
