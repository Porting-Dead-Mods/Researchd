package com.portingdeadmods.researchd.content.blocks;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.api.ghost.GhostMultiblockController;
import com.portingdeadmods.portingdeadlibs.api.ghost.SimpleGhostMultiblockPart;
import com.portingdeadmods.portingdeadlibs.api.ghost.SimpleGhostMultiblockPartBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchLabPart extends SimpleGhostMultiblockPart {
	public ResearchLabPart(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(ResearchLabPart::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new ResearchLabPartBE(blockPos, blockState);
	}

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.getBlockEntity(pos) instanceof SimpleGhostMultiblockPartBE partBE) {
            BlockPos controllerPos = partBE.getControllerPos();
            if (level.getBlockState(controllerPos).getBlock() instanceof GhostMultiblockController) {
                level.destroyBlock(controllerPos, true, player);
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return ResearchdItems.RESEARCH_LAB.toStack();
    }

}
