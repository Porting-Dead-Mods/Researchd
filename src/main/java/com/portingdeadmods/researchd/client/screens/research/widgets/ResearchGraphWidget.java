package com.portingdeadmods.researchd.client.screens.research.widgets;

import com.portingdeadmods.researchd.api.client.ResearchGraph;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.research.graph.GraphLayoutManager;
import com.portingdeadmods.researchd.client.screens.research.graph.GraphLayoutManager.LayoutResult;
import com.portingdeadmods.researchd.client.screens.research.graph.GraphStateManager;
import com.portingdeadmods.researchd.client.screens.research.graph.ResearchNode;
import com.portingdeadmods.researchd.client.screens.research.graph.lines.ResearchHead;
import com.portingdeadmods.researchd.client.screens.research.graph.lines.ResearchLine;
import com.portingdeadmods.researchd.utils.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget.PANEL_HEIGHT;
import static com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget.PANEL_WIDTH;
import static com.portingdeadmods.researchd.client.screens.research.graph.GraphLayoutManager.*;

public class ResearchGraphWidget extends AbstractWidget {
    public static final int LEFT_MARGIN_WIDTH = 174 + 13;

    private @Nullable ResearchGraph graph;
    private final List<ResearchLine> researchLines;
    private @Nullable LayoutResult layoutResult;

    private final ResearchScreen researchScreen;
    private final float ROOT_NODE_SCALING = 1.75f;

    public ResearchGraphWidget(ResearchScreen researchScreen, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.researchScreen = researchScreen;
        this.researchLines = new ArrayList<>();
    }

    /**
     * Set the graph to be displayed, applying layout if needed and calculating connection lines
     */
    public void setGraph(ResearchGraph graph) {
        if (this.graph != graph) {
            this.graph = graph;
            this.researchScreen.getResearchPagesList().setSelectedPage(CommonResearchCache.pageOf(graph.rootNode().getResearch()));
            this.researchLines.clear();
            this.layoutResult = null;

            if (graph == null || graph.nodes().isEmpty()) {
                return;
            }

            boolean layoutRestored = GraphStateManager.getInstance().tryRestoreLastSessionState(graph);

            if (!layoutRestored) {
                this.layoutResult = GraphLayoutManager.applyLayout(graph, 0, 0);
            }

            for (ResearchNode node : graph.nodes().values()) {
                node.refreshHeads();
            }

            // Center the graph in the widget
            int baseX = this.graph.rootNode().getX();
            int baseY = this.graph.rootNode().getY();
            int centerX = baseX + PANEL_WIDTH / 2;
            int centerY = baseY + PANEL_HEIGHT / 2;
            translate(this.getWidth() / 2 - centerX + LEFT_MARGIN_WIDTH, this.getHeight() / 2 - centerY);

            calculateLines();
        }
    }

    // =============================
    // Channel-based edge routing
    // =============================

    /**
     * Builds ResearchLines for every parent - child node connection
     */
    private void calculateLines() {
        this.researchLines.clear();
        if (this.graph == null || this.graph.nodes().isEmpty()) return;

        if (this.layoutResult == null) {
            calculateLinesSimple();
            return;
        }

        // Track used heads to prevent double-assignment
        Set<ResearchHead> usedOutputHeads = new HashSet<>();
        Set<ResearchHead> usedInputHeads = new HashSet<>();

        for (ResearchNode parent : graph.nodes().values()) {
            for (ResearchNode child : parent.getChildren()) {
                if (!graph.nodes().containsValue(child)) continue;

                ResearchHead outputHead = findClosestAvailableHead(parent.getOutputs(), child.getX() + PANEL_WIDTH / 2, usedOutputHeads);
                ResearchHead inputHead = findClosestAvailableHead(child.getInputs(), parent.getX() + PANEL_WIDTH / 2, usedInputHeads);

                if (outputHead == null || inputHead == null) continue;

                usedOutputHeads.add(outputHead);
                usedInputHeads.add(inputHead);

                Point outPt = outputHead.getConnectionPoint();
                Point inPt = inputHead.getConnectionPoint();

                long key = GraphLayoutManager.edgeKey(parent, child);
                Map<Integer, Integer> zoneAssignments = layoutResult.edgeChannelAssignments.get(key);

                ResearchLine line;
                if (zoneAssignments == null || zoneAssignments.isEmpty()) {
                    // Straight edge -> just a vertical line (or near-vertical L)
                    if (outPt.x == inPt.x) {
                        line = ResearchLine.start(outPt).then(inPt);
                    } else {
                        line = ResearchLine.createLConnection(outPt, inPt, true);
                    }
                } else {
                    line = buildChannelRoute(outPt, inPt, parent.getLayer(), child.getLayer(), zoneAssignments);
                }

                this.researchLines.add(line);
            }
        }
    }

    /**
     * Builds a route from parent output to child input through routing channels.
     * For edges spanning a single zone: down → horizontal at channel → down.
     * For edges spanning multiple zones: chains through intermediate channels.
     */
    private ResearchLine buildChannelRoute(Point outPt, Point inPt, int parentLayer, int childLayer, Map<Integer, Integer> zoneAssignments) {
        ResearchLine line = ResearchLine.start(outPt);

        // Current X tracks where the vertical line is
        int currentX = outPt.x;

        for (int zone = parentLayer; zone < childLayer; zone++) {
            Integer channelIndex = zoneAssignments.get(zone);

            if (channelIndex != null) {
                // This edge has a horizontal segment in this zone
                int channelY = layoutResult.zoneBaseY[zone] + channelIndex * CHANNEL_SIZE;

                // Vertical down to channel
                line.then(currentX, channelY);

                // Determine target X: for the last zone, go to input X; otherwise stay at current path
                int targetX;
                if (zone == childLayer - 1) {
                    targetX = inPt.x;
                } else {
                    // For intermediate zones, route toward the child's X
                    targetX = inPt.x;
                }

                // Horizontal along channel
                if (targetX != currentX) {
                    line.then(targetX, channelY);
                    currentX = targetX;
                }
            }
            // If no channel assignment for this zone, just pass through vertically
        }

        // Final vertical segment down to input
        line.then(inPt);

        return line;
    }

    /**
     * Simple fallback routing when layout result is not available (e.g., restored from cache).
     * Uses basic L-shaped connections.
     */
    private void calculateLinesSimple() {
        Set<ResearchHead> usedOutputHeads = new HashSet<>();
        Set<ResearchHead> usedInputHeads = new HashSet<>();

        for (ResearchNode parent : graph.nodes().values()) {
            for (ResearchNode child : parent.getChildren()) {
                if (!graph.nodes().containsValue(child)) continue;

                ResearchHead outputHead = findClosestAvailableHead(parent.getOutputs(), child.getX() + PANEL_WIDTH / 2, usedOutputHeads);
                ResearchHead inputHead = findClosestAvailableHead(child.getInputs(), parent.getX() + PANEL_WIDTH / 2, usedInputHeads);

                if (outputHead == null || inputHead == null) continue;

                usedOutputHeads.add(outputHead);
                usedInputHeads.add(inputHead);

                Point outPt = outputHead.getConnectionPoint();
                Point inPt = inputHead.getConnectionPoint();

                ResearchLine line;
                if (outPt.x == inPt.x) {
                    line = ResearchLine.start(outPt).then(inPt);
                } else {
                    line = ResearchLine.createLConnection(outPt, inPt, true);
                }

                this.researchLines.add(line);
            }
        }
    }

    /**
     * Finds the closest available head to a target X position.
     */
    private @Nullable ResearchHead findClosestAvailableHead(Iterable<ResearchHead> heads, int targetX, Set<ResearchHead> usedHeads) {
        ResearchHead best = null;
        int bestDist = Integer.MAX_VALUE;

        for (ResearchHead head : heads) {
            if (usedHeads.contains(head)) continue;
            int dist = Math.abs(head.getX() - targetX);
            if (dist < bestDist) {
                bestDist = dist;
                best = head;
            }
        }

        // If all heads are used, fall back to closest regardless
        if (best == null) {
            for (ResearchHead head : heads) {
                int dist = Math.abs(head.getX() - targetX);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = head;
                }
            }
        }

        return best;
    }

    // =============================
    // Rendering
    // =============================

    public ResearchGraph getCurrentGraph() {
        return this.graph;
    }

    private void renderHeader(GuiGraphics guiGraphics, int x) {
        if (this.graph == null) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int y = 12;

        ResearchPage page = this.researchScreen.getResearchPagesList().getSelectedPage();
        ResourceLocation pageId = page != null ? page.id() : ResearchPage.DEFAULT_PAGE_ID;

        // Completion text pos is used to wrap title so it needs to be done before
        int completed = (int) this.graph.nodes().values().stream()
                .filter(node -> node.getInstance().isResearched())
                .count();
        int total = this.graph.nodes().size();
        Component completionText = Component.literal(completed + "/" + total).withStyle(ChatFormatting.GOLD);
        int completionTextWidth = font.width(completionText);
        int completionTextX = guiGraphics.guiWidth() - 8 - completionTextWidth - 5;

        // Title
        Component title = Component.translatable("researchpage." + pageId.getNamespace() + "." + pageId.getPath() + ".title");
        int titleMaxWidth = completionTextX - x - 4;
        TextUtils.drawWrappedText(guiGraphics, title, x, y, titleMaxWidth, 0xFFFFFF, true);

        // Description
        Component description = Component.translatable("researchpage." + pageId.getNamespace() + "." + pageId.getPath() + ".description");
        int rightPadding = Math.max(8, guiGraphics.guiWidth() - x - 250);
        int descMaxWidth = Math.min(guiGraphics.guiWidth() - x - rightPadding, 250);
        TextUtils.drawWrappedText(guiGraphics, description.copy().withStyle(ChatFormatting.GRAY), x, y + 12, descMaxWidth, 0xAAAAAA, false);

        // Completion count (right-aligned)
        guiGraphics.drawString(font, completionText, completionTextX, y, 0xFFFFFF, true);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        if (this.graph == null || this.graph.nodes() == null) {
            return;
        }

        int w = 174 + 13;

        // title, description, completion count
        renderHeader(guiGraphics, w + 5);

        guiGraphics.enableScissor(w, 8, guiGraphics.guiWidth() - 8, guiGraphics.guiHeight() - 8);
        {
            for (ResearchLine line : this.researchLines) {
                line.render(guiGraphics);
            }

            for (ResearchNode node : this.graph.nodes().values()) {
                if (node.isRootNode()) {
                    int width = ResearchScreenWidget.PANEL_WIDTH;
                    int height = ResearchScreenWidget.PANEL_HEIGHT;

                    // getX() and getY() = top-left of normal panel (ROOT_NODE_SCALING = 1)
                    float baseX = node.getX();
                    float baseY = node.getY();

                    // compute center of unscaled panel
                    float centerX = baseX + width / 2f;
                    float centerY = baseY + height / 2f;

                    // compute top-left of scaled panel to keep same center
                    int scaledX = (int) (centerX - (width * ROOT_NODE_SCALING) / 2f);
                    int scaledY = (int) (centerY - (height * ROOT_NODE_SCALING) / 2f);

                    node.setHovered(guiGraphics, scaledX, scaledY, (int) (20 * ROOT_NODE_SCALING), (int) (24 * ROOT_NODE_SCALING), mouseX, mouseY);
                    ResearchScreenWidget.renderResearchPanel(
                            guiGraphics,
                            node.getInstance(),
                            scaledX + 1,
                            scaledY,
                            mouseX,
                            mouseY,
                            ROOT_NODE_SCALING
                    );
                } else {
                    node.render(guiGraphics, mouseX, mouseY, v);
                }
            }
        }
        guiGraphics.disableScissor();
    }

    // TODO: Cache hovered node like the isHovered field
    public void renderNodeTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.isHovered() || this.graph == null || this.graph.nodes() == null) return;

        for (ResearchNode node : this.graph.nodes().values()) {
            if (node.isHovered()) {
                Minecraft mc = Minecraft.getInstance();
                // Debug tooltip
                if (SharedConstants.IS_RUNNING_IN_IDE && !ResearchScreen.hasControlDown()) {
                    guiGraphics.renderComponentTooltip(mc.font, List.of(
                            node.getInstance().getDisplayName(mc.level),
                            node.getInstance().getDescription(mc.level),
                            SharedConstants.IS_RUNNING_IN_IDE ? Component.literal("Press Ctrl for debug info") : Component.empty()
                    ), mouseX, mouseY);
                } else {
                    guiGraphics.renderComponentTooltip(mc.font, List.of(
                            node.getInstance().getDisplayName(mc.level),
                            node.getInstance().getDescription(mc.level),
                            Component.literal("x: %d, y: %d".formatted(node.getX(), node.getY())),
                            Component.literal("w: %d, h: %d".formatted(node.getWidth(), node.getHeight())),
                            Component.literal("hovered: %s".formatted(node.isHovered())),
                            Component.literal("%d parents".formatted(node.getParents().size())),
                            Component.literal("%d children".formatted(node.getChildren().size()))
                    ), mouseX, mouseY);
                }
                break;
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public void translate(int dx, int dy) {
        if (this.graph != null && this.graph.nodes() != null) {
            for (ResearchNode node : this.graph.nodes().values()) {
                node.translate(dx, dy);
            }
        }

        for (ResearchLine line : this.researchLines) {
            line.translate(dx, dy);
        }

        // Update zone base Y positions if we have layout result
        if (this.layoutResult != null) {
            for (int i = 0; i < this.layoutResult.zoneBaseY.length; i++) {
                this.layoutResult.zoneBaseY[i] += dy;
            }
            for (int i = 0; i < this.layoutResult.layerY.length; i++) {
                this.layoutResult.layerY[i] += dy;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.graph == null || this.graph.nodes() == null) {
            return false;
        }

        for (ResearchNode node : this.graph.nodes().values()) {
            if (node.isHovered()) {
                this.setGraph(ResearchGraphCache.computeIfAbsent(node.getInstance().getKey()));
                List<ResearchInstance> entries = this.researchScreen.getTechList().entries();
                int index = entries.indexOf(node.getInstance());
                if (index != -1) {
                    this.researchScreen.getSelectedResearchWidget().setSelectedResearch(entries.get(index));
                    return super.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isHovered()) {
            translate((int) dragX, (int) dragY);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public void onClose() {
        if (graph != null) {
            // Make sure we save the final state
            GraphStateManager.getInstance().saveLastSessionState(graph);

            // TODO: Reimpl this
            //ClientResearchCache.ROOT_NODE = graph.rootNode();
        }
    }
}
