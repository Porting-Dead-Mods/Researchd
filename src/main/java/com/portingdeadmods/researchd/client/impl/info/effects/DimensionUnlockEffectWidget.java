package com.portingdeadmods.researchd.client.impl.info.effects;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.api.client.widgets.AbstractResearchInfoWidget;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.GuiUtils;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.util.Size2i;

public class DimensionUnlockEffectWidget extends AbstractResearchInfoWidget<DimensionUnlockEffect> {
    public static final Size2i SPRITE_SIZE = new Size2i(16, 16);
    private final Identifier dimensionIconSprite;

    public DimensionUnlockEffectWidget(int x, int y, DimensionUnlockEffect effect) {
        super(x, y, effect);
        this.dimensionIconSprite = effect.dimensionIconSprite();
    }

    @Override
    public Size2i getSize() {
        return SPRITE_SIZE;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.fill(
                this.getX(),
                this.getY(),
                this.getX() + this.getSize().width,
                this.getY() + this.getSize().height,
                BACKGROUND_COLOR);
        TextureAtlas atlas =
                (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
        if (GuiUtils.spriteExists(atlas, this.dimensionIconSprite)) {
            guiGraphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    this.dimensionIconSprite,
                    this.getX(),
                    this.getY(),
                    SPRITE_SIZE.width,
                    SPRITE_SIZE.height);
        } else {
            guiGraphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    DimensionUnlockEffect.DEFAULT_SPRITE,
                    this.getX(),
                    this.getY(),
                    SPRITE_SIZE.width,
                    SPRITE_SIZE.height);
        }
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered()) {
            MutableComponent component = ResearchdTranslations.component(
                    ResearchdTranslations.Research.DIMENSION_UNLOCK_EFFECT_TOOLTIP,
                    Utils.registryTranslation(this.value.getDimension()));
            GuiUtils.renderTooltip(List.of(component));
            // guiGraphics.renderTooltip(this.font, component, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return false;
    }
}
