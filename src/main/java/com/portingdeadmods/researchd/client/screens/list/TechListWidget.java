package com.portingdeadmods.researchd.client.screens.list;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.utils.researches.TechList;
import com.portingdeadmods.researchd.utils.researches.TechListHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class TechListWidget extends AbstractWidget {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/tech_list_screen.png");
    private static final ResourceLocation BACKGROUND_TEXTURE_SEARCH_BAR = Researchd.rl("textures/gui/tech_list_screen_search_bar.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 150;
    private static final int DISPLAY_ROWS = 5;

    private TechList techList;

    private final int cols;
    private int curRow;
    private int scrollOffset;
    public final ImageButton button;
    private boolean hasSearchBar;

    public TechListWidget(int x, int y, int rows, int cols) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, Component.empty());
        this.cols = cols;
        int padding = 15;
        int scrollerX = getX() + cols * TechListEntry.WIDTH + padding;

        this.button = new ImageButton(scrollerX, y + 5, 14, 14, new WidgetSprites(
                Researchd.rl("search_button"),
                Researchd.rl("search_button_highlighted")
        ), this::onButtonClicked);
    }

    public void setTechList(TechList techList) {
        this.techList = techList;

        int paddingX = 12 + getX();
        int paddingY = 24 + getY();
        TechListHelper.setEntryCoordinates(this.techList, 7, paddingX, paddingY);
    }

    public void onButtonClicked(Button button) {
        this.hasSearchBar = !this.hasSearchBar;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, hasSearchBar ? BACKGROUND_TEXTURE_SEARCH_BAR : BACKGROUND_TEXTURE, getX(), getY(), BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        int col = 0;
        int row = 0;
        for (TechListEntry entry : this.techList.entries()) {
            int paddingY = 24;
            entry.setY(paddingY + getY() + (row - curRow) * TechListEntry.HEIGHT);
            entry.render(guiGraphics, mouseX, mouseY, v);
            if (col < cols) {
                col++;
            } else {
                col = 0;
                row++;
            }
        }

        int padding = 16;
        int paddingY = 24;
        int scrollerX = getX() + cols * TechListEntry.WIDTH + padding;
        guiGraphics.blitSprite(SCROLLER_SPRITE, scrollerX, getY() + paddingY, SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset = Math.max(Math.min((int) Math.max(this.scrollOffset - scrollY, 0), this.techList.entries().size() - 3), 0);
        this.curRow = this.scrollOffset;
        Researchd.LOGGER.debug("offset: {}, scrollY: {}", scrollOffset, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int paddingX = getX() + 12;
        int paddingY = 24 + getY();
        if (mouseX > paddingX && mouseX < paddingX + this.cols * TechListEntry.WIDTH
                && mouseY > paddingY && mouseY < paddingY + DISPLAY_ROWS * TechListEntry.HEIGHT) {
            int indexX = ((int) mouseX - paddingX) / TechListEntry.WIDTH;
            int indexY = ((int) mouseY - paddingY) / TechListEntry.HEIGHT;

            if ((this.techList.entries().size() / this.cols) > indexY) {
                TechListEntry entry = this.techList.entries().get(indexY * this.cols + indexX);
                entry.getResearch().setResearchStatus(EntryType.RESEARCHED);
            }
        }

        return false;
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
