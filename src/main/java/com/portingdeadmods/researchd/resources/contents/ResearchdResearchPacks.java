package com.portingdeadmods.researchd.resources.contents;

import com.mojang.serialization.Codec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class ResearchdResearchPacks implements ResearchdResearchPackProvider {
    public static final Identifier OVERWORLD_PACK_LOC = Researchd.rl("overworld");
    public static final Identifier NETHER_PACK_LOC = Researchd.rl("nether");
    public static final Identifier END_PACK_LOC = Researchd.rl("end");

    private final String modid;
    private final Map<ResourceKey<ResearchPack>, ResearchPack> researchPacks;

    public ResearchdResearchPacks(String modid) {
        this.modid = modid;
        this.researchPacks = new HashMap<>();
    }

    @Override
    public String modid() {
        return modid;
    }

    @Override
    public ResourceKey<Registry<ResearchPack>> registry() {
        return ResearchdRegistries.RESEARCH_PACK_KEY;
    }

    @Override
    public void build() {
        researchPack(
                "overworld",
                builder -> builder.literalName("Overworld Pack").sortingValue(1).color(20, 240, 20));
        researchPack(
                "nether",
                builder -> builder.literalName("Nether Pack").sortingValue(2).color(160, 20, 20));
        researchPack(
                "end",
                builder -> builder.literalName("End Pack").sortingValue(3).color(63, 36, 83));
    }

    public void buildExampleDatapack() {
        researchPack("test_pack", builder -> builder.sortingValue(1).color(120, 150, 90));
    }

    @Override
    public Codec<ResearchPack> codec() {
        return ResearchPack.CODEC;
    }

    @Override
    public Map<ResourceKey<ResearchPack>, ResearchPack> contents() {
        return this.researchPacks;
    }
}
