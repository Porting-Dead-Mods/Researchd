package com.portingdeadmods.researchd.client.screens.list;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.resources.ResourceLocation;

// TODO: Rename to ResearchStatus
public enum EntryType {
    RESEARCHED(Researchd.rl("entry_green")),
    RESEARCHABLE(Researchd.rl("entry_yellow")),
    LOCKED(Researchd.rl("entry_red"));

    private final ResourceLocation spriteTexture;

    EntryType(ResourceLocation spriteTexture) {
        this.spriteTexture = spriteTexture;
    }

    public ResourceLocation getSpriteTexture() {
        return spriteTexture;
    }

}
