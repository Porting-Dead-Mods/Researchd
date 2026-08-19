package com.portingdeadmods.researchd.content.blocks;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.ghost.GhostMultiblockController;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class ResearchLabController extends GhostMultiblockController {
	public ResearchLabController(Properties properties) {
		super(properties);
	}

    @Override
    public @NotNull RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    // The lab slot count is derived on both sides from the block entity's item handler size, which is
    // synced to the client via the block entity NBT, so no extra data beyond the position is needed here.
    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof MenuProvider menuProvider) {
            player.openMenu(menuProvider, regBuf -> regBuf.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hit);
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
