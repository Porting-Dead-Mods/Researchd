package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import net.minecraft.resources.ResourceLocation;

public enum ResearchStatus {
    RESEARCHED("entry_green"),
    RESEARCHABLE("entry_yellow"),
    LOCKED("entry_red");

    private final ResourceLocation spriteSmallTexture;
    private final ResourceLocation spriteTexture;
    private final ResourceLocation spriteTallTexture;

    ResearchStatus(String spriteTexture) {
        this.spriteSmallTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + "_small.png");
        this.spriteTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + ".png");
        this.spriteTallTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + "_tall.png");
    }

    public ResourceLocation getSpriteTexture() {
        return spriteTexture;
    }

    public ResourceLocation getSpriteTexture(ResearchScreenWidget.PanelSpriteType spriteType) {
        return switch (spriteType) {
            case TALL -> this.spriteTallTexture;
            case NORMAL -> this.spriteTexture;
            case SMALL -> this.spriteSmallTexture;
        };
    }
}
