package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ResearchNode extends TechListEntry {
    private final List<ResearchNode> next;

    public ResearchNode(Research research, EntryType type, int x, int y) {
        super(research, type, x, y);
        this.next = new ArrayList<>();
    }

    public void addNext(ResearchNode next) {
        this.next.add(next);
    }

    public void removeNext(ResearchNode toRemove) {
        this.next.remove(toRemove);
    }

    public List<ResearchNode> getNext() {
        return next;
    }
}
