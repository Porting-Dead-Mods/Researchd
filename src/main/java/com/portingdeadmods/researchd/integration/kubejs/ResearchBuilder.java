package com.portingdeadmods.researchd.integration.kubejs;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

@ReturnsSelf
public class ResearchBuilder extends BuilderBase<Research> {
    private Item icon = Items.BOOK;
    private ResearchMethod researchMethod;
    private ResearchEffect researchEffect = EmptyResearchEffect.INSTANCE;
    private final List<ResourceKey<Research>> parents = new ArrayList<>();
    private boolean requiresParent = false;
    private String literalName = null;
    private String literalDescription = null;

    public ResearchBuilder(ResourceLocation id) {
        super(id);
        this.researchMethod = new ConsumeItemResearchMethod(Ingredient.of(Items.BOOK), 1);
    }

    public ResearchBuilder icon(String itemId) {
        this.icon = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        return this;
    }


    public ResearchBuilder method(ResearchMethod method) {
        this.researchMethod = method;
        return this;
    }

    public ResearchBuilder consumeItem(String itemId, int count) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        this.researchMethod = new ConsumeItemResearchMethod(Ingredient.of(item), count);
        return this;
    }

    public ResearchBuilder consumePack(String packId, Object... args) {
        ResourceKey<SimpleResearchPack> pack = ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.parse(packId));
        
        int count = 1;
        int duration = 10;
        
        if (args.length >= 1 && args[0] instanceof Number) {
            count = ((Number) args[0]).intValue();
        }
        if (args.length >= 2 && args[1] instanceof Number) {
            duration = ((Number) args[1]).intValue();
        }
        
        this.researchMethod = new ConsumePackResearchMethod(List.of(pack), count, duration);
        return this;
    }

    public ResearchBuilder requireAllMethods(ResearchMethod... methods) {
        this.researchMethod = new AndResearchMethod(List.of(methods));
        return this;
    }

    public ResearchBuilder requireAnyMethod(ResearchMethod... methods) {
        this.researchMethod = new OrResearchMethod(List.of(methods));
        return this;
    }

    public ResearchBuilder effect(ResearchEffect effect) {
        this.researchEffect = effect;
        return this;
    }

    public ResearchBuilder unlockRecipe(String itemId, String recipeId) {
        this.researchEffect = new RecipeUnlockEffect(BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(itemId)).map(ItemStack::new), Optional.empty(), Set.of(ResourceLocation.parse(recipeId)));
        return this;
    }

    public ResearchBuilder unlockMultipleRecipes(String itemId, String... recipeIds) {
        Set<ResourceLocation> recipes = new HashSet<>();
        for (String recipeId : recipeIds) {
            recipes.add(ResourceLocation.parse(recipeId));
        }
        this.researchEffect = new RecipeUnlockEffect(BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(itemId)).map(ItemStack::new), Optional.empty(), recipes);
        return this;
    }

    public ResearchBuilder unlockDimension(String dimensionId, Object... args) {
        ResourceLocation dimension = ResourceLocation.parse(dimensionId);
        ResourceLocation iconSprite = DimensionUnlockEffect.DEFAULT_SPRITE;
        
        if (args.length >= 1 && args[0] instanceof String) {
            iconSprite = ResourceLocation.parse((String) args[0]);
        }
        
        this.researchEffect = new DimensionUnlockEffect(dimension, iconSprite);
        return this;
    }

    public ResearchBuilder combineEffects(ResearchEffect... effects) {
        this.researchEffect = new AndResearchEffect(List.of(effects));
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


    public ResearchBuilder noParentRequired() {
        this.requiresParent = false;
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

    @Override
    public Research createObject() {
        SimpleResearch.Builder builder = SimpleResearch.Builder.of();
        builder.icon(icon);
        builder.researchMethod(researchMethod);
        builder.researchEffect(researchEffect);
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