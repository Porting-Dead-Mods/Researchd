package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.data.ResearchGraph;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.lines.ResearchHead;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Drawable widget for completedResearches <br>
 * For X and Y setting, use {@link #setXExt(int)} and {@link #setYExt(int)} <br>
 * @see ResearchInstance
 */
public class ResearchNode extends AbstractWidget {
    private final UniqueArray<ResearchNode> parents;
    private final UniqueArray<ResearchNode> children;
    private final UniqueArray<ResearchNode> hiddenChildren;

    private final UniqueArray<ResearchNode> positionLocks;
    private boolean doMovementLogic;

    private final ResearchInstance instance;

    private final UniqueArray<ResearchHead> inputs;
    private final UniqueArray<ResearchHead> outputs;

    private boolean rootNode;
    public ResearchGraph graph;

    public ResearchNode(ResearchInstance instance) {
        super(0, 0, ResearchScreenWidget.PANEL_WIDTH, ResearchScreenWidget.PANEL_HEIGHT, CommonComponents.EMPTY);
        this.instance = instance;

        this.children = new UniqueArray<>();
        this.hiddenChildren = new UniqueArray<>();
        this.parents = new UniqueArray<>();
        this.positionLocks = new UniqueArray<>();

        this.inputs = new UniqueArray<>();
        this.outputs = new UniqueArray<>();
        this.rootNode = false;

        this.doMovementLogic = true;
    }

    public void setHovered(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        this.isHovered = guiGraphics.containsPointInScissor(mouseX, mouseY) && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public void addChild(ResearchNode child) {
        this.children.addLast(child);
    }

    public void addParent(ResearchNode parent) {
        this.parents.addLast(parent);
    }

    public void addPositionLock(ResearchNode positionLock) {
        this.positionLocks.add(positionLock);
    }

    public boolean shouldMove() { return doMovementLogic; }

    /**
     * Add a position lock to the node. Disable doing movement logic for this node. <br>
     * @param node
     */
    public void lockNodeTo(ResearchNode node) {
        this.addPositionLock(node);
        this.doMovementLogic = false;
    }

    public void lockNode() {
        this.doMovementLogic = false;
    }

    public Integer getLayer() {
        if (GraphLayoutManager.nodeLayerMap.get(this) == null) {
            return -1;
        }
        return GraphLayoutManager.nodeLayerMap.get(this);
    }

    public UniqueArray<ResearchNode> getChildren() {
        return children;
    }

    public UniqueArray<ResearchNode> getParents() {
        return parents;
    }

    public UniqueArray<ResearchNode> getPositionLocks() {
        return positionLocks;
    }

    public ResearchInstance getInstance() {
        return instance;
    }

    public UniqueArray<ResearchHead> getInputs() {
        return inputs;
    }

    public UniqueArray<ResearchHead> getOutputs() {
        return outputs;
    }

    public UniqueArray<ResearchNode> getHiddenChildren() {
        return hiddenChildren;
    }

    public void refreshHeads() {
        if (this.graph != null) {
            Collection<ResearchNode> nodes = this.graph.nodes().values();

            this.inputs.clear();
            this.inputs.addAll(ResearchHead.inputsOf(this, nodes));

            this.outputs.clear();
            this.outputs.addAll(ResearchHead.outputsOf(this, nodes));
        }
    }

    public boolean isRootNode() {
        return rootNode;
    }

    public void setRootNode(boolean rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        ResearchScreenWidget.renderResearchPanel(guiGraphics, instance,  getX(), getY(), mouseX, mouseY);
        // FIXME: Can probably be removed
        refreshHeads();

        for (ResearchHead input : inputs) {
            input.render(guiGraphics);
        }
        for (ResearchHead output : outputs) {
            output.render(guiGraphics);
        }
    }

    @Override
    public String toString() {
        return "ResearchNode{" +
                "next=" + children +
                '}';
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    /**
     * Extension of {@link #setX(int)} for more logic. Please call this method. <br>
     *
     * @param x1 x coordinate to set
     */
    public void setXExt(int x1) {
        int dx = x1 - getX();
        translate(dx, 0);
    }

    /**
     * Extension of {@link #setY(int)} for more logic. Please call this method. <br>
     *
     * @param y1 y coordinate to set
     */
    public void setYExt(int y1) {
        int dy = y1 - getY();
        translate(0, dy);
    }

    public void translate(int dx, int dy) {
        setX(getX() + dx);
        setY(getY() + dy);

        refreshHeads();
    }


    // Used for shifting node logic
    public void downStream(int dx, int dy) {
        for (ResearchNode child : children) {
            child.translate(dx, dy);
            child.downStream(dx, dy);
        }
    }

    public void downStreamSetX(int x) {
        int dx = x - getX();
        downStream(dx, 0);
    }

    public void downStreamSetY(int y) {
        int dy = y - getY();
        downStream(0, dy);
    }
}
