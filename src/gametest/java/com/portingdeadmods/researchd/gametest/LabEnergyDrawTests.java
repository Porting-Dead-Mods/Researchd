package com.portingdeadmods.researchd.gametest;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdConfig;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPacks;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearches;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Lab Energy Draw scenarios. Each test builds a Research Lab the way a player would, gives a fresh team a
 * current research, feeds the Lab through a Lab Part and checks only what a player could observe.
 * <p>
 * The draw and the buffer's capacity are global config values, so each setting of them is a test environment, which
 * makes it a batch of its own. Batches run one at a time; the environment sets both before the batch starts and
 * restores them once it ends.
 */
@EventBusSubscriber(modid = Researchd.MODID)
public final class LabEnergyDrawTests {
    private static final int DRAW = 10;
    /** The configured capacity's default. */
    private static final int CAPACITY = 100000;
    private static final int PACKS = 5;
    /** Duration of one pack for the {@code nether} research in the default datapack. */
    private static final int PACK_DURATION = 100;
    /** The Lab takes no packs before its first tick, see {@link TestLab}. */
    private static final int STOCK_TICK = 1;
    /** GameTest's default timeout on 1.21.1. */
    private static final int MAX_TICKS = 100;

    private static final ResourceKey<Research> RESEARCH =
            ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, ResearchdResearches.NETHER_LOC);
    private static final ResourceKey<ResearchPack> PACK =
            ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResearchdResearchPacks.OVERWORLD_PACK_LOC);

    private static final String NAME = "lab_energy_draw";

    private static final List<GameTestCase> DRAW_OFF = List.of(
            test("draw_zero_progresses_and_ignores_energy", LabEnergyDrawTests::drawZeroProgressesAndIgnoresEnergy));
    private static final List<GameTestCase> DRAW_ON = List.of(
            test("empty_buffer_stalls_research", LabEnergyDrawTests::emptyBufferStallsResearch),
            test("less_than_one_tick_stalls_research", LabEnergyDrawTests::lessThanOneTickStallsResearch),
            test(
                    "fully_powered_draws_one_tick_per_tick_of_progress",
                    300,
                    LabEnergyDrawTests::fullyPoweredDrawsOneTickPerTickOfProgress),
            test(
                    "lab_parts_accept_energy_but_do_not_give_it_back",
                    LabEnergyDrawTests::labPartsAcceptEnergyButDoNotGiveItBack),
            test("aborted_insert_stores_nothing", LabEnergyDrawTests::abortedInsertStoresNothing));
    /** A capacity below twice the draw, which the Lab raises to twice the draw. */
    private static final List<GameTestCase> SMALL_BUFFER = List.of(
            test("capacity_is_at_least_twice_the_draw", LabEnergyDrawTests::capacityIsAtLeastTwiceTheDraw));
    /** The one test in this batch lowers the capacity while it runs. */
    private static final List<GameTestCase> CAPACITY_DROP = List.of(
            test("buffer_follows_a_lowered_capacity", LabEnergyDrawTests::bufferFollowsALoweredCapacity));

    private static GameTestCase test(String name, Consumer<GameTestHelper> function) {
        return test(name, MAX_TICKS, function);
    }

    private static GameTestCase test(String name, int maxTicks, Consumer<GameTestHelper> function) {
        return new GameTestCase(NAME + "/" + name, maxTicks, function);
    }

    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        event.register(
                Registries.TEST_ENVIRONMENT_DEFINITION_TYPE,
                registry -> registry.register(Researchd.rl(NAME), EnergyEnvironment.CODEC));
        GameTestCase.registerFunctions(event, DRAW_OFF);
        GameTestCase.registerFunctions(event, DRAW_ON);
        GameTestCase.registerFunctions(event, SMALL_BUFFER);
        GameTestCase.registerFunctions(event, CAPACITY_DROP);
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> drawOff =
                event.registerEnvironment(Researchd.rl(NAME + "_off"), new EnergyEnvironment(0, CAPACITY));
        Holder<TestEnvironmentDefinition<?>> drawOn =
                event.registerEnvironment(Researchd.rl(NAME + "_on"), new EnergyEnvironment(DRAW, CAPACITY));
        Holder<TestEnvironmentDefinition<?>> smallBuffer =
                event.registerEnvironment(Researchd.rl(NAME + "_small_buffer"), new EnergyEnvironment(DRAW, 1));
        Holder<TestEnvironmentDefinition<?>> capacityDrop =
                event.registerEnvironment(Researchd.rl(NAME + "_capacity_drop"), new EnergyEnvironment(0, 1000));
        GameTestCase.registerInstances(event, drawOff, DRAW_OFF);
        GameTestCase.registerInstances(event, drawOn, DRAW_ON);
        GameTestCase.registerInstances(event, smallBuffer, SMALL_BUFFER);
        GameTestCase.registerInstances(event, capacityDrop, CAPACITY_DROP);
    }

    private static void drawZeroProgressesAndIgnoresEnergy(GameTestHelper helper) {
        Scenario scenario = Scenario.build(helper);
        helper.runAtTickTime(STOCK_TICK, () -> {
            scenario.stockPacks(helper);
            scenario.lab().insertEnergy(1000);
        });

        helper.runAtTickTime(50, () -> {
            helper.assertTrue(scenario.progress() > 0, "Lab should progress with the draw off");
            helper.assertValueEqual(PACKS - 1, scenario.lab().itemCount(), "packs left");
            helper.assertValueEqual(1000, scenario.lab().energyStored(), "energy stored");
            helper.succeed();
        });
    }

    private static void emptyBufferStallsResearch(GameTestHelper helper) {
        Scenario scenario = Scenario.build(helper);
        helper.runAtTickTime(STOCK_TICK, () -> scenario.stockPacks(helper));

        helper.runAtTickTime(50, () -> {
            scenario.assertIdle(helper);
            helper.assertValueEqual(0, scenario.lab().energyStored(), "energy stored");
            helper.succeed();
        });
    }

    private static void lessThanOneTickStallsResearch(GameTestHelper helper) {
        Scenario scenario = Scenario.build(helper);
        helper.runAtTickTime(STOCK_TICK, () -> {
            scenario.stockPacks(helper);
            scenario.lab().insertEnergy(DRAW - 1);
        });

        helper.runAtTickTime(50, () -> {
            scenario.assertIdle(helper);
            helper.assertValueEqual(DRAW - 1, scenario.lab().energyStored(), "energy stored");
            helper.succeed();
        });
    }

    /**
     * Stores exactly 150 ticks of draw. The Lab must research for exactly 150 ticks, then stop: that proves one
     * draw is taken per tick of progress. 150 ticks span two packs, so two packs must be used.
     */
    private static void fullyPoweredDrawsOneTickPerTickOfProgress(GameTestHelper helper) {
        int poweredTicks = 150;
        Scenario scenario = Scenario.build(helper);
        helper.runAtTickTime(STOCK_TICK, () -> {
            scenario.stockPacks(helper);
            scenario.lab().insertEnergy(DRAW * poweredTicks);
        });

        helper.runAtTickTime(250, () -> {
            float expected = (float) poweredTicks / PACK_DURATION;
            helper.assertTrue(
                    Math.abs(scenario.progress() - expected) < 1e-3,
                    "progress should be " + expected + " but was " + scenario.progress());
            helper.assertValueEqual(0, scenario.lab().energyStored(), "energy stored");
            helper.assertValueEqual(PACKS - 2, scenario.lab().itemCount(), "packs left");
            helper.succeed();
        });
    }

    private static void labPartsAcceptEnergyButDoNotGiveItBack(GameTestHelper helper) {
        TestLab lab = Scenario.build(helper).lab();

        helper.assertValueEqual(500, lab.insertEnergy(500), "energy accepted");
        helper.assertValueEqual(0, lab.extractEnergy(500), "energy extracted");
        helper.assertValueEqual(500, lab.energyStored(), "energy stored");
        helper.succeed();
    }

    /** A pipe probing the Lab's room opens a transaction, inserts, then aborts; the Lab must keep nothing. */
    private static void abortedInsertStoresNothing(GameTestHelper helper) {
        TestLab lab = Scenario.build(helper).lab();

        helper.assertValueEqual(500, lab.insertEnergyAndAbort(500), "energy accepted before the abort");
        helper.assertValueEqual(0, lab.energyStored(), "energy stored");
        helper.succeed();
    }

    private static void capacityIsAtLeastTwiceTheDraw(GameTestHelper helper) {
        TestLab lab = Scenario.build(helper).lab();

        helper.assertValueEqual(2 * DRAW, lab.insertEnergy(1000), "energy accepted");
        helper.assertValueEqual(2 * DRAW, lab.energyStored(), "energy stored");
        helper.succeed();
    }

    /** Fills a 1000 FE buffer, then lowers the capacity to 100 while the game runs. */
    private static void bufferFollowsALoweredCapacity(GameTestHelper helper) {
        TestLab lab = Scenario.build(helper).lab();
        helper.assertValueEqual(1000, lab.insertEnergy(1000), "energy accepted");
        ResearchdConfig.Server.researchLabEnergyCapacity = 100;

        helper.runAfterDelay(1, () -> {
            helper.assertValueEqual(100, lab.energyStored(), "energy stored");
            helper.assertValueEqual(0, lab.insertEnergy(1), "energy accepted into a full buffer");
            helper.succeed();
        });
    }

    /** A Research Lab placed by a mock player whose fresh team is researching {@link #RESEARCH}. */
    private record Scenario(ResearchTeam team, TestLab lab) {
        static Scenario build(GameTestHelper helper) {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);
            ResearchTeam team = TestTeams.create(helper, player);
            TestTeams.queue(helper, team, RESEARCH);
            return new Scenario(team, TestLab.place(helper, player));
        }

        void stockPacks(GameTestHelper helper) {
            this.lab.insert(helper, ResearchPackImpl.asStack(PACK).copyWithCount(PACKS));
        }

        float progress() {
            return this.team.getResearchProgresses().get(RESEARCH).getProgress();
        }

        void assertIdle(GameTestHelper helper) {
            helper.assertValueEqual(0f, this.progress(), "progress");
            helper.assertValueEqual(PACKS, this.lab.itemCount(), "packs left");
        }
    }

    /** Sets the Lab Energy Draw and the buffer's capacity while a batch runs, then restores the configured values. */
    private record EnergyEnvironment(int draw, int capacity) implements TestEnvironmentDefinition<EnergyEnvironment> {
        static final MapCodec<EnergyEnvironment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("draw").forGetter(EnergyEnvironment::draw),
                        ExtraCodecs.POSITIVE_INT.fieldOf("capacity").forGetter(EnergyEnvironment::capacity))
                .apply(instance, EnergyEnvironment::new));

        @Override
        public EnergyEnvironment setup(ServerLevel level) {
            EnergyEnvironment configured = new EnergyEnvironment(
                    ResearchdConfig.Server.researchLabEnergyUsage, ResearchdConfig.Server.researchLabEnergyCapacity);
            this.apply();
            return configured;
        }

        @Override
        public void teardown(ServerLevel level, EnergyEnvironment configured) {
            configured.apply();
        }

        private void apply() {
            ResearchdConfig.Server.researchLabEnergyUsage = this.draw;
            ResearchdConfig.Server.researchLabEnergyCapacity = this.capacity;
        }

        @Override
        public MapCodec<EnergyEnvironment> codec() {
            return CODEC;
        }
    }
}
