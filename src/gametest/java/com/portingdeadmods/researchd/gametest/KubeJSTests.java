package com.portingdeadmods.researchd.gametest;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffect;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.AndResearchEffect;
import com.portingdeadmods.researchd.impl.research.effect.DimensionUnlockEffect;
import com.portingdeadmods.researchd.impl.research.effect.RecipeUnlockEffect;
import com.portingdeadmods.researchd.impl.research.method.CheckItemPresenceResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.utils.registries.ResearchdManagers;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Researches and Research Packs declared in KubeJS load alongside the datapack ones. They come from
 * {@code src/gametest/kubejs/server_scripts/researchd_gametest.js}, which the gameTestServer run copies into its game
 * directory.
 */
@EventBusSubscriber(modid = Researchd.MODID)
public final class KubeJSTests {
    private static final ResourceKey<ResearchPack> PACK =
            ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, Identifier.parse("researchd_kjs_test:pack"));
    private static final ResourceKey<Research> ROOT =
            ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Identifier.parse("researchd_kjs_test:root"));
    private static final ResourceKey<Research> CHILD =
            ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, Identifier.parse("researchd_kjs_test:child"));

    private static final String NAME = "kubejs";
    private static final int MAX_TICKS = 20;

    private static final List<GameTestCase> TESTS = List.of(
            test("script_declares_a_research_pack", KubeJSTests::scriptDeclaresAResearchPack),
            test("script_declares_researches", KubeJSTests::scriptDeclaresResearches),
            test("script_research_completes", KubeJSTests::scriptResearchCompletes));

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

    private static void scriptDeclaresAResearchPack(GameTestHelper helper) {
        ResearchPack pack = ResearchdManagers.getResearchPacksManager(helper.getLevel())
                .getLookup()
                .get(PACK);
        helper.assertTrue(pack != null, "KubeJS Research Pack " + PACK.identifier() + " wasn't loaded");
        helper.assertValueEqual(100, ((ResearchPackImpl) pack).sortingValue(), "sorting value");
        helper.succeed();
    }

    private static void scriptDeclaresResearches(GameTestHelper helper) {
        SimpleResearch root = research(helper, ROOT);
        helper.assertTrue(
                root.researchMethod() instanceof CheckItemPresenceResearchMethod,
                "root's method is " + root.researchMethod());
        helper.assertValueEqual(Set.of("minecraft:gold_block"), recipes(root.researchEffect()), "root's recipes");

        SimpleResearch child = research(helper, CHILD);
        helper.assertValueEqual(List.of(ROOT), child.parents(), "child's parents");
        helper.assertTrue(
                child.researchMethod() instanceof ConsumePackResearchMethod,
                "child's method is " + child.researchMethod());
        helper.assertTrue(
                child.researchEffect() instanceof AndResearchEffect,
                "child's effect is " + child.researchEffect());
        List<ResearchEffect> effects = ((AndResearchEffect) child.researchEffect()).effects();
        helper.assertValueEqual(
                Set.of("minecraft:iron_block", "minecraft:iron_ingot_from_iron_block"),
                recipes(effects.get(0)),
                "child's recipes");
        helper.assertValueEqual(
                new DimensionUnlockEffect(Level.NETHER.identifier(), DimensionUnlockEffect.DEFAULT_SPRITE),
                effects.get(1),
                "child's dimension unlock");
        helper.succeed();
    }

    /** Completing a KubeJS research applies its Research Effect, dimension unlock included, without throwing. */
    private static void scriptResearchCompletes(GameTestHelper helper) {
        ServerPlayer player = TestPlayers.create(helper);
        ResearchTeam team = TestTeams.create(helper, player);
        TestTeams.complete(helper, team, ROOT);
        TestTeams.complete(helper, team, CHILD);
        helper.assertTrue(team.getResearches().get(CHILD).isResearched(), "child isn't researched");
        helper.succeed();
    }

    private static SimpleResearch research(GameTestHelper helper, ResourceKey<Research> key) {
        Research research =
                ResearchdManagers.getResearchesManager(helper.getLevel()).getLookup().get(key);
        helper.assertTrue(research != null, "KubeJS research " + key.identifier() + " wasn't loaded");
        return (SimpleResearch) research;
    }

    private static Set<String> recipes(ResearchEffect effect) {
        if (effect instanceof AndResearchEffect(List<ResearchEffect> effects)) {
            return effects.stream().flatMap(e -> recipes(e).stream()).collect(Collectors.toSet());
        }
        if (effect instanceof RecipeUnlockEffect unlock) {
            return unlock.recipes().stream()
                    .map(key -> key.identifier().toString())
                    .collect(Collectors.toSet());
        }
        throw new IllegalArgumentException("Not a recipe unlock: " + effect);
    }
}
