package com.portingdeadmods.researchd.client.impl.effects;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Size2i;

public class DimensionUnlockEffectWidget extends AbstractResearchInfoWidget<DimensionUnlockEffect> {
    public static final Size2i SPRITE_SIZE = new Size2i(16, 16);
    private final ResourceLocation dimensionIconSprite;

    public DimensionUnlockEffectWidget(int x, int y, DimensionUnlockEffect method) {
        super(x, y, method);
        this.dimensionIconSprite = method.dimensionIconSprite();
    }

    @Override
    public Size2i getSize() {
        return SPRITE_SIZE;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getSize().width, this.getY() + this.getSize().height, BACKGROUND_COLOR);
        guiGraphics.blitSprite(this.dimensionIconSprite, this.getX(), this.getY(), SPRITE_SIZE.width, SPRITE_SIZE.height);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered()) {
            guiGraphics.renderTooltip(this.font, ResearchdTranslations.component(ResearchdTranslations.Research.DIMENSION_UNLOCK_EFFECT_TOOLTIP, Utils.registryTranslation(this.value.getDimension())), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
}
