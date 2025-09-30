package com.portingdeadmods.researchd.impl.research;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record ItemResearchIcon(Either<List<ItemStack>, Ingredient> items) implements ResearchIcon {
    public static final Codec<ItemResearchIcon> CODEC = Codec.either(ItemStack.CODEC.listOf(), Ingredient.CODEC)
            .xmap(ItemResearchIcon::new, ItemResearchIcon::items);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResearchIcon> STREAM_CODEC = ByteBufCodecs.either(ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), Ingredient.CONTENTS_STREAM_CODEC)
            .map(ItemResearchIcon::new, ItemResearchIcon::items);
    public static final ResourceLocation ID = Researchd.rl("item_research_icon");
    public static final ItemResearchIcon EMPTY = new ItemResearchIcon(Either.left(List.of()));

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public List<ItemStack> getStacks() {
        if (this.items().left().isPresent()) {
            return this.items().left().get();
        } else {
            return Arrays.asList(this.items().right().get().getItems());
        }
    }

    public static ItemResearchIcon single(ItemStack stack) {
        return new ItemResearchIcon(Either.left(Collections.singletonList(stack)));
    }

    public static ItemResearchIcon single(ItemLike item) {
        return new ItemResearchIcon(Either.left(Collections.singletonList(new ItemStack(item))));
    }

}
