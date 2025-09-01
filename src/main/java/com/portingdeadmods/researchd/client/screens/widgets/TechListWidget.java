package com.portingdeadmods.researchd.client.screens.widgets;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.TechList;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.networking.research.ResearchQueueAddPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Consumer;

// TODO: Selected list entries are offset a bit so it looks like they are pressed (like keys on a keyboard)
public class TechListWidget extends ResearchScreenWidget {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/tech_list_screen.png");
    private static final ResourceLocation BOTTOM_TEXTURE = Researchd.rl("textures/gui/tech_list_bottom.png");
    private static final ResourceLocation BACKGROUND_TEXTURE_SEARCH_BAR = Researchd.rl("textures/gui/tech_list_screen_search_bar.png");
    private static final ResourceLocation TECH_LIST_EXPANDABLE = Researchd.rl("textures/gui/research_screen/tech_list_expandable.png");
    private static final int BACKGROUND_WIDTH = 174;
    private static final int BACKGROUND_HEIGHT = 18;
    private static final int BOTTOM_WIDTH = 174;
    private static final int BOTTOM_HEIGHT = 8;
    private static final int DISPLAY_ROWS = 5;

    private TechList techList;
    private TechList displayTechList;

    private final int cols;
    private int curRow;
    private int scrollOffset;
    public final ImageButton searchButton;
    public final Button startResearchButton;
    private final EditBox searchBox;
    private boolean hasSearchBar;
    private final ResearchScreen screen;

    public TechListWidget(ResearchScreen screen, int x, int y, int cols) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.cols = cols;
        int padding = 15;
        int scrollerX = getX() + cols * ResearchScreenWidget.PANEL_WIDTH + padding;

        this.searchButton = new ImageButton(scrollerX, y + 4, 14, 14, new WidgetSprites(
                Researchd.rl("search_button"),
                Researchd.rl("search_button_highlighted")
        ), this::onSearchButtonClicked);

        this.startResearchButton = Button.builder(Component.literal("Start"), this::onStartResearchButtonClicked)
                .bounds(11, y + 4, 32, 14)
                .build();

        this.searchBox = new EditBox(Minecraft.getInstance().font, x + 73 + 2, y + 3 + 4, 78, 14, Component.empty()) {
            @Override
            public boolean charTyped(char codePoint, int modifiers) {
                String searchValue = this.getValue();
                boolean typed = super.charTyped(codePoint, modifiers);
                String newValue = this.getValue();
                if (!searchValue.equals(newValue)) {
                    refreshSearchResult();
                }
                return typed;
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                String searchValue = this.getValue();
                boolean pressed = super.keyPressed(keyCode, scanCode, modifiers);
                String newValue = this.getValue();
                if (!searchValue.equals(newValue)) {
                    refreshSearchResult();
                }
                return pressed;
            }

            @Override
            public void setValue(String text) {
                String searchValue = this.getValue();
                super.setValue(text);
                String newValue = this.getValue();
                if (!searchValue.equals(newValue)) {
                    refreshSearchResult();
                }
            }
        };
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(false);

        this.screen = screen;
    }

    private void refreshSearchResult() {

    }

    public void setTechList(TechList techList) {
        this.techList = techList;
        this.displayTechList = techList;
    }

    public TechList getTechList() {
        return techList;
    }

    public void onSearchButtonClicked(Button button) {
        this.hasSearchBar = !this.hasSearchBar;
        this.searchBox.setVisible(this.hasSearchBar);
    }

    public void onStartResearchButtonClicked(Button button) {
        ResearchQueueWidget queue = this.screen.getResearchQueueWidget();
        ResearchInstance instance = this.screen.getSelectedResearchWidget().getSelectedInstance();
        
        if (instance == null) {
            // No research selected, cannot start research
            return;
        }
        
        instance.setResearchedPlayer(Minecraft.getInstance().player.getUUID());
        instance.setResearchedTime(Minecraft.getInstance().level.getGameTime());
        queue.getQueue().add(instance);
        // TODO: Make this dynamic
        queue.getQueue().setMaxResearchProgress(1000);
        PacketDistributor.sendToServer(new ResearchQueueAddPayload(instance));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        GuiUtils.drawImg(guiGraphics, hasSearchBar ? BACKGROUND_TEXTURE_SEARCH_BAR : BACKGROUND_TEXTURE, getX(), getY() + 3, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        guiGraphics.blit(TECH_LIST_EXPANDABLE, getX(), getY() + BACKGROUND_HEIGHT + 3, 0, 0, 174, guiGraphics.guiHeight() - getY() - BACKGROUND_HEIGHT - BOTTOM_HEIGHT, 174, 16);
        GuiUtils.drawImg(guiGraphics, BOTTOM_TEXTURE, getX(), getY() + BACKGROUND_HEIGHT + guiGraphics.guiHeight() - getY() - BACKGROUND_HEIGHT - BOTTOM_HEIGHT, BOTTOM_WIDTH, BOTTOM_HEIGHT);

        int paddingX = 12;
        int paddingY = 21;

        for (int y = curRow; y < DISPLAY_ROWS; y++) {
            for (int x = 0; x < this.cols; x++) {
                int index = y * this.cols + x;
                if (index < this.techList.entries().size()) {
                    ResearchInstance instance = this.techList.entries().get(index);
                    int y1 = paddingY + getY() + y * PANEL_HEIGHT - (this.scrollOffset * PANEL_HEIGHT);
                    boolean selected = instance == this.screen.getSelectedResearchWidget().getSelectedInstance();
                    if (selected) {
                        y1 += 2;
                    }
                    if (index >= this.techList.entries().size() - this.cols) {
                        if (selected) {
                            renderSmallResearchPanel(guiGraphics, instance, paddingX + getX() + x * PANEL_WIDTH, y1, mouseX, mouseY);
                        } else {
                            renderResearchPanel(guiGraphics, instance, paddingX + getX() + x * PANEL_WIDTH, y1, mouseX, mouseY);
                        }
                    } else {
                        renderTallResearchPanel(guiGraphics, instance, paddingX + getX() + x * PANEL_WIDTH, y1, mouseX, mouseY);
                    }
                }
            }
        }

        int scrollerX = getX() + cols * ResearchScreenWidget.PANEL_WIDTH + paddingX + 4;
        guiGraphics.blitSprite(SCROLLER_SPRITE, scrollerX, getY() + paddingY, SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset = Math.max(Math.min((int) Math.max(this.scrollOffset - scrollY, 0), this.techList.entries().size() - 3), 0);
        this.curRow = this.scrollOffset;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int paddingX = getX() + 12;
        int paddingY = 24 + getY();
        if (mouseX > paddingX && mouseX < paddingX + this.cols * ResearchScreenWidget.PANEL_WIDTH
                && mouseY > paddingY && mouseY < paddingY + DISPLAY_ROWS * ResearchScreenWidget.PANEL_HEIGHT) {
            int indexX = ((int) mouseX - paddingX) / ResearchScreenWidget.PANEL_WIDTH;
            int indexY = ((int) mouseY - paddingY) / ResearchScreenWidget.PANEL_HEIGHT;

            int index = indexY * this.cols + indexX;
            if (index < this.techList.entries().size()) {
                this.screen.prevSelectedResearchMethodWidget = this.screen.getSelectedResearchWidget().methodWidget;
                ResearchInstance instance = this.techList.entries().get(index);
                this.screen.getResearchGraphWidget().setGraph(ResearchGraphCache.computeIfAbsent(instance.getKey()));
                this.screen.getSelectedResearchWidget().setSelectedResearch(instance);
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }

        return false;
    }

    private boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);
        consumer.accept(this.searchButton);
        consumer.accept(this.startResearchButton);
        consumer.accept(this.searchBox);
    }
}
