package com.portingdeadmods.researchd.client.screens.research.widgets;

import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class ResearchPagesList extends AbstractWidget {
    private static final int BUTTON_SIZE = 24;
    private static final int BUTTON_PADDING = 2;

    private final ResearchScreen screen;
    private final List<ResearchPage> pages;
    private ResearchPage selectedPage;

    public ResearchPagesList(ResearchScreen screen, int x, int y) {
        super(x, y, BUTTON_SIZE, 0, CommonComponents.EMPTY);
        this.screen = screen;
        this.pages = new ArrayList<>();

        if (CommonResearchCache.researchPages != null) {
            this.pages.addAll(CommonResearchCache.researchPages.values());
            if (!this.pages.isEmpty()) {
                this.selectedPage = CommonResearchCache.researchPages.get(ResearchPage.DEFAULT_PAGE_ID);
                if (this.selectedPage == null) {
                    this.selectedPage = this.pages.getFirst();
                }
            }
        }

        this.height = this.pages.size() * (BUTTON_SIZE + BUTTON_PADDING) - BUTTON_PADDING;
    }

    public void refreshPages() {
        this.pages.clear();
        if (CommonResearchCache.researchPages != null) {
            this.pages.addAll(CommonResearchCache.researchPages.values());
            if (!this.pages.isEmpty() && this.selectedPage == null) {
                this.selectedPage = CommonResearchCache.researchPages.get(ResearchPage.DEFAULT_PAGE_ID);
                if (this.selectedPage == null) {
                    this.selectedPage = this.pages.getFirst();
                }
            }
        }
        this.height = this.pages.size() * (BUTTON_SIZE + BUTTON_PADDING) - BUTTON_PADDING;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int buttonY = getY();

        for (int i = 0; i < this.pages.size(); i++) {
            ResearchPage page = this.pages.get(i);
            boolean isHovered = isButtonHovered(i, mouseX, mouseY);
            boolean isSelected = page == this.selectedPage;

            // TODO: Add a blur as a background for the list itself, the tab hover should lighten the blur or smth

            // Render icon using the research key (same as ResearchNode does)
            if (page.iconResearchKey() != null) {
                ClientResearchIcon<?> clientIcon = ResearchScreen.CLIENT_ICONS.get(page.iconResearchKey().location());
                if (clientIcon != null) {
                    clientIcon.render(guiGraphics, getX() + 4, buttonY + 4, mouseX, mouseY, 1.0f, partialTick);
                }
            }

            buttonY += BUTTON_SIZE + BUTTON_PADDING;
        }
    }

    private boolean isButtonHovered(int index, int mouseX, int mouseY) {
        int buttonY = getY() + index * (BUTTON_SIZE + BUTTON_PADDING);
        return mouseX >= getX() && mouseX < getX() + BUTTON_SIZE
                && mouseY >= buttonY && mouseY < buttonY + BUTTON_SIZE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < this.pages.size(); i++) {
                if (isButtonHovered(i, (int) mouseX, (int) mouseY)) {
                    ResearchPage clickedPage = this.pages.get(i);
                    if (clickedPage != this.selectedPage) {
                        this.selectedPage = clickedPage;
                        onPageSelected(clickedPage);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void onPageSelected(ResearchPage page) {
        // Notify the screen that the page has changed
        // The screen will filter the graph to show only researches from this page
        this.screen.onResearchPageChanged(page);
    }

    public ResearchPage getSelectedPage() {
        return this.selectedPage;
    }

    public void setSelectedPage(ResearchPage page) {
        if (this.pages.contains(page)) {
            this.selectedPage = page;
        }
    }

    public List<ResearchPage> getPages() {
        return this.pages;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        consumer.accept(this);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
