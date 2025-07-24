package com.portingdeadmods.researchd.content.blocks;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchLabPart extends BaseEntityBlock {
	public ResearchLabPart(Properties properties) {
		super(properties);
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		ServerLevel sl = (ServerLevel) level;
		ServerPlayer sp = (ServerPlayer) player;
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof ResearchLabPartBE researchLabPartBE) {
			if (sl.getBlockEntity(researchLabPartBE.getControllerPos()) instanceof ResearchLabControllerBE controllerBE)
				sp.openMenu(controllerBE, researchLabPartBE.getControllerPos());
			return InteractionResult.CONSUME;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (level.getBlockEntity(pos) instanceof ResearchLabPartBE partBE) {
			BlockPos controllerPos = partBE.getControllerPos();
			if (level.getBlockState(controllerPos).getBlock() instanceof ResearchLabController controller) {
				Researchd.debug("Research Lab Part", "Removing part at " + pos + " and its controller at " + controllerPos);
				level.removeBlock(controllerPos, false);
				level.removeBlockEntity(controllerPos);
			}
		} else {
			Researchd.debug("Research Lab Part", "Block entity at " + pos + " is not an instance of ResearchLabPartBE, skipping removal of parts.");
		}
	}

	@Override
	public @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(ResearchLabPart::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new ResearchLabPartBE(blockPos, blockState);
	}
}
