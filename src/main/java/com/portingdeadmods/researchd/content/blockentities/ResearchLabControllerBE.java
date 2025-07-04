package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.utils.IOAction;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ResearchLabControllerBE extends ContainerBlockEntity implements MenuProvider {
	public ResearchLabControllerBE(BlockPos pos, BlockState blockState) {
		super(ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get(), pos, blockState);
	}

	@Override
	public <T> Map<Direction, Pair<IOAction, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability) {
		return Map.of();
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
