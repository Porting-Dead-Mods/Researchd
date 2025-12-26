package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.resources.ResearchdDatagenProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ResearchdResearchPacks implements ResearchdDatagenProvider<ResearchPack>, ResearchdResearchPackProvider {
    public static final ResourceLocation OVERWORLD_PACK_LOC = Researchd.rl("overworld");
    public static final ResourceLocation NETHER_PACK_LOC = Researchd.rl("nether");
    public static final ResourceLocation END_PACK_LOC = Researchd.rl("end");

    private final String modid;
    private final Map<ResourceKey<ResearchPack>, ResearchPack> researchPacks;

    public ResearchdResearchPacks(String modid) {
        this.modid = modid;
        this.researchPacks = new HashMap<>();
    }

    @Override
    public String getModid() {
        return modid;
    }

    @Override
    public Map<ResourceKey<ResearchPack>, ResearchPack> getResearchPacks() {
        return researchPacks;
    }

    @Override
    public void build() {
        researchPack("overworld", builder -> builder
                .literalName("Overworld Pack")
                .sortingValue(1)
                .color(20, 240, 20));
        researchPack("nether", builder -> builder
                .literalName("Nether Pack")
                .sortingValue(2)
                .color(160, 20, 20));
        researchPack("end", builder -> builder
                .literalName("End Pack")
                .sortingValue(3)
                .color(63, 36, 83));
    }

    public void buildExampleDatapack() {
        researchPack("test_pack", builder -> builder
                .sortingValue(1)
                .color(120, 150, 90));
    }

    @Override
    public Map<ResourceKey<ResearchPack>, ResearchPack> getContents() {
        return this.researchPacks;
    }
}
