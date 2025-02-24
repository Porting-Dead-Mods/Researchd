package com.portingdeadmods.researchd.clown;

import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.utils.researches.data.ResearchGraph;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles rendering of node connections using a tree structure
 */
public class NodeConnectionRenderer {
    private static final int CONNECTION_LENGTH = 20; // Vertical line length from node

    /**
     * Renders connections for the graph with selection awareness
     */
    public static void renderConnections(GuiGraphics guiGraphics, ResearchGraph graph, ResearchNode selectedNode) {
        // If a specific node is selected, only render its connections
        if (selectedNode != null) {
            // Render connections from parent to the selected node
            renderParentConnections(guiGraphics, selectedNode);

            // Render connections from selected node to its children
            renderChildConnections(guiGraphics, selectedNode);
        } else {
            // Otherwise render the full tree starting from root
            renderNodeConnections(guiGraphics, graph.rootNode());
        }
    }

    /**
     * Renders connections from parents to a selected node
     */
    private static void renderParentConnections(GuiGraphics guiGraphics, ResearchNode node) {
        List<ResearchNode> parents = node.getParents();
        if (!parents.isEmpty()) {
            // Since we're selecting a specific connection path, only use first parent
            ResearchNode parent = parents.get(0);

            // Calculate points
            int childX = node.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
            int childY = node.getY();
            int parentX = parent.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
            int parentY = parent.getY() + ResearchScreenWidget.PANEL_HEIGHT;

            // Draw connection
            drawVerticalConnection(guiGraphics, parentX, parentY, childX, childY);
        }
    }

    /**
     * Renders connections from a node to its children
     */
    private static void renderChildConnections(GuiGraphics guiGraphics, ResearchNode node) {
        List<ResearchNode> children = node.getChildren();

        if (!children.isEmpty()) {
            // Calculate output point from parent
            int parentX = node.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
            int parentY = node.getY() + ResearchScreenWidget.PANEL_HEIGHT;

            // If there's only one child, draw a direct line
            if (children.size() == 1) {
                ResearchNode child = children.get(0);
                int childX = child.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
                int childY = child.getY();

                drawVerticalConnection(guiGraphics, parentX, parentY, childX, childY);
            }
            // If there are multiple children, use a tree structure with a single junction
            else if (children.size() > 1) {
                // Create a mutable copy of the children list for sorting
                List<ResearchNode> sortedChildren = new ArrayList<>(children);
                sortedChildren.sort(Comparator.comparingInt(ResearchNode::getX));

                // Find leftmost and rightmost child positions
                int leftmostX = sortedChildren.get(0).getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
                int rightmostX = sortedChildren.get(sortedChildren.size() - 1).getX() + ResearchScreenWidget.PANEL_WIDTH / 2;

                // Draw vertical line down from parent
                int junctionY = parentY + CONNECTION_LENGTH;
                guiGraphics.vLine(parentX, parentY, junctionY, -1);

                // Draw horizontal line connecting all children
                guiGraphics.hLine(leftmostX, rightmostX, junctionY, -1);

                // Draw vertical lines down to each child
                for (ResearchNode child : sortedChildren) {
                    int childX = child.getX() + ResearchScreenWidget.PANEL_WIDTH / 2;
                    int childY = child.getY();
                    guiGraphics.vLine(childX, junctionY, childY, -1);
                }
            }
        }
    }

    /**
     * Recursively renders connections for a node and its children
     */
    private static void renderNodeConnections(GuiGraphics guiGraphics, ResearchNode node) {
        // Render connections to children first
        renderChildConnections(guiGraphics, node);

        // Then recursively render for each child
        for (ResearchNode child : node.getChildren()) {
            renderNodeConnections(guiGraphics, child);
        }
    }

    /**
     * Draws a connection line from parent to child with appropriate vertical and horizontal segments
     */
    private static void drawVerticalConnection(GuiGraphics guiGraphics, int parentX, int parentY, int childX, int childY) {
        // Parent and child aligned - draw straight line
        if (parentX == childX) {
            guiGraphics.vLine(parentX, parentY, childY, -1);
        }
        // Parent and child not aligned - need to draw with corners
        else {
            int midY = parentY + (childY - parentY) / 2;

            // Draw vertical line down from parent
            guiGraphics.vLine(parentX, parentY, midY, -1);
            // Draw horizontal line to child's X position
            guiGraphics.hLine(Math.min(parentX, childX), Math.max(parentX, childX), midY, -1);
            // Draw vertical line to child
            guiGraphics.vLine(childX, midY, childY, -1);
        }
    }
}