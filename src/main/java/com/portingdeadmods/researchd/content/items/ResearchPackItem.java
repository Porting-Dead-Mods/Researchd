package com.portingdeadmods.researchd.content.items;

import com.portingdeadmods.portingdeadlibs.utils.Utils;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ResearchPackItem extends Item {
    public ResearchPackItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        ResearchPackComponent comp = stack.get(ResearchdDataComponents.RESEARCH_PACK);
        if (comp != null)
            if (comp.researchPackKey().isPresent()) {
                return Component.translatable(this.getDescriptionId(stack) + '_' +  comp.researchPackKey().get().location().toString().replace(':', '_'));
            }
        return Component.translatable(this.getDescriptionId(stack));
    }
}
