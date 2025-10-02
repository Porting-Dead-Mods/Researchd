package com.portingdeadmods.researchd.client.screens;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchStatus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;

import javax.annotation.Nullable;

public abstract class ResearchScreenWidget extends AbstractWidget {
    public static final int PANEL_WIDTH = 20;
    public static final int SMALL_PANEL_HEIGHT = 22;
    public static final int PANEL_HEIGHT = 24;
    public static final int TALL_PANEL_HEIGHT = 32;

    public ResearchScreenWidget(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, float scale) {
        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, scale, true);
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, float scale, boolean hoverable) {
        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, scale, hoverable, false);
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, float scale, boolean hoverable, boolean tall) {
        int width = PANEL_WIDTH;
        int height = PANEL_HEIGHT;
        ResearchStatus status = instance.getResearchStatus();
        guiGraphics.blit(status.getSpriteTexture(), x, y, (int) (width * scale), (int) (height * scale), 0, 0, width, height, width, height);

        ClientResearchIcon<?> clientResearchIcon = ResearchScreen.CLIENT_ICONS.get(instance.getKey().location());
        if (clientResearchIcon != null) {
            clientResearchIcon.render(guiGraphics, x, y, mouseX, mouseY, scale, 0);
        }

        if (isHovering(guiGraphics, x, y, mouseX, mouseY, scale) && hoverable) {
            int color = -2130706433;
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, (int) (x + 20 * scale), (int) (y + 20 * scale), color, color, 0);
        }
    }

    public static void renderSmallResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY) {
        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, true, PanelSpriteType.SMALL);
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY) {
        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, true, PanelSpriteType.NORMAL);
    }

    public static void renderTallResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY) {
        renderResearchPanel(guiGraphics, instance, x, y, mouseX, mouseY, true, PanelSpriteType.TALL);
    }

    public static void renderResearchPanel(GuiGraphics guiGraphics, ResearchInstance instance, int x, int y, int mouseX, int mouseY, boolean hoverable, PanelSpriteType spriteType) {
        ResearchStatus status = instance.getResearchStatus();
        GuiUtils.drawImg(guiGraphics, status.getSpriteTexture(spriteType), x, y, PANEL_WIDTH, spriteType.getHeight());

        ClientResearchIcon<?> clientResearchIcon = ResearchScreen.CLIENT_ICONS.get(instance.getKey().location());

        if (clientResearchIcon != null) {
            clientResearchIcon.render(guiGraphics, x + 2, y + 2, mouseX, mouseY, 1, 0);
        }

        if (isHovering(guiGraphics, x, y, mouseX, mouseY) && hoverable) {
            int color = -2130706433;
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 20, y + 20, color, color, 0);
        }
    }

    public static boolean isHovering(@Nullable GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        return isHovering(guiGraphics, x, y, mouseX, mouseY, 1);
    }

    public static boolean isHovering(@Nullable GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float scale) {
        return (guiGraphics == null || guiGraphics.containsPointInScissor(mouseX, mouseY))
                && mouseX >= x
                && mouseY >= y
                && mouseX < x + PANEL_WIDTH * scale
                && mouseY < y + PANEL_HEIGHT * scale;
    }

    public enum PanelSpriteType {
        TALL(TALL_PANEL_HEIGHT),
        NORMAL(PANEL_HEIGHT),
        SMALL(SMALL_PANEL_HEIGHT);

        private final int height;

        PanelSpriteType(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

}
