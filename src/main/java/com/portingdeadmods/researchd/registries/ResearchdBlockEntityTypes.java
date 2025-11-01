package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ResearchdBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Researchd.MODID);

	public static final Supplier<BlockEntityType<ResearchLabPartBE>> RESEARCH_LAB_PART = BLOCK_ENTITY_TYPES.register("research_lab_part",
			() -> BlockEntityType.Builder.of(ResearchLabPartBE::new, ResearchdBlocks.RESEARCH_LAB_PART.get())
					.build(null));
	public static final Supplier<BlockEntityType<ResearchLabControllerBE>> RESEARCH_LAB_CONTROLLER = BLOCK_ENTITY_TYPES.register("research_lab_controller",
			() -> BlockEntityType.Builder.of(ResearchLabControllerBE::new, ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get())
					.build(null));
}
