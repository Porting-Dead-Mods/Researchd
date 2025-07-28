package com.portingdeadmods.researchd.client.screens.lab;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Map;

public class ResearchLabScreen extends PDLAbstractContainerScreen<ResearchLabMenu> {
	public static final Integer SIDE_PADDING = 7;
	public static final Integer TB_PADDING = 12; // Top and Bottom Padding
	public static final Integer SLOTS_PER_ROW = 7;
	public static final Integer SLOT_WIDTH = 18;
	public static final Integer SLOT_HEIGHT = 21; // width + 2px for usage bar + 1px for spacing
	public static final Integer BAR_HEIGHT = 2;
	public static final Integer BAR_WIDTH = SLOT_WIDTH - 2; // Slot width but 1px off each side

	public ResearchLabScreen(ResearchLabMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.width = SIDE_PADDING * 2 + SLOTS_PER_ROW * SLOT_WIDTH;
		this.height = TB_PADDING * 2 + (Researchd.RESEARCH_PACK_COUNT.getOrThrow() / SLOTS_PER_ROW + 1) * SLOT_HEIGHT;
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.drawBars(pGuiGraphics);
	}

	@Override
	public @NotNull ResourceLocation getBackgroundTexture() {
		return Researchd.rl("textures/gui/research_screen/edges/bottom_right.png");
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
