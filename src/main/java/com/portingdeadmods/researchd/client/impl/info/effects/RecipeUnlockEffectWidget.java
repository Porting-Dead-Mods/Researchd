package com.portingdeadmods.researchd.client.impl.info.effects;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.compat.RecipeViewerHelper;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.GuiUtils;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.util.Size2i;
import org.jetbrains.annotations.Nullable;

// TODO(26.1 port, recipe displays): 26.1 clients no longer receive full recipes, so this widget shows only the
// effect's icon and the recipe ids. Draw the recipes from the recipe displays the server will send.
public class RecipeUnlockEffectWidget extends AbstractResearchInfoWidget<RecipeUnlockEffect> {
    public static final Identifier RECIPE_ICON_SPRITE = Researchd.rl("recipe_icon");
    public final Integer textWidth;
    public final List<ResourceKey<Recipe<?>>> recipes;
    public final @Nullable ItemStack icon;

    public RecipeUnlockEffectWidget(int x, int y, RecipeUnlockEffect effect) {
        super(x, y, effect);

        this.textWidth = font.width(
                ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP_NO_ARG));
        this.recipes = effect.recipes().stream()
                .sorted(Comparator.comparing(ResourceKey::identifier))
                .toList();
        this.icon = effect.icon().map(icon -> icon.create()).orElse(null);
    }

    public boolean hasRecipes() {
        return !this.recipes.isEmpty();
    }

    @Override
    public Size2i getSize() {
        return new Size2i(16, 16); // + 2 padding
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float v) {
        if (this.hasRecipes()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + 16, this.getY() + 16, ARGB.color(69, 69, 69));
            if (this.icon != null) {
                guiGraphics.item(this.icon, this.getX(), this.getY());
            }
            guiGraphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED, RECIPE_ICON_SPRITE, this.getX() + 7, this.getY() + 6, 16, 16);
        } else {
            guiGraphics.text(font, "MISSING RECIPE", this.getX() + 2, this.getY() + 4, 0xFFFF5555, true);
        }
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered()) {
            MutableComponent component = ResearchdTranslations.component(
                    ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP,
                    this.recipes.stream().map(ResourceKey::identifier).toList().toString());
            GuiUtils.renderTooltip(List.of(component));
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (this.isHovered() && this.hasRecipes()) {
            RecipeViewerHelper.openRecipe(this.recipes.getFirst());
        }
    }
}
