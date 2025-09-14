# Java Data Generation

You can register new researches and research packs using the `BootstrapContext` in your data generation. Researches and research packs are `DataPackRegistries`, meaning they are loaded from data packs.

## Research Packs

Here is an example of how to register a new research pack:

```java
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public final class YourModResearchPacks {
	public static final ResourceKey<SimpleResearchPack> OVERWORLD = key("overworld");

	public static void bootstrap(BootstrapContext<SimpleResearchPack> context) {
		register(context, OVERWORLD, SimpleResearchPack.builder()
				.sortingValue(1)
				.color(222, 0, 0)
		);
	}

	private static void register(BootstrapContext<SimpleResearchPack> context, ResourceKey<SimpleResearchPack> key, SimpleResearchPack.Builder builder) {
		context.register(key, builder.build());
	}

	private static ResourceKey<SimpleResearchPack> key(String name) {
		return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, YourMod.rl(name));
	}
}
```

## Researches

Here is an example of how to register a new research:

```java
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public final class YourModResearches {
    private static final Map<ResourceKey<Research>, Research.Builder<?>> RESEARCHES = new HashMap<>();

    public static final ResourceKey<Research> WOOD = register("wood", builder -> builder
            .icon(Items.OAK_LOG)
            .researchMethod(
                    new ConsumeItemResearchMethod(Ingredient.of(Items.DIRT), 8)
            ));

    public static void bootstrap(BootstrapContext<Research> context) {
        for (Map.Entry<ResourceKey<Research>, Research.Builder<?>> research : RESEARCHES.entrySet()) {
            register(context, research.getKey(), research.getValue());
        }
    }

    private static void register(BootstrapContext<Research> context, ResourceKey<Research> key, Research.Builder<?> builder) {
        context.register(key, builder.build());
    }

    private static ResourceKey<Research> key(String name) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, YourMod.rl(name));
    }

    private static ResourceKey<Research> register(String name, UnaryOperator<SimpleResearch.Builder> builder) {
        ResourceKey<Research> key = key(name);
        RESEARCHES.put(key, builder.apply(SimpleResearch.Builder.of()));
        return key;
    }
}
```