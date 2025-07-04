package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.items.ResearchLabItem;
import com.portingdeadmods.researchd.content.items.ResearchPackItem;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchdItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Researchd.MODID);

    public static final DeferredItem<ResearchPackItem> RESEARCH_PACK = ITEMS.register("research_pack",
            () -> new ResearchPackItem(new Item.Properties().component(ResearchdDataComponents.RESEARCH_PACK, ResearchPackComponent.EMPTY)));
    public static final DeferredItem<ResearchLabItem> RESEARCH_LAB = ITEMS.register("research_lab",
            () -> new ResearchLabItem(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), new Item.Properties()));
}
