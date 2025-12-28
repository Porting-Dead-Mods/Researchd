package com.portingdeadmods.researchd.impl.research.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectSerializer;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.UnlockItemEffectData;
import com.portingdeadmods.researchd.registries.ResearchEffectTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record ItemUnlockEffect(Optional<ItemStack> icon, Optional<String> name, ResourceLocation item) implements ResearchEffect {
    private static final MapCodec<ItemUnlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("icon").forGetter(ItemUnlockEffect::icon),
            Codec.STRING.optionalFieldOf("name").forGetter(ItemUnlockEffect::name),
            ResourceLocation.CODEC.fieldOf("item").forGetter(ItemUnlockEffect::item)
    ).apply(instance, ItemUnlockEffect::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ItemUnlockEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ItemStack.STREAM_CODEC),
            ItemUnlockEffect::icon,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            ItemUnlockEffect::name,
            ResourceLocation.STREAM_CODEC,
            ItemUnlockEffect::item,
            ItemUnlockEffect::new
    );

    public static final ResearchEffectSerializer<ItemUnlockEffect> SERIALIZER = ResearchEffectSerializer.simple(CODEC, STREAM_CODEC);
    public static final ResourceLocation ID = Researchd.rl("unlock_item");

    public ItemUnlockEffect(ItemStack icon, String name, ResourceLocation item) {
        this(Optional.ofNullable(icon), Optional.ofNullable(name), item);
    }

    public ItemUnlockEffect(ResourceLocation item) {
        this(Optional.empty(), Optional.empty(), item);
    }

    public ItemUnlockEffect(Item item) {
        this(BuiltInRegistries.ITEM.getKey(item));
    }

    @Override
    public void onUnlock(Level level, Player player, ResourceKey<Research> research) {
        UnlockItemEffectData data = player.getData(ResearchdAttachments.ITEM_PREDICATE.get());
        player.setData(ResearchdAttachments.ITEM_PREDICATE.get(), data.remove(this, level));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchEffectType type() {
        return ResearchEffectTypes.ITEM_UNLOCK.get();
    }

    public Item getItem() {
        return BuiltInRegistries.ITEM.get(this.item);
    }

    public ItemStack getDisplayStack() {
        return this.icon().map(ItemStack::copy)
                .orElseGet(() -> new ItemStack(this.getItem()));
    }

    public Set<RecipeHolder<?>> getRecipes(Level level) {
        Item target = this.getItem();
        if (target == Items.AIR) {
            return Set.of();
        }

        Set<RecipeHolder<?>> recipes = new HashSet<>();
        for (RecipeHolder<?> holder : level.getRecipeManager().getRecipes()) {
            Recipe<?> recipe = holder.value();
            ItemStack resultStack = recipe.getResultItem(level.registryAccess());
            boolean matchesResult = resultStack.is(target);
            boolean matchesIngredient = recipe.getIngredients().stream().anyMatch(ingredient -> ingredientMatches(ingredient, target));
            if (matchesResult || matchesIngredient) {
                recipes.add(holder);
            }
        }
        return recipes;
    }

    private boolean ingredientMatches(Ingredient ingredient, Item target) {
        if (ingredient.isEmpty()) {
            return false;
        }
        for (ItemStack stack : ingredient.getItems()) {
            if (stack.is(target)) {
                return true;
            }
        }
        return false;
    }

    public ResourceKey<Item> getItemKey() {
        return ResourceKey.create(Registries.ITEM, this.item());
    }

    @Override
    public ResearchEffectSerializer<ItemUnlockEffect> getSerializer() {
        return SERIALIZER;
    }
}
