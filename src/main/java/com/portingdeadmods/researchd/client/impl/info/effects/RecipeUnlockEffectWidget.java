package com.portingdeadmods.researchd.client.impl.info.effects;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.renderers.CycledItemRenderer;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.compat.JEICompat;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeUnlockEffectWidget extends AbstractResearchInfoWidget<RecipeUnlockEffect> {
    public static final ResourceLocation RECIPE_ICON_SPRITE = Researchd.rl("recipe_icon");
    public final Integer textWidth;
    public final List<RecipeHolder<?>> recipes;
    private final CycledItemRenderer itemRenderer;
    public final ItemStack icon;

    public RecipeUnlockEffectWidget(int x, int y, RecipeUnlockEffect effect) {
        super(x, y, effect);

        this.textWidth = font.width(ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP_NO_ARG));
        this.recipes = effect.getRecipes(Minecraft.getInstance().level).stream().toList();
        List<ItemStack> resultItems = this.recipes.stream()
                .map(RecipeHolder::value)
                .map(r -> r.getResultItem(Minecraft.getInstance().level.registryAccess()))
                .toList();
        if (effect.icon().isPresent()) {
            this.icon = effect.icon().get();
            this.itemRenderer = null; new CycledItemRenderer();
        } else {
            this.icon = null;
            this.itemRenderer = new CycledItemRenderer(List.copyOf(List.copyOf(resultItems)), 1);
        }
    }

    public boolean hasRecipes() {
        return !this.recipes.isEmpty();
    }

    private ItemStack getMostCommonResultItem(List<ItemStack> resultItems) {
        Map<ItemStack, Integer> map = new HashMap<>();
        for (ItemStack item : resultItems) {
            map.merge(item, 1, Integer::sum);
        }
        return map.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(ItemStack.EMPTY);
    }

    @Override
    public Size2i getSize() {
        return new Size2i(16, 16); // + 2 padding
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        if (this.hasRecipes()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + 16, this.getY() + 16, FastColor.ARGB32.color(69, 69, 69));
            if (this.icon != null) {
                guiGraphics.renderItem(this.icon, this.getX(), this.getY());
            } else {
                this.itemRenderer.render(guiGraphics, this.getX(), this.getY());
                this.itemRenderer.tick(v);
            }
            guiGraphics.blitSprite(RECIPE_ICON_SPRITE, this.getX() + 7, this.getY() + 6, 200, 16, 16);
        } else {
            guiGraphics.drawString(font, "MISSING RECIPE", this.getX() + 2, this.getY() + 4, 0xFF5555, true);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered()) {
            MutableComponent component = ResearchdTranslations.component(ResearchdTranslations.Research.RECIPE_UNLOCK_EFFECT_TOOLTIP, value.recipes().toString());
            GuiUtils.renderTooltip(List.of(component));
            //guiGraphics.renderTooltip(this.font, component, mouseX, mouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (this.isHovered()) {
            if (ResearchdCompatHandler.isJeiLoaded()) {
                Set<RecipeHolder<?>> recipes1 = this.value.getRecipes(Minecraft.getInstance().level);
                JEICompat.openRecipes(recipes1);
            }
        }
    }
}
