package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.lines.ResearchLine;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

import java.util.HashSet;
import java.util.Set;

/**
 * Drawable widget for researches <br>
 * For X and Y setting, use {@link #setXExt(int)} and {@link #setYExt(int)} <br>
 * @see ResearchInstance
 */
public class ResearchNode extends AbstractWidget {
    private final Set<ResearchNode> next;
    private final ResearchInstance instance;
    private final ResearchLine inputs;
    private final ResearchLine outputs;

    public ResearchNode(ResearchInstance instance) {
        super(0, 0, ResearchScreenWidget.PANEL_WIDTH, ResearchScreenWidget.PANEL_HEIGHT, CommonComponents.EMPTY);
        this.instance = instance;
        this.next = new HashSet<>();
        this.inputs = ResearchLine.getInputResearchHeads(this);
        this.outputs = ResearchLine.getOutputResearchHeads(this);
    }

    public void addNext(ResearchNode next) {
        this.next.add(next);
    }

    public void removeNext(ResearchNode toRemove) {
        this.next.remove(toRemove);
    }

    public Set<ResearchNode> getNext() {
        return next;
    }

    public ResearchInstance getInstance() {
        return instance;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        ResearchScreenWidget.renderResearchPanel(guiGraphics, instance,  getX(), getY(), mouseX, mouseY);
        this.inputs.render(guiGraphics, mouseX, mouseY, v);
        this.outputs.render(guiGraphics, mouseX, mouseY, v);
    }

    @Override
    public String toString() {
        return "ResearchNode{" +
                "next=" + next +
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
        int dx = x1 - getX();
        setX(x1);

        this.inputs.translateX(dx);
        this.outputs.translateX(dx);
    }

    /**
     * Extension of {@link #setY(int)} to update the input and output lines <br>
     *
     * @param y1 y coordinate to set
     */
    public void setYExt(int y1) {
        int dy = y1 - getY();
        setY(y1);

        this.inputs.translateY(dy);
        this.outputs.translateY(dy);
    }
}
