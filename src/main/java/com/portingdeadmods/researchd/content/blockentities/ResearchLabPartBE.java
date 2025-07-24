package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResearchLabPartBE extends BlockEntity {
	private final LazyFinal<BlockPos> controllerPos = new LazyFinal<>();

	public ResearchLabPartBE(BlockPos pos, BlockState blockState) {
		super(ResearchdBlockEntityTypes.RESEARCH_LAB_PART.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		if (tag.contains("ControllerPos")) {
			this.setControllerPos(BlockPos.of(tag.getLong("ControllerPos")));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);

		this.controllerPos.ifInitialized(pos -> tag.putLong("ControllerPos", this.controllerPos.getOrThrow().asLong()));
	}

	public void setControllerPos(BlockPos controllerPos) {
		if (!this.controllerPos.isInitialized())
			this.controllerPos.initialize(controllerPos);
		else
			Researchd.debug("Research Lab Part BE", "Controller position is already set to: " + this.controllerPos.getOrThrow() + ", ignoring new value: " + controllerPos);
	}

	public BlockPos getControllerPos() {
		return this.controllerPos.getOrThrow();
	}
}
