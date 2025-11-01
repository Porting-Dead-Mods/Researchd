package com.portingdeadmods.researchd.content.menus;

import com.google.common.collect.ImmutableList;
import com.portingdeadmods.portingdeadlibs.api.gui.menus.PDLAbstractContainerMenu;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.registries.ResearchdMenuTypes;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResearchLabMenu extends PDLAbstractContainerMenu<ResearchLabControllerBE> {
    private final List<ItemStack> researchPackItems;
    private final List<ResourceKey<ResearchPack>> researchPacks;

	public final ImmutableList<Integer> labSlotsX;
	public final ImmutableList<Slot> labSlots;

    public ResearchLabMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
		this(containerId, inv, (ResearchLabControllerBE) inv.player.level().getBlockEntity(extraData.readBlockPos()));
	}

	public ResearchLabMenu(int containerId, @NotNull Inventory inv, @NotNull ResearchLabControllerBE blockEntity) {
		super(ResearchdMenuTypes.RESEARCH_LAB_MENU.get(), containerId, inv, blockEntity);
		Researchd.debug("Research Lab Menu", "Creating Research Lab Menu with ", ResearchHelperCommon.getResearchPacks(inv.player.level()).size(), " slots.");

        this.researchPacks = this.getBlockEntity().researchPacks;
        this.researchPackItems = researchPacks.stream().map(ResearchPackImpl::asStack).toList();

		int slotsX = 8;
        int slotsY = 18;

		var x = new ImmutableList.Builder<Integer>();
		var s = new ImmutableList.Builder<Slot>();

        for (int i = 0; i < this.researchPackItems.size(); i++) {
			int slotX = slotsX + i * 18;
			Slot slot = new SlotItemHandler(this.getBlockEntity().getItemHandler(), i, slotX, slotsY);
			x.add(slotX);
			s.add(slot);

            addSlot(slot);
        }

		this.labSlots = s.build();
		this.labSlotsX = x.build();

		addPlayerInventory(inv, 116);
		addPlayerHotbar(inv, 174);
	}

    public List<ResourceKey<ResearchPack>> getResearchPacks() {
        return researchPacks;
    }

    public List<ItemStack> getResearchPackItems() {
        return this.researchPackItems;
    }

    @Override
	protected int getMergeableSlotCount() {
		return ResearchHelperCommon.getResearchPacks(inv.player.level()).size(); // At menu creation time the LazyFinal should be initialized, so safe getOrThrow()
	}

	// TODO: Move to PDL
	public static int[] calculateCenteredPositions(int rangeStart, int rangeEnd, int width, int count) {
		if (count <= 0) return new int[0];
		if ((width * count) > (rangeEnd - rangeStart)) {
			throw new IllegalArgumentException("Total width exceeds the range.");
		}

		int rangeCenter = (rangeStart + rangeEnd) / 2;
		int totalWidth = count * width;
		int startPosition = rangeCenter - totalWidth / 2;

		int[] positions = new int[count];
		for (int i = 0; i < count; i++) {
			positions[i] = startPosition + i * width;
		}

		return positions;
	}
}
