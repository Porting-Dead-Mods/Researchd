package com.portingdeadmods.researchd.api.research.methods;

import net.minecraft.world.item.crafting.Ingredient;

public interface ItemResearchMethod extends ResearchMethod {
    Ingredient item();

    int count();
}
