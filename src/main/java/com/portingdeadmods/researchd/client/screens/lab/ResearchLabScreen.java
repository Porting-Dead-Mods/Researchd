package com.portingdeadmods.researchd.client.screens.lab;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ResearchLabScreen extends PDLAbstractContainerScreen<ResearchLabMenu> {
	public static final Integer SIDE_PADDING = 7;
	public static final Integer TB_PADDING = 12; // Top and Bottom Padding
	public static final Integer SLOTS_PER_ROW = 7;
	public static final Integer SLOT_WIDTH = 18;
	public static final Integer SLOT_HEIGHT = 20; // width + 2px for usage bar
	public static final Integer BAR_HEIGHT = 2;
	public static final Integer BAR_WIDTH = SLOT_WIDTH;

	public ResearchLabScreen(ResearchLabMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.width = SIDE_PADDING * 2 + SLOTS_PER_ROW * SLOT_WIDTH;
		this.height = TB_PADDING * 2 + (Researchd.RESEARCH_PACK_COUNT.getOrThrow() / SLOTS_PER_ROW + 1) * SLOT_HEIGHT;
	}

	@Override
	public @NotNull ResourceLocation getBackgroundTexture() {
		return Researchd.rl("textures/gui/research_screen/edges/bottom_right.png");
	}

	private static void drawBar(GuiGraphics guiGraphics, int x, int y) {

	}
}
