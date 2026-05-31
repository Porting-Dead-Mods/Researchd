package com.portingdeadmods.researchd.impl.research.effect.data;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
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

    public static final MapCodec<RecipeUnlockEffectData> CODEC = UniqueArray.CODEC(ResourceLocation.CODEC)
            .xmap(RecipeUnlockEffectData::new, RecipeUnlockEffectData::blockedRecipes).fieldOf("blocked_recipes");
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeUnlockEffectData> STREAM_CODEC = StreamCodec.composite(
            UniqueArray.STREAM_CODEC(ResourceLocation.STREAM_CODEC),
            RecipeUnlockEffectData::blockedRecipes,
            RecipeUnlockEffectData::new
    );
    public static final ResearchEffectDataType<RecipeUnlockEffectData> TYPE = ResearchEffectDataType.simple(RecipeUnlockEffectData::new, CODEC, STREAM_CODEC);

    public RecipeUnlockEffectData() {
        this(new UniqueArray<>());
    }

    @Override
    public void add(RecipeUnlockEffect recipe, Level level) {
        UniqueArray<ResourceLocation> recipes = this.blockedRecipes();
        recipe.getRecipes(level).forEach(holder -> recipes.add(holder.id()));
    }

    @Override
    public void remove(RecipeUnlockEffect recipe, Level level) {
        UniqueArray<ResourceLocation> recipes = this.blockedRecipes();
        recipe.getRecipes(level).forEach(holder -> recipes.remove(holder.id()));
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
    public ResearchEffectDataType<? extends ResearchEffectData<RecipeUnlockEffect>> type() {
        return TYPE;
    }

    @Override
    public void initDefault(Level level) {
        Collection<RecipeUnlockEffect> recipeEffects = ResearchHelperCommon.getResearchEffects(RecipeUnlockEffect.class, level);
        UniqueArray<ResourceLocation> blocked = new UniqueArray<>();

        for (RecipeUnlockEffect unlock : recipeEffects) {
            unlock.getRecipes(level).forEach(holder -> blocked.add(holder.id()));
        }

    }
}
