package com.portingdeadmods.researchd.gametest;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.Filterable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Recipe blocking scenarios (ADR 0003). Each test gives a mock player a fresh team and checks the crafting-table
 * output slot the player would see.
 * <p>
 * The researches the tests block and complete live in the GameTest data, under {@code researchd:gametest/}.
 */
@EventBusSubscriber(modid = Researchd.MODID)
public final class RecipeBlockingTests {
    /** Blocks the {@code minecraft:bread} recipe by id. */
    private static final ResourceKey<Research> BREAD = research("gametest/bread");
    /**
     * Blocks the recipe {@code researchd:gametest/fall_through_a}. It and {@code fall_through_b} both craft a dead
     * bush, into a diamond and an emerald; {@code a} comes first.
     */
    private static final ResourceKey<Research> FALL_THROUGH = research("gametest/fall_through");
    /** Blocks the items {@code white_carpet}, {@code bamboo_planks}, {@code red_shulker_box} and {@code firework_rocket}. */
    private static final ResourceKey<Research> BLOCKED_ITEMS = research("gametest/blocked_items");
    /**
     * Blocks the vanilla {@code minecraft:book_cloning} recipe, which keeps the written book in the grid.
     * {@code researchd:gametest/book_to_paper} crafts the same grid into paper and uses both books up.
     */
    private static final ResourceKey<Research> BOOK_CLONING = research("gametest/book_cloning");

    private static final String NAME = "recipe_blocking";
    private static final int MAX_TICKS = 20;

    private static final List<GameTestCase> TESTS = List.of(
            test(
                    "blocked_recipe_crafts_once_its_research_completes",
                    RecipeBlockingTests::blockedRecipeCraftsOnceItsResearchCompletes),
            test("recipe_making_a_blocked_item_is_blocked", RecipeBlockingTests::recipeMakingABlockedItemIsBlocked),
            test(
                    "only_blocked_items_in_the_grid_block_a_recipe",
                    RecipeBlockingTests::onlyBlockedItemsInTheGridBlockARecipe),
            test(
                    "dyeing_a_shulker_box_a_blocked_colour_is_blocked",
                    RecipeBlockingTests::dyeingAShulkerBoxABlockedColourIsBlocked),
            test(
                    "recipe_without_a_fixed_result_making_a_blocked_item_is_blocked",
                    RecipeBlockingTests::recipeWithoutAFixedResultMakingABlockedItemIsBlocked),
            test(
                    "blocked_first_match_falls_through_to_the_next",
                    RecipeBlockingTests::blockedFirstMatchFallsThroughToTheNext),
            test(
                    "taking_a_fallen_through_output_leaves_its_own_remainders",
                    RecipeBlockingTests::takingAFallenThroughOutputLeavesItsOwnRemainders),
            test("blocked_recipe_hint_is_skipped", RecipeBlockingTests::blockedRecipeHintIsSkipped),
            test(
                    "recipe_lists_drop_blocked_recipes_under_a_team_context",
                    RecipeBlockingTests::recipeListsDropBlockedRecipesUnderATeamContext),
            test(
                    "recipe_lists_update_once_research_completes",
                    RecipeBlockingTests::recipeListsUpdateOnceResearchCompletes));

    private static GameTestCase test(String name, Consumer<GameTestHelper> function) {
        return new GameTestCase(NAME + "/" + name, MAX_TICKS, function);
    }

    private static ResourceKey<Research> research(String path) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Researchd.rl(path));
    }

    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        GameTestCase.registerFunctions(event, TESTS);
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment =
                event.registerEnvironment(Researchd.rl(NAME), new TestEnvironmentDefinition.AllOf(List.of()));
        GameTestCase.registerInstances(event, environment, TESTS);
    }

    /** Also proves the recipe unblocks without the player relogging: the same open menu crafts it. */
    private static void blockedRecipeCraftsOnceItsResearchCompletes(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        ResearchTeam team = TestTeams.create(helper, player);
        TestCrafting crafting = TestCrafting.open(helper, player);
        ItemStack wheat = new ItemStack(Items.WHEAT);

        crafting.fill(wheat, wheat, wheat);
        crafting.assertNoOutput(helper, "bread before its research");

        TestTeams.complete(helper, team, BREAD);
        crafting.fill(wheat, wheat, wheat);
        crafting.assertOutput(helper, new ItemStack(Items.BREAD), "bread after its research");

        helper.succeed();
    }

    private static void recipeMakingABlockedItemIsBlocked(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        ResearchTeam team = TestTeams.create(helper, player);
        TestCrafting crafting = TestCrafting.open(helper, player);
        ItemStack wool = new ItemStack(Items.WHITE_WOOL);

        crafting.fill(wool, wool);
        crafting.assertNoOutput(helper, "white carpet while white carpet is Blocked");

        TestTeams.complete(helper, team, BLOCKED_ITEMS);
        crafting.fill(wool, wool);
        crafting.assertOutput(helper, new ItemStack(Items.WHITE_CARPET, 3), "white carpet once unblocked");
        helper.succeed();
    }

    /** The stick recipe takes any planks. Blocking bamboo planks must not stop sticks from oak planks. */
    private static void onlyBlockedItemsInTheGridBlockARecipe(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        TestTeams.create(helper, player);
        TestCrafting crafting = TestCrafting.open(helper, player);
        ItemStack oak = new ItemStack(Items.OAK_PLANKS);
        ItemStack bamboo = new ItemStack(Items.BAMBOO_PLANKS);

        crafting.fill(oak, ItemStack.EMPTY, ItemStack.EMPTY, oak);
        crafting.assertOutput(helper, new ItemStack(Items.STICK, 4), "sticks from oak planks");

        crafting.fill(bamboo, ItemStack.EMPTY, ItemStack.EMPTY, bamboo);
        crafting.assertNoOutput(helper, "sticks from Blocked bamboo planks");
        helper.succeed();
    }

    /** Each colour is a recipe of its own, whose result is the dyed box. */
    private static void dyeingAShulkerBoxABlockedColourIsBlocked(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        TestTeams.create(helper, player);
        TestCrafting crafting = TestCrafting.open(helper, player);
        ItemStack box = new ItemStack(Items.SHULKER_BOX);

        crafting.fill(box, new ItemStack(Items.BLUE_DYE));
        crafting.assertOutput(helper, new ItemStack(Items.BLUE_SHULKER_BOX), "blue shulker box");

        crafting.fill(box, new ItemStack(Items.RED_DYE));
        crafting.assertNoOutput(helper, "Blocked red shulker box");
        helper.succeed();
    }

    /** The firework rocket recipe shows no result: only the rocket it would assemble is Blocked. */
    private static void recipeWithoutAFixedResultMakingABlockedItemIsBlocked(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        TestTeams.create(helper, player);
        TestCrafting crafting = TestCrafting.open(helper, player);

        crafting.fill(new ItemStack(Items.PAPER), new ItemStack(Items.GUNPOWDER));
        crafting.assertNoOutput(helper, "Blocked firework rocket");
        helper.succeed();
    }

    private static void blockedFirstMatchFallsThroughToTheNext(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        ResearchTeam team = TestTeams.create(helper, player);
        TestCrafting crafting = TestCrafting.open(helper, player);
        ItemStack deadBush = new ItemStack(Items.DEAD_BUSH);

        crafting.fill(deadBush);
        crafting.assertOutput(helper, new ItemStack(Items.EMERALD), "second match while the first is Blocked");

        // Proves the Blocked recipe really is the first match
        TestTeams.complete(helper, team, FALL_THROUGH);
        crafting.fill(deadBush);
        crafting.assertOutput(helper, new ItemStack(Items.DIAMOND), "first match once unblocked");
        helper.succeed();
    }

    /**
     * The recipe book passes the recipe it placed as a hint, which a lookup returns without searching when it
     * matches. A Blocked hint must be skipped like any other Blocked match.
     */
    /** Taking the output looks the recipe up again to find what stays in the grid. It must find the same recipe. */
    private static void takingAFallenThroughOutputLeavesItsOwnRemainders(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        TestTeams.create(helper, player);
        TestCrafting crafting = TestCrafting.open(helper, player);
        ItemStack writtenBook = new ItemStack(Items.WRITTEN_BOOK);
        writtenBook.set(
                DataComponents.WRITTEN_BOOK_CONTENT,
                new WrittenBookContent(Filterable.passThrough("GameTest"), "GameTest", 0, List.of(), true));

        crafting.fill(writtenBook, new ItemStack(Items.WRITABLE_BOOK));
        crafting.assertOutput(helper, new ItemStack(Items.PAPER), "paper while book cloning is Blocked");

        crafting.take(player);
        crafting.assertGridEmpty(helper, "grid after taking the paper");
        helper.succeed();
    }

    private static void blockedRecipeHintIsSkipped(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ResearchTeam team = TestTeams.create(helper, TestPlayers.create(helper));
        CraftingInput wheat = CraftingInput.of(3, 1, List.of(
                new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT)));
        ResourceKey<Recipe<?>> bread = vanillaRecipe("bread");

        Supplier<Optional<RecipeHolder<CraftingRecipe>>> lookup =
                () -> level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, wheat, level, bread);

        Optional<RecipeHolder<CraftingRecipe>> found = TestRecipes.underTeamContext(team, level, lookup);
        helper.assertTrue(found.isEmpty(), "Blocked hint was returned: " + found.map(RecipeHolder::id));
        helper.assertValueEqual(
                Optional.of(bread), lookup.get().map(RecipeHolder::id), "recipe found without a Team Context");
        helper.succeed();
    }

    /**
     * With no input to go by, an ingredient blocks a recipe only when every item it accepts is Blocked: bamboo slabs
     * take nothing but bamboo planks, while sticks take any planks.
     */
    private static void recipeListsDropBlockedRecipesUnderATeamContext(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ResearchTeam team = TestTeams.create(helper, TestPlayers.create(helper));
        Set<ResourceKey<Recipe<?>>> withoutContext = TestRecipes.craftingRecipeIds(level);
        Set<ResourceKey<Recipe<?>>> withContext =
                TestRecipes.underTeamContext(team, level, () -> TestRecipes.craftingRecipeIds(level));

        helper.assertValueEqual(
                TestRecipes.allCraftingRecipeIds(level), withoutContext, "crafting recipes without a Team Context");
        for (String blocked : List.of("bread", "white_carpet", "bamboo_slab")) {
            ResourceKey<Recipe<?>> id = vanillaRecipe(blocked);
            helper.assertTrue(withoutContext.contains(id), blocked + " missing without a Team Context");
            helper.assertFalse(withContext.contains(id), blocked + " listed while Blocked");
        }
        helper.assertTrue(withContext.contains(vanillaRecipe("stick")), "stick missing while oak planks are allowed");
        helper.succeed();
    }

    /** Lists are cached per team, so this proves completing a research refreshes them. */
    private static void recipeListsUpdateOnceResearchCompletes(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ResearchTeam team = TestTeams.create(helper, TestPlayers.create(helper));
        ResourceKey<Recipe<?>> bread = vanillaRecipe("bread");
        Supplier<Set<ResourceKey<Recipe<?>>>> listed =
                () -> TestRecipes.underTeamContext(team, level, () -> TestRecipes.craftingRecipeIds(level));

        helper.assertFalse(listed.get().contains(bread), "bread listed before its research");
        TestTeams.complete(helper, team, BREAD);
        helper.assertTrue(listed.get().contains(bread), "bread missing after its research");
        helper.succeed();
    }

    private static ResourceKey<Recipe<?>> vanillaRecipe(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace(path));
    }
}
