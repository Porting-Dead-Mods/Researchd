package com.portingdeadmods.researchd.gametest;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Recipe blocking in machines: a block entity ticks under the Team Context of the team that placed it.
 * Each test places furnaces as mock players with fresh teams.
 */
@EventBusSubscriber(modid = Researchd.MODID)
public final class MachineRecipeBlockingTests {
    /**
     * Blocks the recipe {@code researchd:gametest/quick_smelting}, which smelts flint into glowstone dust in a single
     * tick.
     */
    private static final ResourceKey<Research> QUICK_SMELTING =
            ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Researchd.rl("gametest/quick_smelting"));

    private static final BlockPos FURNACE = new BlockPos(2, 1, 3);
    private static final BlockPos OTHER_FURNACE = new BlockPos(4, 1, 3);
    /** Long enough for an unblocked furnace to light and smelt the flint several times over. */
    private static final int SMELT_TICKS = 10;

    private static final String NAME = "machine_recipe_blocking";
    private static final int MAX_TICKS = 40;

    private static final List<GameTestCase> TESTS = List.of(
            test("furnace_refuses_a_blocked_recipe", MachineRecipeBlockingTests::furnaceRefusesABlockedRecipe),
            test(
                    "furnace_processes_it_once_the_research_completes",
                    MachineRecipeBlockingTests::furnaceProcessesItOnceTheResearchCompletes),
            test("furnace_uses_its_placers_team", MachineRecipeBlockingTests::furnaceUsesItsPlacersTeam));

    private static GameTestCase test(String name, Consumer<GameTestHelper> function) {
        return new GameTestCase(NAME + "/" + name, MAX_TICKS, function);
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

    private static void furnaceRefusesABlockedRecipe(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        TestTeams.create(helper, player);
        TestFurnace furnace = TestFurnace.place(helper, player, FURNACE);
        furnace.load(new ItemStack(Items.FLINT));

        helper.runAfterDelay(SMELT_TICKS, () -> {
            furnace.assertNoResult(helper, "Blocked smelting recipe");
            helper.succeed();
        });
    }

    /** The furnace has already failed to find the recipe, so this also proves it isn't remembered as missing. */
    private static void furnaceProcessesItOnceTheResearchCompletes(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        ResearchTeam team = TestTeams.create(helper, player);
        TestFurnace furnace = TestFurnace.place(helper, player, FURNACE);
        furnace.load(new ItemStack(Items.FLINT));

        helper.runAfterDelay(SMELT_TICKS, () -> {
            furnace.assertNoResult(helper, "smelting before its research");
            TestTeams.complete(helper, team, QUICK_SMELTING);
            helper.succeedWhen(() -> furnace.assertResult(helper, new ItemStack(Items.GLOWSTONE_DUST), "smelting after its research"));
        });
    }

    private static void furnaceUsesItsPlacersTeam(GameTestHelper helper) {
        ServerPlayer researcher = TestPlayers.create(helper);
        ResearchTeam researchers = TestTeams.create(helper, researcher);
        TestTeams.complete(helper, researchers, QUICK_SMELTING);
        ServerPlayer other = TestPlayers.create(helper);
        TestTeams.create(helper, other);

        TestFurnace researched = TestFurnace.place(helper, researcher, FURNACE);
        TestFurnace blocked = TestFurnace.place(helper, other, OTHER_FURNACE);
        researched.load(new ItemStack(Items.FLINT));
        blocked.load(new ItemStack(Items.FLINT));

        helper.runAfterDelay(SMELT_TICKS, () -> {
            researched.assertResult(helper, new ItemStack(Items.GLOWSTONE_DUST), "furnace placed by the team with the research");
            blocked.assertNoResult(helper, "furnace placed by the team without it");
            helper.succeed();
        });
    }
}
