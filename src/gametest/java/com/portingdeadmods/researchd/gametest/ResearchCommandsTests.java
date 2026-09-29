package com.portingdeadmods.researchd.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * {@code /researchd research unlock|remove}, run from the server's own command source as the console would. They see
 * the researches declared in KubeJS (see {@link KubeJSTests}) as well as the datapack ones.
 */
@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchCommandsTests {
    private static final ResourceKey<Research> KUBEJS_ROOT =
            ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Identifier.parse("researchd_kjs_test:root"));
    private static final ResourceKey<Research> KUBEJS_CHILD =
            ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Identifier.parse("researchd_kjs_test:child"));
    private static final ResourceKey<Research> DATAPACK_BREAD =
            ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Researchd.rl("gametest/bread"));

    private static final String NAME = "research_commands";
    private static final int MAX_TICKS = 20;

    private static final List<GameTestCase> TESTS = List.of(
            test("console_unlocks_a_kubejs_research", ResearchCommandsTests::consoleUnlocksAKubeJSResearch),
            test("unlock_all_covers_datapack_and_kubejs", ResearchCommandsTests::unlockAllCoversDatapackAndKubeJS),
            test("remove_reverses_one", ResearchCommandsTests::removeReversesOne),
            test("unknown_research_is_rejected", ResearchCommandsTests::unknownResearchIsRejected));

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

    private static void consoleUnlocksAKubeJSResearch(GameTestHelper helper) {
        ResearchTeam team = TestTeams.create(helper, TestPlayers.create(helper));
        run(helper, "researchd research unlock \"%s\" researchd_kjs_test:root".formatted(team.getName()));
        assertResearched(helper, team, KUBEJS_ROOT, true);
        assertResearched(helper, team, KUBEJS_CHILD, false);
        helper.succeed();
    }

    private static void unlockAllCoversDatapackAndKubeJS(GameTestHelper helper) {
        ResearchTeam team = TestTeams.create(helper, TestPlayers.create(helper));
        run(helper, "researchd research unlock \"%s\" all".formatted(team.getName()));
        assertResearched(helper, team, DATAPACK_BREAD, true);
        assertResearched(helper, team, KUBEJS_ROOT, true);
        assertResearched(helper, team, KUBEJS_CHILD, true);
        helper.succeed();
    }

    private static void removeReversesOne(GameTestHelper helper) {
        ResearchTeam team = TestTeams.create(helper, TestPlayers.create(helper));
        run(helper, "researchd research unlock \"%s\" all".formatted(team.getName()));
        run(helper, "researchd research remove \"%s\" researchd_kjs_test:child".formatted(team.getName()));
        assertResearched(helper, team, KUBEJS_CHILD, false);
        assertResearched(helper, team, KUBEJS_ROOT, true);
        helper.succeed();
    }

    private static void unknownResearchIsRejected(GameTestHelper helper) {
        ResearchTeam team = TestTeams.create(helper, TestPlayers.create(helper));
        String command = "researchd research unlock \"%s\" researchd_kjs_test:missing".formatted(team.getName());
        try {
            execute(helper, command);
        } catch (CommandSyntaxException e) {
            helper.assertTrue(
                    e.getMessage().contains("Unknown research"), "unexpected error: " + e.getMessage());
            helper.succeed();
            return;
        }
        helper.fail("/" + command + " was accepted");
    }

    private static void run(GameTestHelper helper, String command) {
        try {
            execute(helper, command);
        } catch (CommandSyntaxException e) {
            helper.fail("/" + command + " failed: " + e.getMessage());
        }
    }

    /** Runs {@code command} as the server console, which has no player. */
    private static void execute(GameTestHelper helper, String command) throws CommandSyntaxException {
        MinecraftServer server = helper.getLevel().getServer();
        CommandSourceStack console = server.createCommandSourceStack().withSuppressedOutput();
        server.getCommands().getDispatcher().execute(command, console);
    }

    private static void assertResearched(
            GameTestHelper helper, ResearchTeam team, ResourceKey<Research> research, boolean expected) {
        helper.assertValueEqual(
                expected, team.getResearches().get(research).isResearched(), research.identifier() + " researched");
    }
}
