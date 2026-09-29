package com.portingdeadmods.researchd.client.screens.lab;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.portingdeadlibs.api.client.screens.widgets.AbstractScroller;
import com.portingdeadmods.portingdeadlibs.client.screens.widgets.EnergyBarWidget;
import com.portingdeadmods.portingdeadlibs.impl.wrappers.NeoEnergyHandlerWrapper;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelperClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class ResearchLabScreen extends PDLAbstractContainerScreen<ResearchLabMenu> {
    public static final Identifier BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_lab.png");
    public static final int BACKGROUND_TEXTURE_SIZE = 256;
    public static final Identifier RESEARCH_PACK_TEXTURE = Researchd.rl("textures/item/research_pack_empty.png");
    public static final Identifier SLOT_SPRITE = Researchd.rl("slot_with_progress");
    public static final int PROGRESS_COLOR = ARGB.color(0, 225, 100);
    public static final int PROGRESS_BAR_WIDTH = 105;
    public static final int SLOT_WIDTH = 18;
    public static final int SLOT_HEIGHT = 20;
    public static final int SCROLLER_X = 160;
    public static final int SCROLLER_Y = 69;
    public static final int SCROLLER_WIDTH = 7;
    public static final int SCROLLER_HEIGHT = 4;
    public static final int SCROLLER_TRACK_LENGTH = 154;
    public static final int ENERGY_BAR_X_OFFSET = 4;
    public static final int ENERGY_BAR_Y_OFFSET = 18;
    public static final int GHOST_PACK_OVERLAY_COLOR = ARGB.color(195, 139, 139, 139);

    private final AbstractScroller scroller =
            new AbstractScroller(
                    this,
                    SCROLLER_X,
                    SCROLLER_Y,
                    SCROLLER_WIDTH,
                    SCROLLER_HEIGHT,
                    SCROLLER_TRACK_LENGTH,
                    AbstractScroller.Mode.HORIZONTAL,
                    Researchd.rl("scroller_small_horizontal")) {
                @Override
                public int getContentLength() {
                    return SLOT_WIDTH * ResearchHelperClient.getResearchPacks().size();
                }

                @Override
                public int getVisibleContentLength() {
                    return 164;
                }

                @Override
                public void onScroll() {
                    updateSlotPositions();
                }
            };

    public ResearchLabScreen(ResearchLabMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 198);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 93;

        this.addRenderableWidget(this.scroller);
    }

    @Override
    protected void init() {
        super.init();

        if (ResearchLabControllerBE.getEnergyUsage() > 0) {
            this.addRenderableWidget(new EnergyBarWidget(
                    this.leftPos + this.imageWidth + ENERGY_BAR_X_OFFSET,
                    this.topPos + ENERGY_BAR_Y_OFFSET,
                    new NeoEnergyHandlerWrapper(this.menu.getBlockEntity().getEnergyHandler()),
                    "FE",
                    true));
        }
    }

    // The Research Pack slots scroll sideways, so they're clipped to the pack row as on 1.21.1
    @Override
    protected void extractSlots(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.enableScissor(7, 17, 7 + SLOT_WIDTH * 9, 17 + this.imageHeight);
        super.extractSlots(guiGraphics, mouseX, mouseY);
        guiGraphics.disableScissor();
    }

    // A Research Pack slot scrolled out of the pack row can't be hovered
    @Override
    public boolean isHovering(Slot slot, double mouseX, double mouseY) {
        if (!super.isHovering(slot, mouseX, mouseY)) return false;
        if (!this.menu.labSlots.contains(slot)) return true;
        int startX = this.leftPos + 7;
        return mouseX >= startX && mouseX < startX + SLOT_WIDTH * 9;
    }

    @Override
    public @NotNull Identifier getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // TODO(26.1 port, PDL): PDL 1.1.15's PDLAbstractContainerScreen blits the background with the image size as
        // the texture size, squeezing the whole 256x256 sheet into the screen, so it's blitted here instead
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                BACKGROUND_TEXTURE_SIZE,
                BACKGROUND_TEXTURE_SIZE);
        //
        //        this.botPos = this.topPos + getYSize();
        //        this.rightPos = this.leftPos + getXSize();
        //
        //        guiGraphics.fill(this.leftPos, this.topPos, this.rightPos, this.botPos, BACKGROUND_COLOR);
        //        //Researchd.debug("Research Lab Screen", "Rendering background at: " + this.leftPos + ":" +
        // this.topPos + " -> " + this.rightPos + ":" + this.botPos);
        //
        //        // Top border
        //        guiGraphics.fill(this.leftPos - BORDER_SIZE, this.topPos - BORDER_SIZE, this.rightPos + BORDER_SIZE,
        // this.topPos, BORDER_COLOR);
        //
        //        // Bottom border
        //        guiGraphics.fill(this.leftPos - BORDER_SIZE, this.botPos, this.rightPos + BORDER_SIZE, this.botPos +
        // BORDER_SIZE, BORDER_COLOR);
        //
        //        // Left border
        //        guiGraphics.fill(this.leftPos - BORDER_SIZE, this.topPos - BORDER_SIZE, this.leftPos, this.botPos +
        // BORDER_SIZE, BORDER_COLOR);
        //
        //        guiGraphics.fill(this.rightPos, this.topPos - BORDER_SIZE, this.rightPos + BORDER_SIZE, this.botPos +
        // BORDER_SIZE, BORDER_COLOR);
        // Right border

        //        for (Point point : this.menu.getSlotPositions()) {
        //            drawPackSlot(guiGraphics, point.x + 1, point.y + 1);
        //        }

        int startX = this.leftPos + 7;
        int startY = this.topPos + 17;
        guiGraphics.enableScissor(startX, startY, startX + 162, startY + SLOT_HEIGHT);
        {
            for (int i = 0; i < this.menu.getResearchPackItems().size(); i++) {
                guiGraphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        SLOT_SPRITE,
                        startX + i * SLOT_WIDTH - this.scroller.getScrollOffset(),
                        startY,
                        SLOT_WIDTH,
                        SLOT_HEIGHT);

                int progress = (int) (this.menu.blockEntity.researchPackUsage.getOrDefault(
                                this.menu.getResearchPacks().get(i), 0f)
                        * 17);
                guiGraphics.fill(
                        startX + 1 + i * SLOT_WIDTH - this.scroller.getScrollOffset(),
                        startY + SLOT_WIDTH,
                        startX + 1 + i * SLOT_WIDTH + progress - this.scroller.getScrollOffset(),
                        startY + SLOT_WIDTH + 1,
                        PROGRESS_COLOR);
                // 26.1 GUI items take no tint, so the ghost pack is faded with a slot-coloured overlay instead
                // of being drawn at 60/255 alpha
                int itemX = startX + i * SLOT_WIDTH + 1 - this.scroller.getScrollOffset();
                guiGraphics.fakeItem(this.menu.getResearchPackItems().get(i), itemX, startY + 1);
                guiGraphics.fill(itemX, startY + 1, itemX + 16, startY + 17, GHOST_PACK_OVERLAY_COLOR);
            }
        }
        guiGraphics.disableScissor();

        ResearchTeam team = ResearchTeamHelperClient.getTeam();
        if (team == null) return;

        ResearchInstance instance = team.getResearches().get(team.getCurrentResearch());
        if (instance != null) {
            ResearchScreenWidget.renderResearchPanel(
                    guiGraphics, instance, this.leftPos + 123, this.topPos + 51, mouseX, mouseY, 2, false, false);

            if (ResearchScreenWidget.isPanelHovered(this.leftPos + 123, this.topPos + 51, mouseX, mouseY, 2)) {
                guiGraphics.setTooltipForNextFrame(
                        Minecraft.getInstance().font,
                        Component.literal("Open Research in Research Screen"),
                        mouseX,
                        mouseY);
            }
        }

        int x = this.leftPos + 12;
        int y = this.topPos + 72;
        ResearchProgress rp = team.getCurrentProgress();
        float progress = rp == null ? 0f : (rp.getProgress() / rp.getMaxProgress());
        int width = (int) (progress * PROGRESS_BAR_WIDTH);
        guiGraphics.fill(x, y, x + width, y + 6, PROGRESS_COLOR);

        guiGraphics.centeredText(
                Minecraft.getInstance().font,
                String.valueOf((int) (progress * 100)) + '%',
                x + 1 + PROGRESS_BAR_WIDTH / 2,
                y + 9,
                0xFFF8F8F8);
    }

    private int getContentWidth() {
        return SLOT_WIDTH * ResearchHelperClient.getResearchPacks().size();
    }

    private void drawSlot(GuiGraphicsExtractor guiGraphics, int x, int y) {}

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (ResearchScreenWidget.isPanelHovered(this.leftPos + 123, this.topPos + 51, (int) mouseX, (int) mouseY, 2)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                ResearchTeam team = ResearchTeamHelperClient.getTeam();
                if (team != null && team.getCurrentResearch() != null) {
                    ResearchdApi.openScreenForResearch(team.getCurrentResearch());
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return super.mouseDragged(event, dragX, dragY);
        // return this.mouseClicked(mouseX, mouseY, 0);
    }

    private void updateSlotPositions() {
        for (int i = 0; i < this.menu.labSlots.size(); i++) {
            this.menu.labSlots.get(i).x = this.menu.labSlotsX.get(i) - this.scroller.getScrollOffset();
        }
    }
}
