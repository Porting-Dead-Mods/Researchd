package com.portingdeadmods.researchd.compat.kubejs.helpers;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.method.AndResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.CheckItemPresenceResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.OrResearchMethod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

public class ResearchMethodHelper {
    
    public static ResearchMethod consumeItem(String itemId, int count) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        return new ConsumeItemResearchMethod(Ingredient.of(item), count);
    }
    
    public static ResearchMethod consumePack(String packId, int count) {
        ResourceKey<ResearchPack> key = ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.parse(packId));
        return new ConsumePackResearchMethod(List.of(key), count, 10);
    }

    public static ResearchMethod consumePack(String packId, int count, int duration) {
        ResourceKey<ResearchPack> key = ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.parse(packId));
        return new ConsumePackResearchMethod(List.of(key), count, duration);
    }

    public static ResearchMethod checkItemPresence(String itemId, int count) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        return new CheckItemPresenceResearchMethod(Ingredient.of(item), count);
    }
    
    public static ResearchMethod and(ResearchMethod... methods) {
        return new AndResearchMethod(Arrays.asList(methods));
    }
    
    public static ResearchMethod or(ResearchMethod... methods) {
        return new OrResearchMethod(Arrays.asList(methods));
    }
    
}
