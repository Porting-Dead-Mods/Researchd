package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blocks.ResearchLabController;
import com.portingdeadmods.researchd.content.blocks.ResearchLabPart;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchdBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Researchd.MODID);

    public static final DeferredBlock<ResearchLabPart> RESEARCH_LAB_PART =
            BLOCKS.registerBlock("research_lab_part", ResearchLabPart::new, properties -> properties
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion());
    public static final DeferredBlock<ResearchLabController> RESEARCH_LAB_CONTROLLER =
            BLOCKS.registerBlock("research_lab_controller", ResearchLabController::new, properties -> properties
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion());
}
