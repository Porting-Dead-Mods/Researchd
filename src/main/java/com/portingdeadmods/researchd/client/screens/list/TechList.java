package com.portingdeadmods.researchd.client.screens.list;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.ResearchManager;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TechList extends AbstractWidget {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/tech_list_screen.png");
    private static final ResourceLocation BACKGROUND_TEXTURE_SEARCH_BAR = Researchd.rl("textures/gui/tech_list_screen_search_bar.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 150;
    private static final int DISPLAY_ROWS = 5;

    private final List<List<TechListEntry>> researches;
    private final int cols;
    private int curRow;
    private int scrollOffset;
    public final ImageButton button;
    private boolean hasSearchBar;

    // DEBUG
    private final List<Item> items = List.of(Items.DIAMOND, Items.IRON_AXE, Items.FURNACE, Items.MINECART);

    public TechList(ResearchManager manager, int x, int y, int rows, int cols) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, Component.empty());
        this.researches = manager.getEntries(cols);
        int paddingX = 12 + getX();
        int paddingY = 24 + getY();
        manager.setEntryCoordinates(this.researches, paddingX, paddingY);
        this.cols = cols;

        int padding = 15;
        int scrollerX = getX() + cols * TechListEntry.WIDTH + padding;
        this.button = new ImageButton(scrollerX, y + 6, 14, 14, new WidgetSprites(
                Researchd.rl("search_button"),
                Researchd.rl("search_button_highlighted")
        ), this::onButtonClicked);
    }

    public void onButtonClicked(Button button) {
        this.hasSearchBar = !this.hasSearchBar;
        Researchd.LOGGER.debug("Click button");
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, hasSearchBar ? BACKGROUND_TEXTURE_SEARCH_BAR : BACKGROUND_TEXTURE, getX(), getY(), BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        // TODO: Change the y position of the individual researches
        for (int row = curRow; row < curRow + DISPLAY_ROWS; row++) {
            for (int col = 0; col < this.cols; col++) {
                if (this.researches.size() > row) {
                    List<TechListEntry> rowResearches = this.researches.get(row);
                    if (rowResearches.size() > col) {
                        TechListEntry entry = rowResearches.get(col);
                        int paddingY = 24;
                        entry.setY(paddingY + getY() + (row - curRow) * TechListEntry.HEIGHT);
                        entry.render(guiGraphics, mouseX, mouseY, v);
                    }
                }
            }
        }
        int padding = 16;
        int paddingY = 24;
        int scrollerX = getX() + cols * TechListEntry.WIDTH + padding;
        guiGraphics.blitSprite(SCROLLER_SPRITE, scrollerX, getY() + paddingY, SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset = Math.max(Math.min((int) Math.max(this.scrollOffset - scrollY, 0), this.researches.size() - 3), 0);
        this.curRow = this.scrollOffset;
        Researchd.LOGGER.debug("offset: {}, scrollY: {}", scrollOffset, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);
        consumer.accept(this.button);
    }
}
