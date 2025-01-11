package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.Research;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ResearchNode extends AbstractWidget {
    private Research research;
    private List<ResearchNode> next;

    public ResearchNode(Research research, int x, int y) {
        super(x, y, 20, 20, Component.empty());
        this.research = research;
    }

    public void setResearch(Research research) {
        this.research = research;
    }

    public Research getResearch() {
        return research;
    }

    public void addNext(ResearchNode next) {
        this.next.add(next);
    }

    public void removeNext(ResearchNode toRemove) {
        this.next.remove(toRemove);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int x, int y, float v) {
        guiGraphics.renderItem(research.icon().getDefaultInstance(), x, y);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
