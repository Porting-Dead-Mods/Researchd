package com.portingdeadmods.researchd.data.helper;

import net.minecraft.network.chat.Component;

public enum ResearchTeamRole {
    MEMBER,
    MODERATOR,
    OWNER;

    public Component getDisplayName() {
        return Component.literal(switch (this) {
            case MEMBER -> "Member";
            case MODERATOR -> "Moderator";
            case OWNER -> "Owner";
        });
    }

}
