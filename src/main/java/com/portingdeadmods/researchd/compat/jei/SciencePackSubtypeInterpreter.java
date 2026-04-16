package com.portingdeadmods.researchd.compat.jei;

import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SciencePackSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(@NotNull ItemStack stack, @NotNull UidContext context) {
        var component = stack.get(ResearchdDataComponents.RESEARCH_PACK.get());
        if (component == null || component.researchPackKey().isEmpty()) {
            return null;
        }
        return component.researchPackKey().get().location().toString();
    }

    @Override
    public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack stack, @NotNull UidContext context) {
        return "";
    }
}
