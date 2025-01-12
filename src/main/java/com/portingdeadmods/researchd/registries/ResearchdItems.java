package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.items.ResearchPackItem;
import com.portingdeadmods.researchd.impl.research.SimpleResearchPack;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchdItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Researchd.MODID);

    public static final DeferredItem<ResearchPackItem> RESEARCH_PACK = ITEMS.register("research_pack",
            () -> new ResearchPackItem(new Item.Properties().component(ResearchdDataComponents.RESEARCH_PACK, SimpleResearchPack.EMPTY)));
}
