package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.portingdeadlibs.api.misc.PDLDeferredRegisterItems;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.items.ResearchLabItem;
import com.portingdeadmods.researchd.content.items.ResearchPackItem;
import com.portingdeadmods.researchd.data.ResearchdDataComponents;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ResearchdItems {
    public static final PDLDeferredRegisterItems ITEMS = PDLDeferredRegisterItems.createItemsRegister(Researchd.MODID);

    public static final DeferredItem<ResearchPackItem> RESEARCH_PACK = ITEMS.registerItem(
            "research_pack",
            ResearchPackItem::new,
            properties -> properties.component(ResearchdDataComponents.RESEARCH_PACK, ResearchPackComponent.EMPTY));
    // TODO(26.1 port, PDL): PDL's registerSimpleItemNoCreative doesn't set the item id that 26.1 requires
    public static final DeferredItem<Item> GREEN_RESEARCH_PACK_ICON = ITEMS.registerNoCreative(
            "green_research_pack_icon",
            id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    public static final DeferredItem<ResearchLabItem> RESEARCH_LAB = ITEMS.registerItem(
            "research_lab",
            properties -> new ResearchLabItem(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get(), properties),
            // Named after the Lab Controller block, as the 1.21.1 block item was
            properties -> properties.overrideDescription(
                    ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get().getDescriptionId()));
}
