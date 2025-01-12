package com.portingdeadmods.researchd.client.screens.graph;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;

public class ResearchNode extends TechListEntry {
    private final List<ResearchNode> next;
    private final Holder<Research> holder;

    public ResearchNode(Holder<Research> research) {
        super(research.value(), EntryType.LOCKED, 0, 0);
        this.next = new ArrayList<>();
        this.holder = research;
    }

    public Holder<Research> getHolder() {
        return holder;
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

    @Override
    public String toString() {
        return "ResearchNode{" +
                "next=" + next +
                ", holder=" + holder +
                '}';
    }
}
