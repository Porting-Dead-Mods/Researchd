package com.portingdeadmods.researchd.gametest;

import com.portingdeadmods.researchd.Researchd;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * One GameTest. On 26.1 a test is two registry entries: its function, registered at startup, and a test instance
 * naming that function, its structure and its environment, registered with the data-driven tests.
 * <p>
 * A test class lists its cases and registers them from both events, see {@link LabEnergyDrawTests}. Tests that share
 * an environment run in one batch, and batches run one after another, so a batch's environment can safely change
 * global state such as config values.
 * <p>
 * Pass the expected value first to {@link GameTestHelper#assertValueEqual}: 26.1's failure message reads its first
 * argument as the expected one, whatever the parameter names say.
 *
 * @param name     the test's path under the {@code researchd} namespace, such as {@code lab_energy_draw/empty_buffer}
 * @param maxTicks the test fails if it hasn't succeeded by then
 */
public record GameTestCase(String name, int maxTicks, Consumer<GameTestHelper> function) {
    /** An empty 7x7x7 structure. */
    public static final Identifier EMPTY_7X7X7 = Researchd.rl("empty_7x7x7");

    public Identifier id() {
        return Researchd.rl(this.name);
    }

    public static void registerFunctions(RegisterEvent event, List<GameTestCase> tests) {
        event.register(
                Registries.TEST_FUNCTION,
                registry -> tests.forEach(test -> registry.register(test.id(), test.function())));
    }

    public static void registerInstances(
            RegisterGameTestsEvent event, Holder<TestEnvironmentDefinition<?>> environment, List<GameTestCase> tests) {
        for (GameTestCase test : tests) {
            ResourceKey<Consumer<GameTestHelper>> function = ResourceKey.create(Registries.TEST_FUNCTION, test.id());
            TestData<Holder<TestEnvironmentDefinition<?>>> data =
                    new TestData<>(environment, EMPTY_7X7X7, test.maxTicks(), 0, true);
            event.registerTest(test.id(), new FunctionGameTestInstance(function, data));
        }
    }
}
