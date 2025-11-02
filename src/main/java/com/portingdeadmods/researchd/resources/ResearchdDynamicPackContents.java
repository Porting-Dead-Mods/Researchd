package com.portingdeadmods.researchd.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.portingdeadmods.portingdeadlibs.api.resources.DynamicPack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.resources.contents.ResearchdLang;
import com.portingdeadmods.researchd.resources.contents.ResearchdRecipes;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPacks;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearches;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Map;
import java.util.function.Function;

public class ResearchdDynamicPackContents {
    public static void writeData(DynamicPack pack) {
        writeResearchdRegistry(pack, ResearchdResearchPacks::new, ResearchPackImpl.CODEC, "research_pack");
        writeResearchdRegistry(pack, ResearchdResearches::new, Research.CODEC, "research");
        writeRecipeRegistry(pack, ResearchdRecipes::new, Recipe.CODEC, "recipe");
    }

    public static void writeAssets(DynamicPack pack) {
        writeLang(pack);
    }

    private static void writeLang(DynamicPack pack) {
        ResearchdLang provider = new ResearchdLang(Researchd.MODID);
        provider.build();

        JsonObject object = new JsonObject();
        for (Map.Entry<String, String> entry : provider.getContents().entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }

        pack.put(Researchd.rl("lang/en_us"), object);
    }

    private static void writeRecipeRegistry(DynamicPack pack, Function<String, ResearchdRecipes> providerFactory, Codec<Recipe<?>> codec, String path) {
        ResearchdRecipes provider = providerFactory.apply(Researchd.MODID);
        provider.build();

        for (Map.Entry<ResourceLocation, Recipe<?>> entry : provider.getContents().entrySet()) {
            Recipe<?> recipe = entry.getValue();
            DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, recipe);
            result.ifSuccess(json -> pack.put(entry.getKey().withPrefix(path + "/"), json));
        }
    }

    private static <T, P extends ResearchdDatagenProvider<T>> void writeResearchdRegistry(DynamicPack pack, Function<String, P> providerFactory, Codec<T> codec, String path) {
        P provider = providerFactory.apply(Researchd.MODID);
        provider.build();

        for (Map.Entry<ResourceKey<T>, T> entry : provider.getContents().entrySet()) {
            T research = entry.getValue();
            DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, research);
            result.ifSuccess(json -> pack.put(entry.getKey().location().withPrefix("researchd/" + path + "/"), json));
        }
    }

}
