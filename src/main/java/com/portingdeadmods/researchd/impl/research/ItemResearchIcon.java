package com.portingdeadmods.researchd.impl.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collections;
import java.util.List;

public record ItemResearchIcon(List<ItemStack> items) implements ResearchIcon {
    public static final Codec<ItemResearchIcon> CODEC = ItemStack.CODEC.listOf()
            .xmap(ItemResearchIcon::new, ItemResearchIcon::items);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResearchIcon> STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(ItemResearchIcon::new, ItemResearchIcon::items);
    public static final ResourceLocation ID = Researchd.rl("item_research_icon");
    public static final ItemResearchIcon EMPTY = new ItemResearchIcon(Collections.emptyList());

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static ItemResearchIcon single(ItemStack stack) {
        return new ItemResearchIcon(Collections.singletonList(stack));
    }

    public static ItemResearchIcon single(ItemLike item) {
        return new ItemResearchIcon(Collections.singletonList(new ItemStack(item)));
    }

}
