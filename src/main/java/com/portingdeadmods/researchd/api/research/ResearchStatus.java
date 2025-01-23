package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.resources.ResourceLocation;

// TODO: Rename to ResearchStatus
public enum ResearchStatus {
    RESEARCHED("entry_green"),
    RESEARCHABLE("entry_yellow"),
    LOCKED("entry_red");

    private final ResourceLocation spriteTexture;

    ResearchStatus(String spriteTexture) {
        this.spriteTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + ".png");
    }

    public ResourceLocation getSpriteTexture() {
        return spriteTexture;
    }

}
