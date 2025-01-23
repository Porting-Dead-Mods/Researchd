package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

import java.util.HashSet;
import java.util.Set;

public class ResearchNode extends AbstractWidget {
    private final Set<ResearchNode> next;
    private final ResearchInstance instance;

    public ResearchNode(ResearchInstance instance) {
        super(0, 0, ResearchScreenWidget.PANEL_WIDTH, ResearchScreenWidget.PANEL_HEIGHT, CommonComponents.EMPTY);
        this.instance = instance;
        this.next = new HashSet<>();
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
}
