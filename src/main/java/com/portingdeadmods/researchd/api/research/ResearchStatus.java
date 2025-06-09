package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import net.minecraft.resources.ResourceLocation;

public enum ResearchStatus {
    RESEARCHED("entry_green", 3),
    RESEARCHABLE("entry_yellow", 1),
    LOCKED("entry_red", 2);

    private final ResourceLocation spriteSmallTexture;
    private final ResourceLocation spriteTexture;
    private final ResourceLocation spriteTallTexture;

    /**
     * @param spriteTexture The texture name without the file extension.
     * @param sortingValue A comparative value for sorting purposes. Higher values are drawn on top of lower values.
     */
    ResearchStatus(String spriteTexture, int sortingValue) {
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

    public int getSortingValue() {
        return switch (this) {
            case RESEARCHED -> 3;
            case RESEARCHABLE -> 1;
            case LOCKED -> 2;
        };
    }
}
