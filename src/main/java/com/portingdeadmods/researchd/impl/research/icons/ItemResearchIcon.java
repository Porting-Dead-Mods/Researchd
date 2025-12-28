package com.portingdeadmods.researchd.impl.research.icons;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.serializers.ResearchIconSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collections;
import java.util.List;

public record ItemResearchIcon(List<ItemStack> items) implements ResearchIcon {
    public static final ResearchIconSerializer<ItemResearchIcon> SERIALIZER = ResearchIconSerializer.simple(ItemStack.CODEC.listOf()
            .xmap(ItemResearchIcon::new, ItemResearchIcon::items).fieldOf("items"));
    public static final ResourceLocation ID = Researchd.rl("item_research_icon");
    public static final ItemResearchIcon EMPTY = new ItemResearchIcon(Collections.emptyList());

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchIconSerializer<ItemResearchIcon> getSerializer() {
        return SERIALIZER;
    }

    public static ItemResearchIcon single(ItemStack stack) {
        return new ItemResearchIcon(Collections.singletonList(stack));
    }

    public static ItemResearchIcon single(ItemLike item) {
        return new ItemResearchIcon(Collections.singletonList(new ItemStack(item)));
    }

}
