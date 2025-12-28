package com.portingdeadmods.researchd.client.impl.effects;

import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.compat.JEICompat;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.impl.research.effect.ItemUnlockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Size2i;

import java.util.Set;

public class UnlockItemEffectWidget extends AbstractResearchInfoWidget<ItemUnlockEffect> {
    private final ItemStack icon;

    public UnlockItemEffectWidget(int x, int y, ItemUnlockEffect effect) {
        super(x, y, effect);
        this.icon = effect.getDisplayStack();
    }

    @Override
    public Size2i getSize() {
        return new Size2i(16, 16);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + 16, this.getY() + 16, FastColor.ARGB32.color(69, 69, 69));
        if (!this.icon.isEmpty()) {
            guiGraphics.renderItem(this.icon, this.getX(), this.getY());
        } else {
            guiGraphics.drawString(this.font, Component.literal("?"), this.getX() + 5, this.getY() + 4, 0xFFFFFF, false);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered()) {
            MutableComponent defaultName = Component.translatable(this.value.getItem().getDescriptionId());
            Component displayName = this.value.name().map(Component::literal).orElse(defaultName);
            MutableComponent message = ResearchdTranslations.component(ResearchdTranslations.Research.ITEM_UNLOCK_EFFECT_TOOLTIP, displayName);
            guiGraphics.renderTooltip(this.font, message, mouseX, mouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (this.isHovered() && ResearchdCompatHandler.isJeiLoaded()) {
            Set<RecipeHolder<?>> recipes = this.value.getRecipes(Minecraft.getInstance().level);
            if (!recipes.isEmpty()) {
                JEICompat.openRecipes(recipes);
            }
        }
    }
}
