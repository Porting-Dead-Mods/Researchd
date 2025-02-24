package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResearchLineHelper {
    public static void drawLineBetweenNodes(GuiGraphics guiGraphics, ResearchNode parent, ResearchNode child) {
        // Calculate center points
        int parentX = parent.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int parentY = parent.getY() + ResearchScreenWidget.PANEL_HEIGHT;
        int childX = child.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int childY = child.getY();

        // Draw connection using a tree structure
        drawConnection(guiGraphics, parentX, parentY, childX, childY);
    }

    public static void drawConnection(GuiGraphics guiGraphics, int parentX, int parentY, int childX, int childY) {
        // If the parent and child are aligned vertically, draw a straight line
        if (parentX == childX) {
            guiGraphics.vLine(parentX, parentY, childY, -1);
        }
        // Otherwise draw a stepped line with corners
        else {
            // Calculate midpoint for the horizontal segment
            int midY = parentY + (childY - parentY) / 2;

            // Draw vertical line from parent down to midpoint
            guiGraphics.vLine(parentX, parentY, midY, -1);

            // Draw horizontal line from parent's X to child's X
            guiGraphics.hLine(Math.min(parentX, childX), Math.max(parentX, childX), midY, -1);

            // Draw vertical line from midpoint down to child
            guiGraphics.vLine(childX, midY, childY, -1);
        }
    }

    /**
     * Draws connections for a node with multiple children using a tree structure
     */
    public static void drawConnectionTree(GuiGraphics guiGraphics, ResearchNode parent, List<ResearchNode> children) {
        if (children.isEmpty()) return;

        // Get parent connection point
        int parentX = parent.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int parentY = parent.getY() + ResearchScreenWidget.PANEL_HEIGHT;

        // For a single child, just draw a direct connection
        if (children.size() == 1) {
            drawLineBetweenNodes(guiGraphics, parent, children.get(0));
            return;
        }

        // For multiple children, create a tree structure
        List<ResearchNode> sortedChildren = new ArrayList<>(children);
        sortedChildren.sort(Comparator.comparingInt(ResearchNode::getX));

        // Find leftmost and rightmost children
        int leftmostX = sortedChildren.get(0).getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
        int rightmostX = sortedChildren.get(sortedChildren.size() - 1).getX() + ResearchScreenWidget.PANEL_WIDTH / 2;

        // Draw the trunk of the tree
        int junctionY = parentY + 20;
        guiGraphics.vLine(parentX, parentY, junctionY, -1);

        // Draw the horizontal branch
        guiGraphics.hLine(leftmostX, rightmostX, junctionY, -1);

        // Draw connections to each child
        for (ResearchNode child : sortedChildren) {
            int childX = child.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
            int childY = child.getY();
            guiGraphics.vLine(childX, junctionY, childY, -1);
        }
    }
}
