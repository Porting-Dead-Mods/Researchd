package com.portingdeadmods.researchd.content.menus;

import com.portingdeadmods.portingdeadlibs.api.gui.menus.PDLAbstractContainerMenu;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.registries.ResearchdMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ResearchLabMenu extends PDLAbstractContainerMenu<ResearchLabControllerBE> {
	public ResearchLabMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
		this(containerId, inv, (ResearchLabControllerBE) inv.player.level().getBlockEntity(extraData.readBlockPos()));
	}


	public ResearchLabMenu(int containerId, @NotNull Inventory inv, @NotNull ResearchLabControllerBE blockEntity) {
		super(ResearchdMenuTypes.RESEARCH_LAB_MENU.get(), containerId, inv, blockEntity);
	}

	@Override
	protected int getMergeableSlotCount() {
		return 0;
	}
}
