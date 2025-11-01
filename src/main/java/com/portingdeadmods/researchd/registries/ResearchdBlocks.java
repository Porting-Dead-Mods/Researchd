package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blocks.ResearchLabController;
import com.portingdeadmods.researchd.content.blocks.ResearchLabPart;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ResearchdBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Researchd.MODID);

	public static final DeferredBlock<ResearchLabPart> RESEARCH_LAB_PART = BLOCKS.register("research_lab_part",
			() -> new ResearchLabPart(BlockBehaviour.Properties.of().strength(3.0F, 3.0F).noOcclusion()));
	public static final DeferredBlock<ResearchLabController> RESEARCH_LAB_CONTROLLER = BLOCKS.register("research_lab_controller",
			() -> new ResearchLabController(BlockBehaviour.Properties.of().strength(3.0F, 3.0F).noOcclusion()));
}

