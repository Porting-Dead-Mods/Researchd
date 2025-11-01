package com.portingdeadmods.researchd.content.items;

import com.portingdeadmods.researchd.api.research.RegistryDisplay;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.utils.SpaghettiCommon;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ResearchPackItem extends Item {
    public ResearchPackItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        ResearchPackComponent comp = stack.get(ResearchdDataComponents.RESEARCH_PACK);
        if (comp != null && comp.researchPackKey().isPresent()) {
            ResearchPack researchPack = ResearchHelperCommon.getResearchPack(comp.researchPackKey().get(), SpaghettiCommon.tryGetLevel());
            if (researchPack instanceof RegistryDisplay<?> display) {
                return display.getDisplayNameUnsafe(comp.researchPackKey().get());
            }
        }
        return super.getName(stack);
    }
}
