package com.portingdeadmods.researchd.content.blocks;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.blocks.ContainerBlock;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import net.minecraft.core.BlockPos;
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
	public @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(ResearchLabController::new);
	}
}
