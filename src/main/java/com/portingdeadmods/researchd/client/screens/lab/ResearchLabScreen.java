package com.portingdeadmods.researchd.client.screens.lab;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Map;

public class ResearchLabScreen extends PDLAbstractContainerScreen<ResearchLabMenu> {
	public static final Integer SIDE_PADDING = 11;
	public static final Integer TOP_PADDING = 25;
	public static final Integer SLOTS_PER_ROW = 9;
	public static final Integer SLOT_WIDTH = 18;
	public static final Integer SLOT_HEIGHT = 21; // width + 2px for usage bar + 1px for spacing
	public static final Integer BAR_HEIGHT = 2;
	public static final Integer BAR_WIDTH = SLOT_WIDTH - 2; // Slot width but 1px off each side

	public static final Integer BACKGROUND_COLOR = FastColor.ARGB32.color(8, 1, 38);
	public static final Integer BORDER_COLOR = FastColor.ARGB32.color(5, 1, 23);
	public static final Integer BORDER_SIZE = 2;

	public Integer botPos;
	public Integer rightPos;
	public Integer imageWidth;
	public Integer imageHeight;

	public ResearchLabScreen(ResearchLabMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = SIDE_PADDING * 2 + SLOTS_PER_ROW * SLOT_WIDTH;
		Researchd.debug("Research Lab Screen", "Width: " + this.imageWidth);

		this.imageHeight = TOP_PADDING + SIDE_PADDING + (Researchd.RESEARCH_PACK_COUNT.getOrThrow() / SLOTS_PER_ROW + 1) * SLOT_HEIGHT;
		Researchd.debug("Research Lab Screen", "Height: " + this.imageHeight);
}

	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		// Background
		this.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);

		// Foreground
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.drawBars(pGuiGraphics);
	}

	@Override
	public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, (TOP_PADDING - this.font.lineHeight) / 2, 0xF8F8F8);
	}

	@Override
	public @NotNull ResourceLocation getBackgroundTexture() {
		return Researchd.rl(""); // We don't use it
	}

	@Override
	public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		this.botPos = this.topPos + getYSize();
		this.rightPos = this.leftPos + getXSize();

		guiGraphics.fill(this.leftPos, this.topPos, this.rightPos, this.botPos, BACKGROUND_COLOR);
		//Researchd.debug("Research Lab Screen", "Rendering background at: " + this.leftPos + ":" + this.topPos + " -> " + this.rightPos + ":" + this.botPos);

		// Top border
		guiGraphics.fill(this.leftPos - BORDER_SIZE, this.topPos - BORDER_SIZE, this.rightPos + BORDER_SIZE, this.topPos, BORDER_COLOR);

		 // Bottom border
		guiGraphics.fill(this.leftPos - BORDER_SIZE, this.botPos, this.rightPos + BORDER_SIZE, this.botPos + BORDER_SIZE, BORDER_COLOR);

		 // Left border
		guiGraphics.fill(this.leftPos - BORDER_SIZE, this.topPos - BORDER_SIZE, this.leftPos, this.botPos + BORDER_SIZE, BORDER_COLOR);

		 // Right border
		guiGraphics.fill(this.rightPos, this.topPos - BORDER_SIZE, this.rightPos + BORDER_SIZE, this.botPos + BORDER_SIZE, BORDER_COLOR);
	}

	/**
	 * The slot order is based on the {@link Researchd#RESEARCH_PACKS}'s index order
	 *
	 * @param guiGraphics GuiGraphics instance for drawing
	 */
	private void drawBars(GuiGraphics guiGraphics) {
		for (Map.Entry<ResourceKey<ResearchPack>, Float> entry : this.menu.blockEntity.researchPackUsage.entrySet()) {
			ResearchPack pack = Researchd.RESEARCH_PACK_REGISTRY.getOrThrow().get(entry.getKey()).get().value(); // Safe usage of Optional
			int idx = Researchd.RESEARCH_PACKS.indexOf(pack);
			Point slotPos = this.menu.getSlotPositions().get(idx);
			float usage = entry.getValue();

			int left = this.leftPos + slotPos.x;
			int top = this.topPos + slotPos.y + SLOT_WIDTH;
			guiGraphics.fill(
					left + 1, // LEFT X
					top + 1, // TOP Y
					left + 1 + (int) (BAR_WIDTH * usage), // RIGHT X
					top + 1, // BOTTOM Y
					pack.color()
			);
		}
	}
}
