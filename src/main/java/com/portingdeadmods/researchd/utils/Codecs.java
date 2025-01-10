package com.portingdeadmods.researchd.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class Codecs {
	// TODO: Place this into PDL
	public static final Codec<RecipeHolder<?>> RECIPE_HOLDER_CODEC = RecordCodecBuilder.create(instance -> instance.group (
			ResourceLocation.CODEC.fieldOf("id").forGetter(RecipeHolder::id),
			Recipe.CODEC.fieldOf("recipe").forGetter(RecipeHolder::value)
	).apply(instance, RecipeHolder::new));
}
