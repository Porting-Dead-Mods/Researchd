package com.portingdeadmods.researchd.client.screens.lab;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ResearchLabScreen extends PDLAbstractContainerScreen<ResearchLabMenu> {
	public ResearchLabScreen(ResearchLabMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	public @NotNull ResourceLocation getBackgroundTexture() {
		// TODO: Placeholder
		return Researchd.rl("textures/gui/research_screen/edges/bottom_right.png");
	}
}
