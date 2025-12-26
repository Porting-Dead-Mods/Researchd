package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.resources.ResearchdDatagenProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ResearchdResearches implements ResearchdDatagenProvider<Research>, ResearchdResearchProvider {
    public static final ResourceLocation COBBLESTONE_LOC = Researchd.rl("cobblestone");
    public static final ResourceLocation OVERWORLD_PACK_LOC = Researchd.rl("overworld_pack");
    public static final ResourceLocation NETHER_LOC = Researchd.rl("nether");
    public static final ResourceLocation END_LOC = Researchd.rl("the_end");
    public static final ResourceLocation END_CRYSTAL_LOC = Researchd.rl("end_crystal");
    public static final ResourceLocation BEACON_LOC = Researchd.rl("beacon");
    public static final ResourceLocation STONE_LOC = Researchd.rl("stone");
    public static final ResourceLocation DIFFERENT_ROCKS_LOC = Researchd.rl("different_rocks");

    private final String modid;
    private final Map<ResourceKey<Research>, Research> researches;

    public ResearchdResearches(String modid) {
        this.modid = modid;
        this.researches = new HashMap<>();
    }

    @Override
    public String getModid() {
        return modid;
    }

    @Override
    public Map<ResourceKey<Research>, Research> getResearches() {
        return researches;
    }

    @Override
    public void build() {
         ResourceKey<Research> cobblestone = simpleResearch("cobblestone", builder -> builder
                .icon(Items.COBBLESTONE)
                .method(consumeItem(Items.COBBLESTONE, 4))
                .effect(
                        and(unlockRecipe(mcLoc("stone_pickaxe")), unlockRecipe(mcLoc("furnace")))
                ));
        ResourceKey<Research> stone = simpleResearch("stone", builder -> builder
                .icon(Items.STONE)
                .method(hasItem(Items.COBBLESTONE, 64))
                .parents(cobblestone)
                .effect(
                        and(
                                unlockItem(Items.STONE),
                                unlockItem(Items.STONE_BRICKS),
                                unlockItem(Items.STONE_BRICK_SLAB),
                                unlockItem(Items.STONE_BRICK_STAIRS),
                                unlockItem(Items.SMOOTH_STONE),
                                unlockItem(Items.STONECUTTER)
                        )
                ));
        ResourceKey<Research> different_rocks = simpleResearch("different_rocks", builder -> builder
                .icon(Items.DIORITE)
                .method(
                    and(
                        or(
                                hasItem(Items.DIORITE, 16),
                                hasItem(Items.ANDESITE, 16),
                                hasItem(Items.GRANITE, 16)
                        ),
                        hasItem(Items.STONE, 64)
                    )
                )
                .parents(stone)
                .effect(
                        and(
                                unlockItem(Items.STONE),
                                unlockItem(Items.STONE_BRICKS),
                                unlockItem(Items.STONE_BRICK_SLAB),
                                unlockItem(Items.STONE_BRICK_STAIRS),
                                unlockItem(Items.SMOOTH_STONE),
                                unlockItem(Items.STONECUTTER)
                        )
                ));
         ResourceKey<Research> overworldPack = simpleResearch("overworld_pack", builder -> builder
                .icon(ResearchdItems.RESEARCH_LAB.asItem())
                .parents(cobblestone)
                .method(consumeItem(Items.IRON_INGOT, 4))
                .effect(
                        and(unlockRecipe(modLoc("research_lab")), unlockRecipe(modLoc("overworld_pack")))
                ));
         ResourceKey<Research> nether = simpleResearch("nether", builder -> builder
                .icon(Items.NETHERRACK)
                .parents(overworldPack)
                .method(
                        consumePack(25, 100, pack(ResearchdResearchPacks.OVERWORLD_PACK_LOC))
                )
                .effect(
                        and(
                                unlockRecipe(modLoc("nether_pack")),
                                unlockDimension(mcLoc("the_nether"), DimensionUnlockEffect.NETHER_SPRITE)
                        )
                ));
         ResourceKey<Research> cake = simpleResearch("cake", builder -> builder
                .icon(Items.CAKE)
                .parents(nether)
                .method(
                        hasItem(Items.CAKE, 1)
                )
                .effect(
                        unlockItem(Items.CAKE)
                ));
         ResourceKey<Research> end = simpleResearch("the_end", builder -> builder
                .icon(Items.END_STONE)
                .parents(nether)
                .method(consumePack(100, 200, pack(ResearchdResearchPacks.OVERWORLD_PACK_LOC), pack(ResearchdResearchPacks.NETHER_PACK_LOC)))
                .effect(
                        and(
                                unlockRecipe(modLoc("end_pack")),
                                unlockDimension(mcLoc("the_end"), DimensionUnlockEffect.END_SPRITE)
                        )
                ));
         simpleResearch("end_crystal", builder -> builder
                .icon(Items.END_CRYSTAL)
                .researchPage(ResearchdResearchPages.END_CRYSTAL)
                .method(consumePack(250, 200, pack(ResearchdResearchPacks.OVERWORLD_PACK_LOC), pack(ResearchdResearchPacks.NETHER_PACK_LOC), pack(ResearchdResearchPacks.END_PACK_LOC)))
                .effect(
                        and(unlockRecipe(mcLoc("end_crystal")))
                ));
         simpleResearch("beacon", builder -> builder
                .icon(Items.BEACON)
                .parents(end)
                .method(
                        consumePack(250, 200, pack(ResearchdResearchPacks.OVERWORLD_PACK_LOC), pack(ResearchdResearchPacks.NETHER_PACK_LOC), pack(ResearchdResearchPacks.END_PACK_LOC))
                )
                .effect(and(
                                unlockRecipe(mcLoc("beacon"))
                )));
    }

    public void buildExampleDatapack() {
        ResourceKey<Research> wood = simpleResearch("wood", builder -> builder
                .literalName("Wood")
                .literalDescription("Boxing match")
                .icon(Items.OAK_LOG)
                .method(and(consumeItem(Items.DIRT, 8), consumeItem(Items.WHEAT_SEEDS, 1)))
                .effect(unlockRecipe(mcLoc("oak_planks"))));
        ResourceKey<Research> iron = simpleResearch("iron", builder -> builder
                .literalName("Iron")
                .literalDescription("Acquire software")
                .icon(Items.IRON_INGOT)
                .parents(wood)
                .method(or(consumeItem(Items.FURNACE, 1), consumeItem(Items.COBBLESTONE, 8)))
                .effect(unlockRecipe(mcLoc("iron_pickaxe"))));
        simpleResearch("nether_dim", builder -> builder
                .literalName("Nether")
                .literalDescription("The second dimension")
                .icon(Items.NETHERRACK)
                .parents(iron)
                .method(consumePack(50, 200, pack(modLoc("test_pack"))))
                .effect(and(unlockRecipe(mcLoc("netherite_ingot")), unlockRecipe(mcLoc("gold_block")))));
    }

    @Override
    public Map<ResourceKey<Research>, Research> getContents() {
        return this.researches;
    }
}
