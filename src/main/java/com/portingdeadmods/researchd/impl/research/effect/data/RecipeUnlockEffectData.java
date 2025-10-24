package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Collection;

public record RecipeUnlockEffectData(UniqueArray<ResourceLocation> blockedRecipes) implements ResearchEffectData<RecipeUnlockEffect> {
    public static final RecipeUnlockEffectData EMPTY = new RecipeUnlockEffectData(new UniqueArray<>());

    public static final Codec<RecipeUnlockEffectData> CODEC = UniqueArray.CODEC(ResourceLocation.CODEC)
            .xmap(RecipeUnlockEffectData::new, RecipeUnlockEffectData::blockedRecipes);
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeUnlockEffectData> STREAM_CODEC = StreamCodec.composite(
            UniqueArray.STREAM_CODEC(ResourceLocation.STREAM_CODEC),
            RecipeUnlockEffectData::blockedRecipes,
            RecipeUnlockEffectData::new
    );

    @Override
    public RecipeUnlockEffectData add(RecipeUnlockEffect recipe, Level level) {
        UniqueArray<ResourceLocation> recipes = new UniqueArray<>(this.blockedRecipes());
        recipe.getRecipes(level).forEach(holder -> recipes.add(holder.id()));
        return new RecipeUnlockEffectData(recipes);
    }

    @Override
    public RecipeUnlockEffectData remove(RecipeUnlockEffect recipe, Level level) {
        UniqueArray<ResourceLocation> recipes = new UniqueArray<>(this.blockedRecipes());
        recipe.getRecipes(level).forEach(holder -> recipes.remove(holder.id()));
        return new RecipeUnlockEffectData(recipes);
    }

    public boolean contains(ResourceLocation recipeId) {
        return this.blockedRecipes.contains(recipeId);
    }

    public boolean contains(RecipeHolder<?> holder) {
        return contains(holder.id());
    }

    public boolean isEmpty() {
        return this.blockedRecipes.isEmpty();
    }

    public UniqueArray<RecipeHolder<?>> resolve(Level level) {
        UniqueArray<RecipeHolder<?>> resolved = new UniqueArray<>();
        for (ResourceLocation id : this.blockedRecipes()) {
            level.getRecipeManager().byKey(id).ifPresent(resolved::add);
        }
        return resolved;
    }

    @Override
    public UniqueArray<ResourceLocation> getAll() {
        return this.blockedRecipes();
    }

    @Override
    public RecipeUnlockEffectData getDefault(Level level) {
        Collection<RecipeUnlockEffect> recipeEffects = ResearchHelperCommon.getResearchEffects(RecipeUnlockEffect.class, level);
        UniqueArray<ResourceLocation> blocked = new UniqueArray<>();

        for (RecipeUnlockEffect unlock : recipeEffects) {
            unlock.getRecipes(level).forEach(holder -> blocked.add(holder.id()));
        }

        return new RecipeUnlockEffectData(blocked);
    }
}
