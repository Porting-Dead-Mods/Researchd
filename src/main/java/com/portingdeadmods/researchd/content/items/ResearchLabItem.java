package com.portingdeadmods.researchd.content.items;

import com.portingdeadmods.portingdeadlibs.api.ghost.GhostControllerItem;
import com.portingdeadmods.portingdeadlibs.api.ghost.GhostMultiblockShape;
import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResearchLabItem extends GhostControllerItem {
	public static final GhostMultiblockShape MULTIBLOCK_SHAPE = GhostMultiblockShape.builder()
			.layer(
					"AIA",
					"ICI",
					"AIA"
			)
			.layer(
					"AAA",
					"AAA",
					"AAA"
			)
			.layer(
					"AAA",
					"AAA",
					"AAA"
			)
			.onPlaceOffset(0, 0, 1)
			.controllerChar('C')
			.exposeHandlers('I', Capabilities.ItemHandler.BLOCK.name())
			.build();

	public ResearchLabItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		Level level = context.getLevel();
		Player player = context.getPlayer();

		if (ResearchHelperCommon.getResearchPackKeys(level).isEmpty()) {
			if (player != null && !level.isClientSide())
				player.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Errors.NO_RESEARCH_PACKS_PRESENT));
			return false;
		};

		return super.canPlace(context, state);
	}

	@Override
	protected @NotNull Block getPartBlock() {
		return ResearchdBlocks.RESEARCH_LAB_PART.get();
	}

	@Override
	protected @NotNull GhostMultiblockShape getBaseShape() {
		return MULTIBLOCK_SHAPE;
	}

	@Override
	protected void afterPlacement(@NotNull Level level, @NotNull BlockPos controllerPos, @NotNull List<BlockPos> allPos, @Nullable Player player) {
		for (BlockPos pos : allPos) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be != null) {
				be.setData(ResearchdAttachments.PLACED_BY_UUID, player == null ? PlayerUtils.EmptyUUID : player.getUUID());
			}
		}
	}
}
