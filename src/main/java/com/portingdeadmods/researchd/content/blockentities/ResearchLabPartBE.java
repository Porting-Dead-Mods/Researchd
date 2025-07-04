package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResearchLabPartBE extends BlockEntity {
	public ResearchLabPartBE(BlockPos pos, BlockState blockState) {
		super(ResearchdBlockEntityTypes.RESEARCH_LAB_PART.get(), pos, blockState);
	}
}
