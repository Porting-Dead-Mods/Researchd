package com.portingdeadmods.researchd.compat.kubejs.builders;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.impl.research.ItemResearchIcon;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import dev.latvian.mods.kubejs.script.SourceLine;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ResearchBuilder {
    public final ResourceLocation id;
    public SourceLine sourceLine;
    private ItemResearchIcon icon = ItemResearchIcon.EMPTY;
    private ResearchMethod researchMethod;
    private ResearchEffect researchEffect = EmptyResearchEffect.INSTANCE;
    private final List<ResourceKey<Research>> parents = new ArrayList<>();
    private boolean requiresParent = false;
    private String literalName = null;
    private String literalDescription = null;

    public ResearchBuilder(ResourceLocation id) {
        this.id = id;
        this.sourceLine = SourceLine.UNKNOWN;
        this.researchMethod = new ConsumeItemResearchMethod(Ingredient.of(Items.BOOK), 1);
    }

    public ResearchBuilder icon(String... itemId) {
        return this.iconStacks(Stream.of(itemId)
                .map(ResourceLocation::parse)
                .map(BuiltInRegistries.ITEM::get)
                .map(Item::getDefaultInstance)
                .toArray(ItemStack[]::new));
    }

    public ResearchBuilder iconStacks(ItemStack... stacks) {
        this.icon = new ItemResearchIcon(Arrays.asList(stacks));
        return this;
    }

    public ResearchBuilder method(ResearchMethod method) {
        this.researchMethod = method;
        return this;
    }

    public ResearchBuilder effect(ResearchEffect effect) {
        this.researchEffect = effect;
        return this;
    }

    public ResearchBuilder parent(String parent) {
        ResourceLocation location = ResourceLocation.parse(parent);
        this.parents.add(ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, location));
        return this;
    }

    public ResearchBuilder parents(String... parents) {
        for (String parent : parents) {
            parent(parent);
        }
        return this;
    }

    public ResearchBuilder requiresParents(boolean requiresParent) {
        this.requiresParent = requiresParent;
        return this;
    }

    public ResearchBuilder literalName(String name) {
        this.literalName = name;
        return this;
    }

    public ResearchBuilder literalDescription(String description) {
        this.literalDescription = description;
        return this;
    }

    public Research createObject() {
        if (parents.isEmpty() && requiresParent) {
            throw new IllegalStateException("Research '" + id + "' requires a parent but has no parents defined. Set requiresParent to false or add parents.");
        }
        
        SimpleResearch.Builder builder = SimpleResearch.builder();
        builder.icon(icon);
        builder.method(researchMethod);
        builder.effect(researchEffect);
        for (ResourceKey<Research> parent : parents) {
            builder.parents(parent);
        }
        builder.requiresParent(requiresParent);
        if (literalName != null) {
            builder.literalName(literalName);
        }
        if (literalDescription != null) {
            builder.literalDescription(literalDescription);
        }
        return builder.build();
    }
}