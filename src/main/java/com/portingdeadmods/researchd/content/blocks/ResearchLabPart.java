package com.portingdeadmods.researchd.content.blocks;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchLabPart extends BaseEntityBlock {
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
}
