package com.portingdeadmods.researchd.client.impl.effects;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.Optional;

public class RecipeUnlockEffectWidget extends AbstractResearchInfoWidget<RecipeUnlockEffect> {
    public static final ResourceLocation RECIPE_ICON_SPRITE = Researchd.rl("recipe_icon");
    public final Integer textWidth;
    public final RecipeHolder<?> recipe;
    public final ItemStack result;

    public RecipeUnlockEffectWidget(int x, int y, RecipeUnlockEffect method) {
        super(x, y, method);

        this.textWidth = font.width(ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP_NO_ARG));
        Optional<RecipeHolder<?>> holder = Minecraft.getInstance().level.getRecipeManager().byKey(method.recipe());
        this.recipe = holder.orElse(null);
        this.result = this.recipe != null ? this.recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess()) : null;
    }

    @Override
    public Size2i getSize() {
        return new Size2i(16, 16); // + 2 padding
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        if (this.result != null) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + 16, this.getY() + 16, FastColor.ARGB32.color(69, 69, 69));
            guiGraphics.renderItem(result, this.getX(), this.getY());
            guiGraphics.blitSprite(RECIPE_ICON_SPRITE, this.getX() + 7, this.getY() + 6, 16, 16);
        } else {
            guiGraphics.drawString(font, "MISSING RECIPE", this.getX() + 2, this.getY() + 4, 0xFF5555, true);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered()) {
            MutableComponent component = ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP, value.recipe().toString());
            guiGraphics.renderTooltip(this.font, component, mouseX, mouseY);
        }
    }
}
