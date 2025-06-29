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

    /**
     * Returns the permission level of the role. <br>
     * <span style="color:red">0 - Member</span> <br>
     * <span style="color:yellow">1 - Moderator</span> <br>
     * <span style="color:green">2 - Owner</span> <br>
     */
    public int getPermissionLevel() {
        return switch (this) {
            case MEMBER -> 0;
            case MODERATOR -> 1;
            case OWNER -> 2;
        };
    }
}
