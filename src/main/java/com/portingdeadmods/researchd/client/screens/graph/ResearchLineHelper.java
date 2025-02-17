package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import net.minecraft.client.gui.GuiGraphics;

public class ResearchLineHelper {
    public static void drawLineBetweenNodes(GuiGraphics guiGraphics, ResearchNode firstNode, ResearchNode secNode) {
        int firstX = firstNode.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int secondX = secNode.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int firstY = firstNode.getY() + ResearchScreenWidget.PANEL_HEIGHT - 1;
        int secY = secNode.getY();
        if (firstNode.getX() == secNode.getX()) {
            guiGraphics.vLine(firstX, firstY, secY, -1);
        } else {
            int hWidth = secondX - firstX;

            int vHeight = 3;
            guiGraphics.vLine(firstX, firstY, firstY + vHeight, -1);
            guiGraphics.hLine(firstX, firstX + hWidth, firstY + vHeight, -1);
            guiGraphics.vLine(secondX, firstY + vHeight, secY, -1);
        }
    }
}
