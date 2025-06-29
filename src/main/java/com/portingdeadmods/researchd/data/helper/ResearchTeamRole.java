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
     * <font color="red">0 - Member</font> <br>
     * <font color="yellow">1 - Moderator</font> <br>
     * <font color="green">2 - Owner</font> <br>
     */
    public int getPermissionLevel() {
        return switch (this) {
            case MEMBER -> 0;
            case MODERATOR -> 1;
            case OWNER -> 2;
        };
    }
}
