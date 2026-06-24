package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import com.portingdeadmods.researchd.compat.immersiveengineering.UnlockIEMultiblockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.ItemUnlockEffectData;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ResearchdEffectDataTypes {
    public static final DeferredRegister<ResearchEffectDataType<?>> TYPES = DeferredRegister.create(ResearchdRegistries.RESEARCH_EFFECT_DATA_TYPE_KEY, Researchd.MODID);

    public static final Supplier<ResearchEffectDataType<ItemUnlockEffectData>> ITEM_UNLOCK = TYPES.register("items_unlock_effect", () -> ItemUnlockEffectData.TYPE);
    public static final Supplier<ResearchEffectDataType<RecipeUnlockEffectData>> RECIPE_UNLOCK = TYPES.register("recipes_unlock_effect", () -> RecipeUnlockEffectData.TYPE);
    public static final Supplier<ResearchEffectDataType<DimensionUnlockEffectData>> DIMENSION_UNLOCK = TYPES.register("dimensions_unlock_effect", () -> DimensionUnlockEffectData.TYPE);

    // COMPAT //

    // Immersive Engineering - data only stores ResourceLocations (no IE classes), safe to register unconditionally.
    public static final Supplier<ResearchEffectDataType<UnlockIEMultiblockEffectData>> IE_MULTIBLOCK_UNLOCK = TYPES.register("ie_multiblock_unlock", () -> UnlockIEMultiblockEffectData.TYPE);
}
