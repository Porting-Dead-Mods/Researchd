package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record RecipeUnlockEffect(Optional<ItemStack> icon, Optional<String> name, Set<ResourceLocation> recipes) implements ResearchEffect {
    private static final MapCodec<RecipeUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("icon").forGetter(RecipeUnlockEffect::icon),
            Codec.STRING.optionalFieldOf("name").forGetter(RecipeUnlockEffect::name),
            CodecUtils.set(ResourceLocation.CODEC).fieldOf("recipes").forGetter(RecipeUnlockEffect::recipes)
    ).apply(instance, RecipeUnlockEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, RecipeUnlockEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ItemStack.STREAM_CODEC),
            RecipeUnlockEffect::icon,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            RecipeUnlockEffect::name,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
            RecipeUnlockEffect::recipes,
            RecipeUnlockEffect::new
    );

    public static final ResearchEffectSerializer<RecipeUnlockEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final ResourceLocation ID = Researchd.rl("unlock_recipe");

    public RecipeUnlockEffect(ItemStack icon, String name, ResourceLocation ...recipes) {
        this(Optional.ofNullable(icon), Optional.ofNullable(name), Set.of(recipes));
    }

    public RecipeUnlockEffect(ResourceLocation ...recipes) {
        this(Optional.empty(), Optional.empty(), Set.of(recipes));
    }

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        RecipeUnlockEffectData data = player.getData(ResearchdAttachments.RECIPE_PREDICATE.get());
        player.setData(ResearchdAttachments.RECIPE_PREDICATE.get(), data.remove(this, level));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public Set<RecipeHolder<?>> getRecipes(Level level) {
        Set<RecipeHolder<?>> recipes = new HashSet<>(this.recipes.size());
        for (ResourceLocation recipe : this.recipes) {
            Optional<RecipeHolder<?>> recipeHolder = level.getRecipeManager().byKey(recipe);
            recipeHolder.ifPresent(recipes::add);
        }
        return recipes;
    }

    @Override
    public ResearchEffectSerializer<RecipeUnlockEffect> getSerializer() {
        return RecipeUnlockEffect.SERIALIZER;
    }

}
