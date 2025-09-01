package com.portingdeadmods.researchd.translations;

import com.portingdeadmods.portingdeadlibs.api.translations.TranslatableConstant;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.network.chat.MutableComponent;

import java.util.HashMap;
import java.util.Map;

public final class ResearchdTranslations {
    public static final Map<String, String> TRANSLATIONS = new HashMap<>();

    public static final class Team {
        public static final TranslatableConstant OWNER = create("role.owner", "Owner");
        public static final TranslatableConstant MEMBER = create("role.player", "Member");
        public static final TranslatableConstant MODERATOR = create("role.moderator", "Moderator");

        public static final TranslatableConstant SCREEN_TITLE = create("screen.title", "Research Team");
        public static final TranslatableConstant SETTINGS_SCREEN_TITLE = create("screen.settings.title", "Team Settings");
        public static final TranslatableConstant BUTTON_INVITE = create("buttons.invite", "Invite Player");
        public static final TranslatableConstant BUTTON_TEAM_SETTINGS = create("buttons.team_settings", "Team Settings");

        public static final TranslatableConstant BUTTON_MANAGE_MEMBERS = create("buttons.manage_members", "Manage Members");
        public static final TranslatableConstant BUTTON_TRANSFER_OWNERSHIP = create("buttons.transfer_ownership", "Transfer Ownership");
        public static final TranslatableConstant BUTTON_LEAVE_TEAM = create("buttons.leave_team", "Leave Team");

        public static final TranslatableConstant TITLE_MEMBERS = create("titles.members", "Members");
        public static final TranslatableConstant TITLE_RECENTLY_RESEARCHED = create("titles.recently_researched", "Recently Researched");

        private static TranslatableConstant create(String key, String defaultValue) {
            TranslatableConstant constant = new TranslatableConstant(key, "team");
            TRANSLATIONS.put(constant.key(Researchd.MODID), defaultValue);
            return constant;
        }

        private static void init() {
        }
    }

    public static final class Research {
        public static final TranslatableConstant SCREEN_TITLE = create("screen.title", "Researches");

        public static final TranslatableConstant QUEUE_ADDED = create("queue.added", "%s added %s to the research queue!");
        public static final TranslatableConstant QUEUE_FINISHED = create("queue.finished", "%s finished researching (%s)!");

        public static final TranslatableConstant SCREEN_LABEL_RESEARCH_COST = create("screen.label.researched_by", "Cost");
        public static final TranslatableConstant SCREEN_LABEL_RESEARCH_EFFECTS = create("screen.label.effects", "Effects");

        public static final TranslatableConstant DIMENSION_UNLOCK_EFFECT_TOOLTIP = create("effects.tooltip.dimension_unlock", "Unlocks Dimension: %s");

        private static TranslatableConstant create(String key, String defaultValue) {
            TranslatableConstant constant = new TranslatableConstant(key, "research");
            TRANSLATIONS.put(constant.key(Researchd.MODID), defaultValue);
            return constant;
        }

        private static void init() {
        }
    }

    public static final class Errors {
        public static final TranslatableConstant RESEARCH_QUEUE_DESYNC = create("research_queue_desync", "Small desync happened, please relog. A research complete packet was emitted but your queue was empty");
        public static final TranslatableConstant NO_RESEARCH_TEAM = create("no_research_team", "Research related packet handled to player lacking a team");

        private static TranslatableConstant create(String key, String defaultValue) {
            TranslatableConstant constant = new TranslatableConstant(key, "error");
            TRANSLATIONS.put(constant.key(Researchd.MODID), defaultValue);
            return constant;
        }

        private static void init() {
        }
    }

    public static MutableComponent component(TranslatableConstant constant, Object... args) {
        return constant.component(Researchd.MODID, args);
    }

    public static void init() {
        Team.init();
        Research.init();
        Errors.init();
    }
}
