package com.portingdeadmods.researchd.content.blocks;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.blocks.ContainerBlock;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchLabController extends ContainerBlock {
	public ResearchLabController(Properties properties) {
		super(properties);
	}

	@Override
	public boolean tickingEnabled() {
		return true;
	}

	@Override
	public BlockEntityType<? extends ContainerBlockEntity> getBlockEntityType() {
		return ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get();
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (level.getBlockEntity(pos) instanceof ResearchLabControllerBE controllerBE) {
			if (controllerBE.partPos.isInitialized()) {
				controllerBE.partPos.getOrThrow().forEach(partPos -> {
					if (level.getBlockEntity(partPos) instanceof ResearchLabPartBE partBE) {
						Researchd.debug("Research Lab Controller", "Removing part at " + partPos);
						level.removeBlock(partPos, false);
						level.removeBlockEntity(partPos);
					}
				});
			}
		} else {
			Researchd.debug("Research Lab Controller", "Block entity at " + pos + " is not an instance of ResearchLabControllerBE, skipping removal of parts.");
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(ResearchLabController::new);
	}
}
