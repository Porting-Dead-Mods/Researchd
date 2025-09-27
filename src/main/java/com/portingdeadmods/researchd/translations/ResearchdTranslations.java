package com.portingdeadmods.researchd.translations;

import com.portingdeadmods.portingdeadlibs.api.translations.TranslatableConstant;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.network.chat.MutableComponent;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.ChatFormatting.*;

public final class ResearchdTranslations {
    public static final Map<String, String> TRANSLATIONS = new HashMap<>();

	public static final String AQUA_AT = AQUA + "@: " + RESET;
	public static final String STR_PARAM = YELLOW + "%s" + RESET;

    public static final class Team {
        public static final TranslatableConstant OWNER = create("role.owner", "Owner");
        public static final TranslatableConstant MEMBER = create("role.player", "Member");
        public static final TranslatableConstant NOT_MEMBER = create("role.not_member", "Not Member");
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

		public static final TranslatableConstant SENT_INVITE = create("sent_invite", AQUA_AT + GREEN + "You invited " + RESET + STR_PARAM + GREEN + " to join " + RESET + STR_PARAM + GREEN + "!" + RESET);
		public static final TranslatableConstant REMOVED_INVITE = create("removed_invite", AQUA_AT + GREEN + "Invite to " + RESET + STR_PARAM + GREEN + " has been removed!" + RESET);
		public static final TranslatableConstant RECEIVED_INVITE = create("received_invite", AQUA_AT + GREEN + "You were invited to join: " + RESET + STR_PARAM);
	    public static final TranslatableConstant YOU_JOINED_TEAM = create("joined_team",  AQUA_AT + STR_PARAM + GREEN + "You joined: " + RESET + STR_PARAM + GREEN + "!" + RESET);
		public static final TranslatableConstant PLAYER_JOINED_TEAM = create("joined_team",  AQUA_AT + STR_PARAM + GREEN + " joined your team!" + RESET);
		public static final TranslatableConstant LEFT_TEAM = create("left_team", GREEN.toString() + BOLD + "You successfully abandoned your team!" + RESET);
		public static final TranslatableConstant ACCEPT = create("accept", GREEN.toString() + BOLD + UNDERLINE + "ACCEPT" + RESET);
		public static final TranslatableConstant DECLINE = create("decline", RED.toString() + BOLD + UNDERLINE + "DECLINE" + RESET);
		public static final TranslatableConstant PROMOTED = create("promoted", AQUA_AT + STR_PARAM + AQUA + " has been promoted to Moderator!" + RESET);
		public static final TranslatableConstant DEMOTED = create("demoted", AQUA_AT + STR_PARAM + AQUA + " has been demoted to Member!" + RESET);
		public static final TranslatableConstant REMOVED = create("removed", AQUA_AT + STR_PARAM + AQUA + " has been removed from the team!" + RESET);
		public static final TranslatableConstant TRANSFERRED_OWNERSHIP = create("transferred_ownership", AQUA_AT + STR_PARAM + AQUA + " is the new team owner!" + RESET);
	    public static final TranslatableConstant IGNORE = create("ignore", AQUA_AT + AQUA + "Ignoring invites from " + STR_PARAM + AQUA + " from now on!" + RESET);
		public static final TranslatableConstant NEW_TEAM_NAME = create("new_team_name", AQUA_AT + STR_PARAM + AQUA + " changed to " + RESET + STR_PARAM + RESET);
		public static final TranslatableConstant ALREADY_IN_TEAM = create("already_in_team", BOLD.toString() + RED + "You gotta abandon your team first..." + RESET);
		public static final TranslatableConstant NO_NEXT_LEADER = create("no_next_leader", BOLD.toString() + RED + "You gotta specify the next leader..." + RESET);
		public static final TranslatableConstant NO_PERMS = create("no_perms", BOLD.toString() + RED + "You don't have the permissions to do that..." + RESET);

		public static final TranslatableConstant BAD_INPUT = create("bad_input", BOLD.toString() + RED + "Invalid input... how even..." + RESET);

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

        public static final TranslatableConstant RECIPE_UNLOCK_EFFECT_TOOLTIP_NO_ARG = create("effects.tooltip.recipe_unlock_no_arg", "Unlocks Recipe: ");
        public static final TranslatableConstant RECIPE_UNLOCK_EFFECT_TOOLTIP = create("effects.tooltip.recipe_unlock", "Unlocks Recipe: %s");
        public static final TranslatableConstant START_RESEARCH_BUTTON = create("screen.button.start_research", "Start");
        public static final TranslatableConstant ENQUEUE_RESEARCH_BUTTON = create("screen.button.enqueue_research", "Enqueue");

        private static TranslatableConstant create(String key, String defaultValue) {
            TranslatableConstant constant = new TranslatableConstant(key, "research");
            TRANSLATIONS.put(constant.key(Researchd.MODID), defaultValue);
            return constant;
        }

        private static void init() {
        }
    }

    public static final class Errors {
        public static final TranslatableConstant RESEARCH_QUEUE_DESYNC = create("research_queue_desync", "A small desynchronization happened regarding the Research Queue, please relog.");
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

    public static final class Gui {
        public static final TranslatableConstant RESEARCHED_BY_ON = create("researched_by_on", "Researched by %s on %s");

        private static TranslatableConstant create(String key, String defaultValue) {
            TranslatableConstant constant = new TranslatableConstant(key, "gui");
            TRANSLATIONS.put(constant.key(Researchd.MODID), defaultValue);
            return constant;
        }

        private static void init() {
        }
    }

    public static void init() {
        Team.init();
        Research.init();
        Errors.init();
        Gui.init();
    }
}
