package com.portingdeadmods.researchd.client.screens.list;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.resources.ResourceLocation;

// TODO: Rename to ResearchStatus
public enum EntryType {
    RESEARCHED("entry_green"),
    RESEARCHABLE("entry_yellow"),
    LOCKED("entry_red");

    private final ResourceLocation spriteTexture;

    EntryType(String spriteTexture) {
        this.spriteTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + ".png");
    }

    public ResourceLocation getSpriteTexture() {
        return spriteTexture;
    }

}
