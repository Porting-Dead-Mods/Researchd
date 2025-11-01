package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.api.ghost.SimpleGhostMultiblockPartBE;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ResearchLabPartBE extends SimpleGhostMultiblockPartBE {
	public ResearchLabPartBE(BlockPos pos, BlockState blockState) {
		super(ResearchdBlockEntityTypes.RESEARCH_LAB_PART.get(), pos, blockState);
	}
}
