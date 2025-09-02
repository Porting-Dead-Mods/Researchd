package com.portingdeadmods.researchd.client.impl.effects;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.Optional;

public class RecipeUnlockEffectWidget extends AbstractResearchInfoWidget<RecipeUnlockEffect> {
    public final Integer TEXT_WIDTH;
    public final RecipeHolder<?> recipe;
    public final ItemStack result;

    public RecipeUnlockEffectWidget(int x, int y, RecipeUnlockEffect method) {
        super(x, y, method);

        this.TEXT_WIDTH = font.width(ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP_NO_ARG));
        Optional<RecipeHolder<?>> holder = Minecraft.getInstance().level.getRecipeManager().byKey(method.recipe());
        this.recipe = holder.orElse(null);
        this.result = this.recipe != null ? this.recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess()) : null;
    }

    @Override
    public Size2i getSize() {
        // Constructors need super() as first call in java 21 <3
        if (this.TEXT_WIDTH == null)
            return new Size2i(font.width(ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP_NO_ARG)) + 16, 16);

        return new Size2i(TEXT_WIDTH + 16 + 2, 16); // + 2 padding
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getSize().width, this.getY() + this.getSize().height, BACKGROUND_COLOR);
        guiGraphics.drawString(font, ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP_NO_ARG), this.getX() + 2, this.getY() + 4, 0xFFFFFF, true);

        if (this.result != null) {
            guiGraphics.renderItem(result, this.getX() + 2 + TEXT_WIDTH, this.getY());
            guiGraphics.renderItemDecorations(font, result,this.getX() + 2 + TEXT_WIDTH, this.getY());
        } else {
            guiGraphics.drawString(font, "MISSING RECIPE", this.getX() + 2 + TEXT_WIDTH, this.getY() + 4, 0xFF5555, true);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered()) {
            guiGraphics.renderTooltip(this.font, ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP, value.recipe().toString()), mouseX, mouseY);
        }
    }
}
