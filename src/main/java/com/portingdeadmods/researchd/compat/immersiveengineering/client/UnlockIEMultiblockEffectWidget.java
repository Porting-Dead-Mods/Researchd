package com.portingdeadmods.researchd.compat.immersiveengineering.client;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.compat.immersiveengineering.UnlockIEMultiblockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Size2i;

public class UnlockIEMultiblockEffectWidget extends AbstractResearchInfoWidget<UnlockIEMultiblockEffect> {
    private final ItemStack icon;

    public UnlockIEMultiblockEffectWidget(int x, int y, UnlockIEMultiblockEffect effect) {
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
            guiGraphics.drawString(this.font, Component.literal("MB"), this.getX() + 2, this.getY() + 4, 0xFFFFFF, false);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered()) {
            Component displayName;
            MultiblockHandler.IMultiblock multiblock = MultiblockHandler.getByUniqueName(this.value.multiblock());
            if (multiblock != null) {
                displayName = this.value.name()
                        .map(Component::literal)
                        .orElse(MutableComponent.create(multiblock.getDisplayName().getContents()));
            } else {
                displayName = this.value.name()
                        .map(Component::literal)
                        .orElse(Component.literal(this.value.multiblock().toString()));
            }

            MutableComponent message = ResearchdTranslations.component(ResearchdTranslations.Research.MULTIBLOCK_UNLOCK_EFFECT_TOOLTIP, displayName)
                    .withStyle(ChatFormatting.GOLD);
            guiGraphics.renderTooltip(this.font, message, mouseX, mouseY);
        }
    }
}
