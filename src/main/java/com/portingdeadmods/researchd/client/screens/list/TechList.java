package com.portingdeadmods.researchd.client.screens.list;

import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TechList extends AbstractWidget {
    private final List<List<TechListEntry>> researches;
    private final int rows;
    private final int cols;

    // DEBUG
    private final List<Item> items = List.of(Items.DIAMOND, Items.IRON_AXE, Items.FURNACE, Items.MINECART);

    public TechList(int x, int y, int rows, int cols) {
        super(x, y, TechListEntry.WIDTH * rows, TechListEntry.HEIGHT * cols, Component.empty());
        this.researches = new ArrayList<>();
        this.rows = rows;
        this.cols = cols;
    }

    public void fillList() {
        for (int col = 0; col < cols; col++) {
            List<TechListEntry> entries = new ArrayList<>();
            for (int row = 0; row < rows; row++) {
                RandomSource random = Minecraft.getInstance().level.random;
                int randInt = random.nextInt(0, items.size());
                int randType = random.nextInt(0, 3);
                entries.add(new TechListEntry(SimpleResearch.debug(this.items.get(randInt)), EntryType.values()[randType], getX() + row * TechListEntry.WIDTH, getY() + col * TechListEntry.HEIGHT));
            }
            this.researches.add(entries);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                consumer.accept(this.researches.get(col).get(row));
            }
        }
    }
}
