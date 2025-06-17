package com.portingdeadmods.researchd.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Codecs {
	// TODO: Place this into PDL
	public static final Codec<RecipeHolder<?>> RECIPE_HOLDER_CODEC = RecordCodecBuilder.create(instance -> instance.group (
			ResourceLocation.CODEC.fieldOf("id").forGetter(RecipeHolder::id),
			Recipe.CODEC.fieldOf("recipe").forGetter(RecipeHolder::value)
	).apply(instance, RecipeHolder::new));

	// TODO: Move to PDL
	public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
		return Codec.INT.xmap(i -> clazz.getEnumConstants()[i], Enum::ordinal);
	}

	public static <T extends Enum<T>> StreamCodec<ByteBuf, T> enumStreamCodec(Class<T> clazz) {
		return ByteBufCodecs.INT.map(i -> clazz.getEnumConstants()[i], Enum::ordinal);
	}
}
