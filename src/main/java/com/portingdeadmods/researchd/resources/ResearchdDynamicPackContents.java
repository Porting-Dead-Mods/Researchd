package com.portingdeadmods.researchd.resources;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.portingdeadmods.portingdeadlibs.api.resources.DynamicPack;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.registries.ResearchdResearches;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class ResearchdDynamicPackContents {
    public static void write(DynamicPack pack) {
        ResearchdResearches researches = new ResearchdResearches(Researchd.MODID);
        researches.build();

        for (Map.Entry<ResourceKey<Research>, Research> entry : researches.getResearches().entrySet()) {
            Research research = entry.getValue();
            Codec<Research> codec = Research.CODEC;
            DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, research);
            result.ifSuccess(json -> pack.put(entry.getKey().location().withPrefix("researchd/research/"), json));
            Researchd.LOGGER.debug("Research: {}", entry.getKey());
        }



        Researchd.LOGGER.debug("Done");
    }

}
