package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.utils.IOAction;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.content.items.ResearchPackItem;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.research.ResearchPack;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResearchLabControllerBE extends ContainerBlockEntity implements MenuProvider {
	public LazyFinal<List<BlockPos>> partPos = LazyFinal.create();

	public ResearchLabControllerBE(BlockPos pos, BlockState blockState) {
		super(ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get(), pos, blockState);
		addItemHandler(Researchd.RESEARCH_PACK_COUNT.getOrThrow(), (idx, stack) -> {
			Researchd.debug("Research Lab Item Handler", "Checking stack at idx: ", idx, " stack: ", stack);

			if (!(stack.getItem() instanceof ResearchPackItem pack)) {
				Researchd.debug("Research Lab Item Handler", "Not a ResearchPackItem at idx: ", idx);
				return false;
			}
			if (!stack.has(ResearchdDataComponents.RESEARCH_PACK)) {
				Researchd.debug("Research Lab Item Handler", "Stack doesn't have a Research Pack Data Component at idx: ", idx);
				return false;
			}

			ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK);
			Optional<ResourceKey<ResearchPack>> optKey = component.researchPackKey();
			if (optKey.isEmpty()) {
				Researchd.debug("Research Lab Item Handler", "Component present, but no researchPackKey was found at idx: ", idx);
				return false;
			}
			ResourceKey<ResearchPack> key = optKey.get();

			ResearchPack expectedPack = Researchd.RESEARCH_PACKS.get(idx);
			ResearchPack actualPack = Researchd.RESEARCH_PACK_REGISTRY.get().getOrThrow(key).value();
			boolean matches = expectedPack.equals(actualPack);
			Researchd.debug("Research Lab Item Handler", "Expected: ", expectedPack, " Actual: ", actualPack, " Match: ", matches, " at idx: ", idx);
			return matches;
		});
	}

	@Override
	protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
		partPos.ifInitialized(pos -> {
			tag.putLongArray("PartPositions", pos.stream().mapToLong(BlockPos::asLong).toArray());
		});
	}

	@Override
	protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {
		if (tag.contains("PartPositions")) {
			long[] partPositions = tag.getLongArray("PartPositions");
			List<BlockPos> positions = new UniqueArray<>();
			for (long posLong : partPositions) {
				BlockPos pos = BlockPos.of(posLong);
				positions.add(pos);
			}

			this.setPartPositions(positions);
		}
	}

	@Override
	public <T> Map<Direction, Pair<IOAction, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability) {
		return Map.of();
	}

	public void setPartPositions(List<BlockPos> partPositions) {
		if (!this.partPos.isInitialized())
			this.partPos.initialize(partPositions);
		else
			Researchd.debug("Research Lab Controller BE", "Part positions are already initialized, ignoring new values: ", partPositions);
	}

	@Override
	public @NotNull Component getDisplayName() {
		return Component.literal("Research Lab");
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
		return new ResearchLabMenu(i, inventory, this);
	}
}
