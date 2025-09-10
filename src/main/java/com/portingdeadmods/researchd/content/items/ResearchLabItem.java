package com.portingdeadmods.researchd.content.items;

import com.portingdeadmods.portingdeadlibs.utils.AABBUtils;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResearchLabItem extends BlockItem {
	public ResearchLabItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public @NotNull InteractionResult place(BlockPlaceContext context) {
		return super.place(context);
	}

	@Override
	public boolean placeBlock(BlockPlaceContext context, BlockState state) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		if (level.isClientSide()) {
			return true;
		}
		if (player == null) { // Cannot place without a player due to researching being player dependent
			return false;
		}

		Researchd.debug("Research Lab", "Placing Research Lab at " + context.getClickedPos());
		BlockPos controllerPos = context.getClickedPos().offset(context.getHorizontalDirection().getNormal());
		BlockPos center = controllerPos.offset(Direction.UP.getNormal());
		AABB aabb = AABBUtils.create(1, 1, 1, 1, 1, 1, center);
		Spaghetti.printAABB(aabb);
		UniqueArray<BlockPos> allPos = AABBUtils.getAllPositionsInAABB(aabb);
		Researchd.debug("Research Lab", "Found " + allPos.size() + " positions in AABB: " + aabb);

		allPos.forEach(pos -> {
			if (pos.equals(controllerPos)) {
				//Researchd.debug("Research Lab", "Placing Research Lab Controller at " + pos);
				level.setBlockAndUpdate(pos, ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get().defaultBlockState());
			} else {
				//Researchd.debug("Research Lab", "Placing Research Lab Part at " + pos);
				level.setBlockAndUpdate(pos, ResearchdBlocks.RESEARCH_LAB_PART.get().defaultBlockState());
				ResearchLabPartBE rlp = (ResearchLabPartBE) level.getBlockEntity(pos);
				rlp.setControllerPos(controllerPos);
			}

			level.getBlockEntity(pos).setData(ResearchdAttachments.PLACED_BY_UUID, player != null ? player.getUUID() : null);
		});

		allPos.remove(controllerPos);
		if (level.getBlockEntity(controllerPos) instanceof ResearchLabControllerBE controller) {
			controller.setPartPositions(allPos);
			Researchd.debug("Research Lab", "Set part positions for controller at " + controllerPos + ": " + allPos);
		}
		return true;
	}

	@Override
	public boolean canPlace(BlockPlaceContext context, BlockState state) {
		Player player = context.getPlayer();
		Level level = context.getLevel();
		AABB aabb = AABBUtils.create(1, 1, 1, 1, 1, 1, context.getClickedPos().above());
		AABBUtils.move(aabb, context.getHorizontalDirection(), 1);
		AABBUtils.move(aabb, Direction.UP, 1);

		UniqueArray<BlockPos> allPos = AABBUtils.getAllPositionsInAABB(aabb);
		List<BlockPos> badPos = allPos.stream().filter(pos -> !level.getBlockState(pos).canBeReplaced()).toList();
		if (badPos.isEmpty()) {
			Researchd.debug("Research Lab", "Can place at " + context.getClickedPos());
			return true;
		} else {
			Researchd.debug("Research Lab", "Cannot place at " + context.getClickedPos());
			Researchd.debug("Research Lab", "Bad positions: ");
			badPos.forEach(pos -> Researchd.debug("Research Lab", pos.getX(), ", ", pos.getY(), ", ", pos.getZ(), " is ", level.getBlockState(pos).getBlock().getClass().getSimpleName()));
			return false;
		}
	}
}
