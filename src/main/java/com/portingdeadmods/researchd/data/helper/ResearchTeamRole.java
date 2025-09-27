package com.portingdeadmods.researchd.data.helper;

import com.portingdeadmods.portingdeadlibs.api.translations.TranslatableConstant;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.network.chat.Component;

public enum ResearchTeamRole {
	OWNER(ResearchdTranslations.Team.OWNER, 2),
	MODERATOR(ResearchdTranslations.Team.MODERATOR, 1),
	MEMBER(ResearchdTranslations.Team.MEMBER, 0),
    NOT_MEMBER(ResearchdTranslations.Team.NOT_MEMBER, -1);

    private final TranslatableConstant displayName;
    private final int permissionLevel;

    ResearchTeamRole(TranslatableConstant displayName, int permissionLevel) {
        this.displayName = displayName;
        this.permissionLevel = permissionLevel;
    }

    public Component getDisplayName() {
        return displayName.component(Researchd.MODID);
    }

    /**
     * Returns the permission level of the role. <br>
     * <span style="color:red">0 - Member</span> <br>
     * <span style="color:yellow">1 - Moderator</span> <br>
     * <span style="color:green">2 - Owner</span> <br>
     */
    public int getPermissionLevel() {
        return this.permissionLevel;
    }
}
