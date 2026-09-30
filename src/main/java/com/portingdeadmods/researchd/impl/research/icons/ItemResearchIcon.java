package com.portingdeadmods.researchd.impl.research.icons;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.serializers.ResearchIconSerializer;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

public record ItemResearchIcon(List<ItemStackTemplate> items) implements ResearchIcon {
    public static final ResearchIconSerializer<ItemResearchIcon> SERIALIZER =
            ResearchIconSerializer.simple(ItemStackTemplate.CODEC
                    .listOf()
                    .xmap(ItemResearchIcon::new, ItemResearchIcon::items)
                    .fieldOf("items"));
    public static final Identifier ID = Researchd.rl("item_research_icon");
    public static final ItemResearchIcon EMPTY = new ItemResearchIcon(Collections.emptyList());

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public ResearchIconSerializer<ItemResearchIcon> getSerializer() {
        return SERIALIZER;
    }

    public List<ItemStack> stacks() {
        return this.items.stream().map(ItemStackTemplate::create).toList();
    }

    public static ItemResearchIcon ofStacks(List<ItemStack> stacks) {
        return new ItemResearchIcon(stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStackTemplate::fromNonEmptyStack)
                .toList());
    }

    public static ItemResearchIcon single(ItemStack stack) {
        return ofStacks(Collections.singletonList(stack));
    }

    // No ItemStack here: the default datapack is built before item components are bound
    public static ItemResearchIcon single(ItemLike item) {
        return new ItemResearchIcon(List.of(new ItemStackTemplate(item.asItem())));
    }
}
