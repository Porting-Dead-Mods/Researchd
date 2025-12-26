package com.portingdeadmods.researchd.client.screens.research.widgets;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.ClientResearchIcon;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.cache.CommonResearchCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class ResearchPagesList extends AbstractWidget {
    public static final ResourceLocation PAGE_BUTTON_ACTIVE = Researchd.rl("textures/gui/research_screen/research_page_button_active.png");
    public static final ResourceLocation PAGE_BUTTON_INACTIVE = Researchd.rl("textures/gui/research_screen/research_page_button_inactive.png");
    public static final ResourceLocation PAGE_BUTTON_INACTIVE_HOVER = Researchd.rl("textures/gui/research_screen/research_page_button_inactive_hover.png");


    private static final int BUTTON_SIZE = 10;
    public static final int HEIGHT = 243;

    private final ResearchScreen screen;
    private final List<ResearchPage> pages;
    private ResearchPage selectedPage;

    public ResearchPagesList(ResearchScreen screen, int x, int y) {
        super(x, y, BUTTON_SIZE, HEIGHT, Component.empty());
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
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int buttonY = getY();

        for (int i = 0; i < this.pages.size(); i++) {
            ResearchPage page = this.pages.get(i);
            boolean isHovered = isButtonHovered(i, mouseX, mouseY);
            boolean isSelected = page == this.selectedPage;

            // Render icon using the research key (same as ResearchNode does)
            if (isSelected) {
                GuiUtils.drawImg(guiGraphics, PAGE_BUTTON_ACTIVE, getX(), buttonY, BUTTON_SIZE, BUTTON_SIZE);
            } else {
                if (isButtonHovered(i, mouseX, mouseY)) {
                    GuiUtils.drawImg(guiGraphics, PAGE_BUTTON_INACTIVE_HOVER, getX(), buttonY, BUTTON_SIZE, BUTTON_SIZE);
                } else {
                    GuiUtils.drawImg(guiGraphics, PAGE_BUTTON_INACTIVE, getX(), buttonY, BUTTON_SIZE, BUTTON_SIZE);
                }
            }

            if (page.iconResearchKey() != null) {
                ClientResearchIcon<?> clientIcon = ResearchScreen.CLIENT_ICONS.get(page.iconResearchKey().location());
                if (clientIcon != null) {
                    clientIcon.render(guiGraphics, getX(), buttonY, mouseX, mouseY, 0.48f, partialTick);
                }
            }

            buttonY += BUTTON_SIZE;
        }
    }

    private boolean isButtonHovered(int index, int mouseX, int mouseY) {
        int buttonY = getY() + index * BUTTON_SIZE;
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
