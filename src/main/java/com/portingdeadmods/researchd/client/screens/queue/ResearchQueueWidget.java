package com.portingdeadmods.researchd.client.screens.queue;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.client.screens.widgets.QueueControllsButton;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ResearchQueueWidget extends AbstractWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_queue.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 42;

    private final List<QueueEntryWidget> widgetQueue;
    private final ResearchQueue queue;
    private final ResearchScreen screen;

    public ImageButton leftButton;
    public ImageButton rightButton;
    public ImageButton removeButton;

    public ResearchQueueWidget(ResearchScreen screen, int x, int y) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, Component.empty());
        this.screen = screen;
        this.widgetQueue = new ArrayList<>();
        setLeftButton(-1);
        setRightButton(-1);
        setRemoveButton(-1);
        this.queue = new ResearchQueue();
    }

    public void addEntry(TechListEntry entry) {
        if (this.widgetQueue.size() >= 7) return;
        if (entry.getResearch().getResearchStatus() == EntryType.RESEARCHED) return;

        for (QueueEntryWidget e : this.widgetQueue) {
            if (e.getResearch().equals(entry.getResearch().getResearch())) return;
        }

        int index = this.queue.entries().size();
        QueueEntryWidget widget = new QueueEntryWidget(this.screen, entry, 12 + this.widgetQueue.size() * TechListEntry.WIDTH, 17, index);
        this.queue.add(entry.getResearch());
        this.widgetQueue.add(widget);
        widget.setQueue(this.queue);
    }

    public void removeEntry(int index) {
        System.out.println("Removing entry at index: " + index);
        List<QueueEntryWidget> copy = new ArrayList<>(this.widgetQueue);

        this.widgetQueue.clear();
        copy.remove(index);

        for (QueueEntryWidget entry : copy) {
            addEntry(entry.getEntry());
        }
    }

    public void moveEntry(int index, boolean left) {
        if (left && index == 0) return;
        if (!left && index == this.widgetQueue.size() - 1) return;

        if (!left) index++;

        String direction = left ? "left" : "right";
        System.out.println("Moving entry at index " + index + " to the " + direction);

        List<QueueEntryWidget> copy = new ArrayList<>(this.widgetQueue);

        QueueEntryWidget entry1 = copy.get(index - 1);
        QueueEntryWidget entry2 = copy.get(index);

        copy.set(index - 1, entry2);
        copy.set(index, entry1);

        this.widgetQueue.clear();
        for (QueueEntryWidget entry : copy) {
            addEntry(entry.getEntry());
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);

        for (QueueEntryWidget entry : this.widgetQueue) {
            entry.render(guiGraphics, i, i1, v);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (QueueEntryWidget widget : this.widgetQueue) {
            if (widget.isHovered()) {
                return widget.mouseClicked(mouseX, mouseY, button);
            }
        }
        return false;
    }

    public void removeResearch(int index) {
        if (this.queue.entries().size() > index) {
            this.queue.remove(index);
            this.widgetQueue.remove(index);
        }
    }

    private void setLeftButton(int index) {
        System.out.println("Setting left button to index: " + index);
        if (index == -1) {
            this.leftButton = new QueueControllsButton(index, "left", 12 + index * TechListEntry.WIDTH, 17, 0, 0, new WidgetSprites(
                    Researchd.rl("left_button"),
                    Researchd.rl("left_button_highlighted")
            ), button -> moveEntry(index, true), CommonComponents.EMPTY);
        }
        this.leftButton = new QueueControllsButton(index, "left", 12 + index * TechListEntry.WIDTH, 17 + TechListEntry.HEIGHT + 2, 7, 4, new WidgetSprites(
                Researchd.rl("left_button"),
                Researchd.rl("left_button_highlighted")
        ), button -> moveEntry(index, true), CommonComponents.EMPTY);
    }

    private void setRightButton(int index) {
        System.out.println("Setting right button to index: " + index);
        if (index == -1) {
            this.rightButton = new QueueControllsButton(index, "right", 12 + index * TechListEntry.WIDTH + 16, 17, 0, 0, new WidgetSprites(
                    Researchd.rl("right_button"),
                    Researchd.rl("right_button_highlighted")
            ), button -> moveEntry(index, false), CommonComponents.EMPTY);
        }
        this.rightButton = new QueueControllsButton(index, "right", 12 + index * TechListEntry.WIDTH + 7 + 4 + 2, 17 + TechListEntry.HEIGHT + 2, 7, 4, new WidgetSprites(
                Researchd.rl("right_button"),
                Researchd.rl("right_button_highlighted")
        ), button -> moveEntry(index, false), CommonComponents.EMPTY);
    }

    private void setRemoveButton(int index) {
        System.out.println("Setting remove button to index: " + index);
        if (index == -1) {
            this.removeButton = new QueueControllsButton(index, "remove", 12 + index * TechListEntry.WIDTH + 32, 17, 0, 0, new WidgetSprites(
                    Researchd.rl("remove_button"),
                    Researchd.rl("remove_button_highlighted")
            ), button -> removeEntry(index), CommonComponents.EMPTY);
        }
        this.removeButton = new QueueControllsButton(index, "remove", 12 + index * TechListEntry.WIDTH + 7 + 1, 17 + TechListEntry.HEIGHT + 2, 4, 4, new WidgetSprites(
                Researchd.rl("remove_button"),
                Researchd.rl("remove_button_highlighted")
        ), button -> removeEntry(index), CommonComponents.EMPTY);
    }
}
