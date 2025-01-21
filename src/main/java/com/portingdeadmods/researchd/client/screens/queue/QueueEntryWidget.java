package com.portingdeadmods.researchd.client.screens.queue;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.utils.researches.data.TechList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceKey;

public class QueueEntryWidget extends AbstractWidget {
    private final TechListEntry queueEntry;

    public QueueEntryWidget(TechListEntry queueEntry, int x, int y) {
        super(x, y, TechListEntry.WIDTH, TechListEntry.HEIGHT, CommonComponents.EMPTY);
        this.queueEntry = queueEntry.copy();
        this.queueEntry.setX(x);
        this.queueEntry.setY(y);
    }

    public static QueueEntryWidget from(TechListEntry entry) {
        return new QueueEntryWidget(entry, entry.getX(), entry.getY());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        this.queueEntry.render(guiGraphics, mouseX, mouseY, v);

        if (this.isHovered()) {
            guiGraphics.fillGradient(getX(), getY(), getX() + getWidth(), getY() + getHeight(), ChatFormatting.DARK_GRAY.getColor(), ChatFormatting.DARK_GRAY.getColor());
        }
    }

    public TechListEntry getEntry() {
        return this.queueEntry;
    }

    public ResourceKey<Research> getResearch() {
        return this.queueEntry.getResearch().getResearch();
    }

    public ResearchInstance getResearchInstance() {
        return this.queueEntry.getResearch();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
