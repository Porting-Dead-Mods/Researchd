package com.portingdeadmods.researchd.client.screens.lab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.portingdeadlibs.api.client.screens.widgets.AbstractScroller;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public class ResearchLabScreen extends PDLAbstractContainerScreen<ResearchLabMenu> {
    public static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_lab.png");
    public static final ResourceLocation RESEARCH_PACK_TEXTURE = Researchd.rl("textures/item/research_pack_empty.png");
    public static final ResourceLocation SLOT_SPRITE = Researchd.rl("slot_with_progress");
    public static final int PROGRESS_COLOR = FastColor.ARGB32.color(0, 225, 100);
    public static final int PROGRESS_BAR_WIDTH = 105;
    public static final int SLOT_WIDTH = 18;
    public static final int SLOT_HEIGHT = 20;
    public static final int SCROLLER_X = 8;
    public static final int SCROLLER_Y = 41;
    public static final int SCROLLER_WIDTH = 7;
    public static final int SCROLLER_HEIGHT = 4;
    public static final int SCROLLER_TRACK_LENGTH = 154;

    private final AbstractScroller scroller = new AbstractScroller(this, SCROLLER_X, SCROLLER_Y, SCROLLER_WIDTH, SCROLLER_HEIGHT, SCROLLER_TRACK_LENGTH, AbstractScroller.Mode.HORIZONTAL, Researchd.rl("scroller_small_horizontal")) {
        @Override
        public int getContentLength() {
            return SLOT_WIDTH * Researchd.RESEARCH_PACK_COUNT.getOrThrow();
        };

        @Override
        public int getVisibleContentLength() {
            return 164;
        };

        @Override
        public void onScroll() {
            updateSlotPositions();
        }
    };

    public ResearchLabScreen(ResearchLabMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 198;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 93;

        this.addRenderableWidget(this.scroller);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseX, pPartialTick);
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Background(this, pGuiGraphics, pMouseX, pMouseY));

        for(Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        renderItemsAndSlots(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.scroller.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        // Foreground
        this.drawBars(pGuiGraphics);

        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    public boolean isHovering(GuiGraphics guiGraphics, Slot slot, double mouseX, double mouseY) {
        return guiGraphics.containsPointInScissor((int) mouseX, (int) mouseY) && this.isHovering(slot, mouseX, mouseY);
    }

    private void renderItemsAndSlots(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.disableDepthTest();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float)this.leftPos, (float)this.topPos, 0.0F);
        this.hoveredSlot = null;


        int startX = this.leftPos + 7;
        int startY = this.topPos + 17;
        guiGraphics.enableScissor(startX, startY, startX + SLOT_WIDTH * 9, startY + this.imageHeight);
        {
        for(int k = 0; k < this.menu.slots.size(); ++k) {
            Slot slot = this.menu.slots.get(k);
            if (slot.isActive()) {
                this.renderSlot(guiGraphics, slot);
            }

            if (this.isHovering(guiGraphics, slot, mouseX, mouseY) && slot.isActive()) {
                this.hoveredSlot = slot;
                this.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick);
            }
        }
        }
        guiGraphics.disableScissor();

        this.renderLabels(guiGraphics, mouseX, mouseY);
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(this, guiGraphics, mouseX, mouseY));
        ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
        if (!itemstack.isEmpty()) {
            int l1 = 8;
            int i2 = this.draggingItem.isEmpty() ? 8 : 16;
            String s = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                itemstack = itemstack.copyWithCount(Mth.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                itemstack = itemstack.copyWithCount(this.quickCraftingRemainder);
                if (itemstack.isEmpty()) {
                    s = ChatFormatting.YELLOW + "0";
                }
            }

            this.renderFloatingItem(guiGraphics, itemstack, mouseX - this.leftPos - 8, mouseY - this.topPos - i2, s);
        }

        if (!this.snapbackItem.isEmpty()) {
            float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                this.snapbackItem = ItemStack.EMPTY;
            }

            int j2 = this.snapbackEnd.x - this.snapbackStartX;
            int k2 = this.snapbackEnd.y - this.snapbackStartY;
            int j1 = this.snapbackStartX + (int)((float)j2 * f);
            int k1 = this.snapbackStartY + (int)((float)k2 * f);
            this.renderFloatingItem(guiGraphics, this.snapbackItem, j1, k1, (String)null);
        }

        guiGraphics.pose().popPose();
        RenderSystem.enableDepthTest();
    }

    @Override
    public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    public @NotNull ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
//
//        this.botPos = this.topPos + getYSize();
//        this.rightPos = this.leftPos + getXSize();
//
//        guiGraphics.fill(this.leftPos, this.topPos, this.rightPos, this.botPos, BACKGROUND_COLOR);
//        //Researchd.debug("Research Lab Screen", "Rendering background at: " + this.leftPos + ":" + this.topPos + " -> " + this.rightPos + ":" + this.botPos);
//
//        // Top border
//        guiGraphics.fill(this.leftPos - BORDER_SIZE, this.topPos - BORDER_SIZE, this.rightPos + BORDER_SIZE, this.topPos, BORDER_COLOR);
//
//        // Bottom border
//        guiGraphics.fill(this.leftPos - BORDER_SIZE, this.botPos, this.rightPos + BORDER_SIZE, this.botPos + BORDER_SIZE, BORDER_COLOR);
//
//        // Left border
//        guiGraphics.fill(this.leftPos - BORDER_SIZE, this.topPos - BORDER_SIZE, this.leftPos, this.botPos + BORDER_SIZE, BORDER_COLOR);
//
//        guiGraphics.fill(this.rightPos, this.topPos - BORDER_SIZE, this.rightPos + BORDER_SIZE, this.botPos + BORDER_SIZE, BORDER_COLOR);
        // Right border

//        for (Point point : this.menu.getSlotPositions()) {
//            drawPackSlot(guiGraphics, point.x + 1, point.y + 1);
//        }

        int startX = this.leftPos + 7;
        int startY = this.topPos + 17;
        guiGraphics.enableScissor(startX, startY, startX + 162, startY + SLOT_HEIGHT);
        {
            for (int i = 0; i < this.menu.getResearchPackItems().size(); i++) {
                guiGraphics.blitSprite(SLOT_SPRITE, startX + i * SLOT_WIDTH - this.scroller.getScrollOffset(), startY, SLOT_WIDTH, SLOT_HEIGHT);
                int progress = (int) (this.menu.blockEntity.researchPackUsage.get(this.menu.getResearchPacks().get(i)) * 17);
                guiGraphics.fill(startX + 1 + i * SLOT_WIDTH - this.scroller.getScrollOffset(), startY + SLOT_WIDTH, startX + 1 + i * SLOT_WIDTH + progress - this.scroller.getScrollOffset(), startY + SLOT_WIDTH + 1, PROGRESS_COLOR);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 60f / 255f);
                {
                    guiGraphics.renderFakeItem(this.menu.getResearchPackItems().get(i),
                            startX + i * SLOT_WIDTH + 1 - this.scroller.getScrollOffset(),
                            startY + 1);
                }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
            }
        }
        guiGraphics.disableScissor();

        ResearchTeam team = ClientResearchTeamHelper.getTeam();
        ResearchInstance instance = team.getResearches().get(team.getCurrentResearch());
        if (instance != null) {
            ResearchScreenWidget.renderResearchPanel(guiGraphics, instance, this.leftPos + 123, this.topPos + 51, mouseX, mouseY, 2, false, false);
        }

        int x = this.leftPos + 12;
        int y = this.topPos + 72;
        ResearchMethodProgress<?> rmp = ClientResearchTeamHelper.getTeam().getResearchProgresses().get(team.getCurrentResearch());
        float progress = rmp == null ? 0f : rmp.getProgressPercent();
        int width = (int) (progress * PROGRESS_BAR_WIDTH);
        guiGraphics.fill(x, y, x + width, y + 6, PROGRESS_COLOR);

        guiGraphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf((int) (progress * 100)) + '%', x + 1 + PROGRESS_BAR_WIDTH / 2,  y + 9, 0xF8F8F8);
    }

    /**
     * The slot order is based on the {@link Researchd#RESEARCH_PACKS}'s index order
     *
     * @param guiGraphics GuiGraphics instance for drawing
     */
    private void drawBars(GuiGraphics guiGraphics) {
//        for (Map.Entry<ResourceKey<SimpleResearchPack>, Float> entry : this.menu.blockEntity.researchPackUsage.entrySet()) {
//            SimpleResearchPack pack = Researchd.RESEARCH_PACK_REGISTRY.getOrThrow().get(entry.getKey()).get().value(); // Safe usage of Optional
//            int idx = Researchd.RESEARCH_PACKS.indexOf(pack);
//            Point slotPos = this.menu.getSlotPositions().get(idx);
//            float usage = entry.getValue();
//
//            int left = this.leftPos + slotPos.x;
//            int top = this.topPos + slotPos.y + SLOT_WIDTH;
//            guiGraphics.fill(
//                    left + 1, // LEFT X
//                    top + 1, // TOP Y
//                    left + 1 + (int) (BAR_WIDTH * usage), // RIGHT X
//                    top + 1, // BOTTOM Y
//                    pack.color()
//            );
//        }
    }

    private int getContentWidth() {
        return SLOT_WIDTH * Researchd.RESEARCH_PACK_COUNT.getOrThrow();
    }

    private void drawSlot(GuiGraphics guiGraphics, int x, int y) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        //return this.mouseClicked(mouseX, mouseY, 0);
    }

    private void updateSlotPositions() {
         for (int i = 0; i < this.menu.labSlots.size(); i++) {
            this.menu.labSlots.get(i).x = this.menu.labSlotsX.get(i) - this.scroller.getScrollOffset();
         }
    }

    private void drawPackSlot(GuiGraphics guiGraphics, int x, int y) {
        GuiUtils.ShaderChain.create()
                .grayscale()
                .drawTo(guiGraphics, RESEARCH_PACK_TEXTURE, this.getGuiLeft() + x, this.getGuiTop() + y, 16, 16, GuiUtils.BlendMode.DARKEN);
    }
}
