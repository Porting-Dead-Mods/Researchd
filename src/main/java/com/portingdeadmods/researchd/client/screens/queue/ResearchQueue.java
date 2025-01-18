package com.portingdeadmods.researchd.client.screens.queue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.list.EntryType;
import com.portingdeadmods.researchd.client.screens.list.TechListEntry;
import com.portingdeadmods.researchd.client.screens.widgets.QueueControllsButton;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.registries.Researches;
import com.portingdeadmods.researchd.utils.researches.ResearchGraphCache;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ResearchQueue extends AbstractWidget {
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_queue.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 42;

    private final List<TechListEntry> queue;

    public ImageButton leftButton;
    public ImageButton rightButton;
    public ImageButton removeButton;

    private final ResearchScreen screen;

    public ResearchQueue(ResearchScreen screen, int x, int y) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, Component.empty());
        this.queue = new ArrayList<>();
        this.screen = screen;
        setLeftButton(-1);
        setRightButton(-1);
        setRemoveButton(-1);
    }

    public void addEntry(TechListEntry entry) {
        if (this.queue.size() >= 7) return;
        if (entry.getResearch().getResearchStatus() == EntryType.RESEARCHED) return;

        for (TechListEntry e : this.queue) {
            if (e.getResearch().getResearch().equals(entry.getResearch().getResearch())) return;
        }

        this.queue.add(new TechListEntry(entry.getResearch(), 12 + this.queue.size() * TechListEntry.WIDTH, 17));
    }

    public void removeEntry(int index) {
        System.out.println("Removing entry at index: " + index);
        ArrayList<TechListEntry> copy = new ArrayList<>(this.queue);

        this.queue.clear();
        copy.remove(index);

        for (TechListEntry entry : copy) {
            addEntry(entry);
        }
    }

    public void moveEntry(int index, boolean left) {
        if (left && index == 0) return;
        if (!left && index == this.queue.size() - 1) return;

        if (!left) index++;

        String direction = left ? "left" : "right";
        System.out.println("Moving entry at index " + index + " to the " + direction);

        ArrayList<TechListEntry> copy = new ArrayList<>(this.queue);

        TechListEntry entry1 = copy.get(index - 1);
        TechListEntry entry2 = copy.get(index);

        copy.set(index - 1, entry2);
        copy.set(index, entry1);

        this.queue.clear();
        for (TechListEntry entry : copy) {
            addEntry(entry);
        }
    }

    public void fillList() {
        for (int col = 0; col < 7; col++) {
            this.queue.add(new TechListEntry(new ResearchInstance(Researches.STONE, EntryType.RESEARCHABLE), 12 + col * TechListEntry.WIDTH, 17));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        GuiUtils.drawImg(guiGraphics, BACKGROUND_TEXTURE, getX(), getY(), width, height);

        for (TechListEntry entry : this.queue) {
            entry.render(guiGraphics, i, i1, v);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int paddingX = getX() + 12;
        int paddingY = 17 + getY();
        if (
            mouseX > paddingX &&
            mouseX < paddingX + this.queue.size() * TechListEntry.WIDTH &&
            mouseY > paddingY &&
            mouseY < paddingY + TechListEntry.HEIGHT
        )
        {
            int indexX = ((int) mouseX - paddingX) / TechListEntry.WIDTH;
            setLeftButton(indexX);
            setRightButton(indexX);
            setRemoveButton(indexX);
        }

        return false;
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
