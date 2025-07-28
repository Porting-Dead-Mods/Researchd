package com.portingdeadmods.researchd.content.menus;

import com.portingdeadmods.portingdeadlibs.api.gui.menus.PDLAbstractContainerMenu;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.client.screens.lab.ResearchLabScreen;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import com.portingdeadmods.researchd.registries.ResearchdMenuTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ResearchLabMenu extends PDLAbstractContainerMenu<ResearchLabControllerBE> {
	private final List<Point> slotPositions = new UniqueArray<>();
	public List<Point> getSlotPositions() {
		return slotPositions;
	}

	public ResearchLabMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
		this(containerId, inv, (ResearchLabControllerBE) inv.player.level().getBlockEntity(extraData.readBlockPos()));
	}


	public ResearchLabMenu(int containerId, @NotNull Inventory inv, @NotNull ResearchLabControllerBE blockEntity) {
		super(ResearchdMenuTypes.RESEARCH_LAB_MENU.get(), containerId, inv, blockEntity);
		Researchd.debug("Research Lab Menu", "Creating Research Lab Menu with ", Researchd.RESEARCH_PACK_COUNT, " slots.");

		ArrayList<Integer> slotXPositions = new ArrayList<>();
		for (int i = Researchd.RESEARCH_PACK_COUNT.getOrThrow(); i > 0; i -= ResearchLabScreen.SLOTS_PER_ROW) {
			int rowCount = Math.min(i, ResearchLabScreen.SLOTS_PER_ROW);
			int[] positions = calculateCenteredPositions(ResearchLabScreen.SIDE_PADDING, ResearchLabScreen.SIDE_PADDING + rowCount * ResearchLabScreen.SLOT_WIDTH, ResearchLabScreen.SLOT_WIDTH, rowCount);
			for (int pos : positions) {
				slotXPositions.add(pos);
			}
		}

		for (int i = 0; i < Researchd.RESEARCH_PACK_COUNT.getOrThrow(); i++) {
			int row = i / ResearchLabScreen.SLOTS_PER_ROW;
			addSlot(new SlotItemHandler(blockEntity.getItemHandler(), i, slotXPositions.get(i), ResearchLabScreen.TB_PADDING + row * ResearchLabScreen.SLOT_HEIGHT));
			Researchd.debug("Research Lab Menu", "Adding slot ", i, " at position ", slotXPositions.get(i), ":", ResearchLabScreen.TB_PADDING + row * ResearchLabScreen.SLOT_HEIGHT);
			this.slotPositions.add(new Point(slotXPositions.get(i), ResearchLabScreen.TB_PADDING + row * ResearchLabScreen.SLOT_HEIGHT));
		}

		addPlayerInventory(inv);
		addPlayerHotbar(inv);
	}

	@Override
	protected int getMergeableSlotCount() {
		return Researchd.RESEARCH_PACK_COUNT.getOrThrow(); // At menu creation time the LazyFinal should be initialized, so safe getOrThrow()
	}

	// TODO: Move to PDL
	public static int[] calculateCenteredPositions(int rangeStart, int rangeEnd, int width, int count) {
		if (count <= 0) return new int[0];
		if ((width * count) > (rangeEnd - rangeStart)) {
			throw new IllegalArgumentException("Total width exceeds the range.");
		}

		int rangeCenter = (rangeStart + rangeEnd) / 2;
		int totalWidth = (count - 1) * width;
		int startPosition = rangeCenter - totalWidth / 2;

		int[] positions = new int[count];
		for (int i = 0; i < count; i++) {
			positions[i] = startPosition + i * width;
		}

		return positions;
	}
}
