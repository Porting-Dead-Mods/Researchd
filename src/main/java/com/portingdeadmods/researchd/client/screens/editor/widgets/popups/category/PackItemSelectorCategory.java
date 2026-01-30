package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category;

import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collection;

public class PackItemSelectorCategory implements ItemSelectorCategory {
    public static final PackItemSelectorCategory INSTANCE = new PackItemSelectorCategory();
    private Collection<ItemStack> items;

    private PackItemSelectorCategory() {
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public Collection<ItemStack> getItems() {
        if (this.items == null) {
            this.items = ResearchHelperClient.getResearchPacks().keySet().stream().map(ResearchPackImpl::asStack).toList();
        }
        return this.items;
    }

    @Override
    public ItemStack getIcon() {
        return ResearchdItems.GREEN_RESEARCH_PACK_ICON.toStack();
    }

    @Override
    public Component getName() {
        return Component.literal("Research Pack");
    }
}
