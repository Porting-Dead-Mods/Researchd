package com.portingdeadmods.researchd.client.screens.widgets;

import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.TechList;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.networking.research.ResearchQueueAddPayload;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;
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
    private static final int BACKGROUND_HEIGHT = 150;
    private static final int BACKGROUND_HEIGHT_SPRITE = 18;
    private static final int BOTTOM_WIDTH = 174;
    private static final int BOTTOM_HEIGHT = 8;
    private static final int PADDING_Y = 21;
    private static final int PADDING_X = 12;

    private final int searchButtonX;
    private final int scrollX;

    private TechList techList;
    private TechList displayTechList;

    private final int cols;
    private int rows;
    private int scrollOffset;
    private ResearchInstance hoveredResearch;

    public final ImageButton searchButton;
    public final Button startResearchButton;
    private final EditBox searchBox;
    private boolean hasSearchBar;
    private final ResearchScreen screen;

    public TechListWidget(ResearchScreen screen, int x, int y, int cols) {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.cols = cols;

        int padding = 15;
        this.searchButtonX = getX() + cols * ResearchScreenWidget.PANEL_WIDTH + padding;
        this.scrollX = getX() + cols * ResearchScreenWidget.PANEL_WIDTH + PADDING_X + 4;

        this.searchButton = new ImageButton(searchButtonX, y + 4, 14, 14, new WidgetSprites(
                Researchd.rl("search_button"),
                Researchd.rl("search_button_highlighted")
        ), this::onSearchButtonClicked);

        Font font = Minecraft.getInstance().font;

        this.startResearchButton = Button.builder(ResearchdTranslations.component(ResearchdTranslations.Research.START_RESEARCH_BUTTON), this::onStartResearchButtonClicked)
                .bounds(11, y + 4, Math.min(font.width(ResearchdTranslations.component(ResearchdTranslations.Research.ENQUEUE_RESEARCH_BUTTON)), 58), 14)
                .build();

        this.searchBox = new EditBox(font, x + 73 + 2, y + 3 + 4, 78, 14, Component.empty()) {
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

    public void setResearchButtonMode(ResearchButtonMode mode) {
        this.startResearchButton.setMessage(mode == ResearchButtonMode.START
                ? ResearchdTranslations.component(ResearchdTranslations.Research.START_RESEARCH_BUTTON)
                : ResearchdTranslations.component(ResearchdTranslations.Research.ENQUEUE_RESEARCH_BUTTON));
    }

    private void refreshSearchResult() {
        this.displayTechList = this.techList.getListForSearch(this.searchBox.getValue());
        this.scrollOffset = 0;

        this.rows = this.displayTechList.entries().size() / this.cols + (this.displayTechList.entries().size() % this.cols != 0 ? 1 : 0);
    }

    public void setTechList(TechList techList) {
        this.scrollOffset = 0;
        this.techList = techList;
        this.displayTechList = techList;

        this.rows = this.displayTechList.entries().size() / this.cols + (this.displayTechList.entries().size() % this.cols != 0 ? 1 : 0);
    }

    public TechList getDisplayTechList() {
        return this.displayTechList;
    }

    public TechList getTechList() {
        return this.techList;
    }

    public void onSearchButtonClicked(Button button) {
        this.hasSearchBar = !this.hasSearchBar;
        this.searchBox.setVisible(this.hasSearchBar);
        this.displayTechList = this.techList;
    }

    public void onStartResearchButtonClicked(Button button) {
        ResearchQueueWidget queue = this.screen.getResearchQueueWidget();
        ResearchInstance selectedInstance = this.screen.getSelectedResearchWidget().getSelectedInstance();
        if (selectedInstance != null) {
            if (queue.getQueue().add(selectedInstance)) {
                UUID player = Minecraft.getInstance().player.getUUID();
                long gameTime = Minecraft.getInstance().level.getDayTime();


                selectedInstance.setResearchedPlayer(player);
                selectedInstance.setResearchedTime(gameTime);

                ResourceKey<Research> researchKey = selectedInstance.getKey();
                PacketDistributor.sendToServer(new ResearchQueueAddPayload(researchKey, player, gameTime));

                // Instantaneous Effect
                ClientResearchTeamHelper.getTeam().refreshResearchStatus();
                ClientResearchTeamHelper.refreshResearchScreenData();
            }

            this.startResearchButton.active = false;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        this.hoveredResearch = null;
        GuiUtils.drawImg(guiGraphics, hasSearchBar ? BACKGROUND_TEXTURE_SEARCH_BAR : BACKGROUND_TEXTURE, getX(), getY() + 3, BACKGROUND_WIDTH, BACKGROUND_HEIGHT_SPRITE);
        int techListHeight = this.getTechListHeight();
        guiGraphics.blit(TECH_LIST_EXPANDABLE, getX(), getY() + BACKGROUND_HEIGHT_SPRITE + 3, 0, 0, 174, techListHeight, 174, 16);
        GuiUtils.drawImg(guiGraphics, BOTTOM_TEXTURE, getX(), getY() + BACKGROUND_HEIGHT_SPRITE + guiGraphics.guiHeight() - getY() - BACKGROUND_HEIGHT_SPRITE - BOTTOM_HEIGHT, BOTTOM_WIDTH, BOTTOM_HEIGHT);

        int paddingX = 12;
        int paddingY = 21;

        guiGraphics.enableScissor(12, 106 + 24, 12 + 140, 106 + 24 + techListHeight - 1);
        {
            for (int row = 0; row < this.rows; row++) {
                for (int col = 0; col < this.cols; col++) {
                    int index = row * this.cols + col;
                    if (index < this.displayTechList.entries().size()) {
                        ResearchInstance instance = this.displayTechList.entries().get(index);
                        int y1 = PADDING_Y + getY() + row * PANEL_HEIGHT - this.scrollOffset;
                        boolean selected = instance == this.screen.getSelectedResearchWidget().getSelectedInstance();
                        if (selected) {
                            y1 += 2;
                        }
                        int x = paddingX + getX() + col * PANEL_WIDTH;
                        if (isHovering(guiGraphics, x, y1, mouseX, mouseY)) {
                            this.hoveredResearch = instance;
                        }
                        if (index >= this.displayTechList.entries().size() - this.cols) {
                            if (selected) {
                                renderSmallResearchPanel(guiGraphics, instance, x, y1, mouseX, mouseY);
                            } else {
                                renderResearchPanel(guiGraphics, instance, x, y1, mouseX, mouseY);
                            }
                        } else {
                            renderTallResearchPanel(guiGraphics, instance, x, y1, mouseX, mouseY);
                        }
                    }
                }
            }
        }
        guiGraphics.disableScissor();

        float percentage = (float) this.scrollOffset / (this.getContentHeight() - techListHeight);
        guiGraphics.blitSprite(SCROLLER_SPRITE, this.scrollX, (int) (getY() + PADDING_Y + (percentage * (techListHeight - SCROLLER_HEIGHT - 1))), SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    private int getTechListHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight() - getY() - BACKGROUND_HEIGHT_SPRITE - BOTTOM_HEIGHT;
    }

    public int getContentHeight() {
        return this.rows * ResearchScreenWidget.PANEL_HEIGHT;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.isHovered(mouseX, mouseY, 12, 130, 156, this.getTechListHeight() - 1) && this.getContentHeight() > this.getTechListHeight()) {
            double rawScrollOffset = (int) Math.max(this.scrollOffset - scrollY * 7, 0);
            if (rawScrollOffset > this.getContentHeight() - this.getTechListHeight() + 1) {
                this.scrollOffset = (this.getContentHeight() - this.getTechListHeight()) + 1;
            } else {
                this.scrollOffset = (int) rawScrollOffset;
            }
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.hoveredResearch != null) {
            this.screen.getResearchGraphWidget().setGraph(ResearchGraphCache.computeIfAbsent(this.hoveredResearch.getKey()));
            this.screen.getSelectedResearchWidget().setSelectedResearch(this.hoveredResearch);
            return super.mouseClicked(mouseX, mouseY, button);
        } else if (
                    mouseX >= this.scrollX &&
                    mouseX < this.scrollX + SCROLLER_WIDTH &&
                    mouseY >= getY() + PADDING_Y &&
                    mouseY < getY() + PADDING_Y + this.getTechListHeight() - 1 &&
                    this.getContentHeight() > this.getTechListHeight()
        ) {
            int scrollableHeight = this.getContentHeight() - this.getTechListHeight();
            int minY = getY() + PADDING_Y + 7;
            int maxY = getY() + PADDING_Y + this.getTechListHeight() - 1 - 8;

            double scrolledPercentage = ((Math.clamp(mouseY, minY, maxY) - (minY))) / (double) (maxY - minY);

            this.scrollOffset = (int) (scrollableHeight * scrolledPercentage);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        return false;
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (
                mouseX >= this.scrollX &&
                mouseX < this.scrollX + SCROLLER_WIDTH &&
                mouseY >= getY() + PADDING_Y &&
                mouseY < getY() + PADDING_Y + this.getTechListHeight() - 1 &&
                this.getContentHeight() > this.getTechListHeight()
        ) {
            this.mouseClicked(mouseX, mouseY, 0);
        }
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

    public enum ResearchButtonMode {
        START,
        ENQUEUE
    }

}
