package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.lines.ResearchHead;
import com.portingdeadmods.researchd.client.screens.lines.ResearchLine;
import com.portingdeadmods.researchd.utils.Spaghetti;
import com.portingdeadmods.researchd.utils.UniqueArray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Drawable widget for researches <br>
 * For X and Y setting, use {@link #setXExt(int)} and {@link #setYExt(int)} <br>
 * @see ResearchInstance
 */
public class ResearchNode extends AbstractWidget {
    private final UniqueArray<ResearchNode> parents;
    private final UniqueArray<ResearchNode> children;
    private final ResearchInstance instance;

    private final UniqueArray<ResearchHead> inputs;
    private final UniqueArray<ResearchHead> outputs;

    private boolean rootNode;

    public ResearchNode(ResearchInstance instance) {
        super(0, 0, ResearchScreenWidget.PANEL_WIDTH, ResearchScreenWidget.PANEL_HEIGHT, CommonComponents.EMPTY);
        this.instance = instance;
        this.children = new UniqueArray<>();
        this.parents = new UniqueArray<>();
        this.inputs = new UniqueArray<>();
        this.outputs = new UniqueArray<>();
        this.rootNode = false;
    }

    public void addChild(ResearchNode child) {
        this.children.addLast(child);
    }

    public void addParent(ResearchNode parent) {
        this.parents.addLast(parent);
    }

    public UniqueArray<ResearchNode> getChildren() {
        return children;
    }

    public UniqueArray<ResearchNode> getParents() {
        return parents;
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

    public void refreshHeads(Set<ResearchNode> visibleNodes) {
        this.inputs.clear();
        this.inputs.addAll(ResearchHead.inputsOf(this, visibleNodes));

        this.outputs.clear();
        this.outputs.addAll(ResearchHead.outputsOf(this, visibleNodes));
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
        refreshHeads(Spaghetti.getNodesFromScreen());

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
     * Extension of {@link #setX(int)} to update the input and output lines <br>
     *
     * @param x1 x coordinate to set
     */
    public void setXExt(int x1) {
        setX(x1);

        refreshHeads(Spaghetti.getNodesFromScreen());
    }

    /**
     * Extension of {@link #setY(int)} to update the input and output lines <br>
     *
     * @param y1 y coordinate to set
     */
    public void setYExt(int y1) {
        setY(y1);

        refreshHeads(Spaghetti.getNodesFromScreen());
    }

    public void translate(int dx, int dy) {
        setXExt(getX() + dx);
        setYExt(getY() + dy);
    }
}
