package com.portingdeadmods.researchd.client.screens.lab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.portingdeadlibs.utils.renderers.GuiUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ResearchLabScreen extends PDLAbstractContainerScreen<ResearchLabMenu> {
    public static final ResourceLocation BACKGROUND_TEXTURE = Researchd.rl("textures/gui/research_lab.png");
    public static final ResourceLocation RESEARCH_PACK_TEXTURE = Researchd.rl("textures/item/research_pack_empty.png");
    public static final ResourceLocation SLOT_SPRITE = Researchd.rl("slot_with_progress");
    public static final ResourceLocation SCROLLER_HORIZONTAL = Researchd.rl("scroller_small_horizontal");
    public static final int PROGRESS_COLOR = FastColor.ARGB32.color(0, 225, 100);

    public ResearchLabScreen(ResearchLabMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 198;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        // Foreground
        this.drawBars(pGuiGraphics);
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
        guiGraphics.enableScissor(startX, startY, startX + 162, startY + 20);
        {
            for (int i = 0; i < this.menu.getResearchPackItems().size(); i++) {
                guiGraphics.blitSprite(SLOT_SPRITE, startX + i * 18, startY, 18, 20);
                int progress = (int) (this.menu.blockEntity.researchPackUsage.get(this.menu.getResearchPacks().get(i)) * 17);
                guiGraphics.fill(startX + 1 + i * 18, startY + 18, startX + 1 + i * 18 + progress, startY + 18 + 1, PROGRESS_COLOR);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 60f / 255f);
                {
                    guiGraphics.renderFakeItem(this.menu.getResearchPackItems().get(i),
                            startX + i * 18 + 1,
                            startY + 1);
                }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
            }
        }
        guiGraphics.disableScissor();

        ResearchTeam team = ClientResearchTeamHelper.getTeam();
        ResearchInstance instance = team.getResearches().get(team.getFirstQueueResearch());
        if (instance != null) {
            ResearchScreenWidget.renderResearchPanel(guiGraphics, instance, this.leftPos + 123, this.topPos + 51, mouseX, mouseY, 2, false, false);
        }

        int x = this.leftPos + 12;
        int y = this.topPos + 72;
        ResearchMethodProgress rmp = ClientResearchTeamHelper.getTeam().getResearchProgress().getRootProgress(team.getFirstQueueResearch());
        float progress = rmp == null ? 0f : rmp.getProgressPercent();
        int width = (int) (progress * 93);
        guiGraphics.fill(x, y, x + width, y + 6, PROGRESS_COLOR);

        guiGraphics.drawString(Minecraft.getInstance().font, "0%", this.leftPos + 108,  this.topPos + 71, 0xF8F8F8);

        guiGraphics.blitSprite(SCROLLER_HORIZONTAL, this.leftPos + 8, this.topPos + 41, 7, 4);
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

    private void drawSlot(GuiGraphics guiGraphics, int x, int y) {
    }

    private void drawPackSlot(GuiGraphics guiGraphics, int x, int y) {
        GuiUtils.ShaderChain.create()
                .grayscale()
                .drawTo(guiGraphics, RESEARCH_PACK_TEXTURE, this.getGuiLeft() + x, this.getGuiTop() + y, 16, 16, GuiUtils.BlendMode.DARKEN);
    }
}
