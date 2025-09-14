# Addons

This page provides an in-depth guide on how to create addons for Researchd, including creating custom research effects and methods.

## `ResearchEffect` Interface

A `ResearchEffect` is triggered when a research is successfully completed. It's how you grant rewards to the player, such as unlocking recipes, giving items, or applying any other custom effect.

To create a research effect, you need to implement the `ResearchEffect` interface.

```java
public interface ResearchEffect {
    void onUnlock(Level level, Player player, ResourceKey<Research> research);
    ResourceLocation id();
    ResearchEffectSerializer<?> getSerializer();
    // ... other methods
}
```

### Key Methods

*   `void onUnlock(Level level, Player player, ResourceKey<Research> research)`: This is the core method of the interface. It's called on the server side when the specified `research` is completed. You can use the `player` and `level` objects to apply your effects.

*   `ResearchEffectSerializer<?> getSerializer()`: Every research effect needs a serializer to handle networking and saving to disk. You'll need to create a serializer for your custom effect and return it here.

### Serialization and Registration

To allow your `ResearchEffect` to be saved and synced, you need to create a `ResearchEffectSerializer` and register it.

1.  **Create the Serializer**: Create a class that implements `ResearchEffectSerializer<YourResearchEffect>`. You'll need to implement `codec()` and `streamCodec()`.

    ```java
    public static final class Serializer implements ResearchEffectSerializer<YourResearchEffect> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<YourResearchEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                // ... your fields here
        ).apply(instance, YourResearchEffect::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, YourResearchEffect> STREAM_CODEC = StreamCodec.composite(
                // ... your fields here
        );

        @Override
        public @NotNull MapCodec<YourResearchEffect> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, YourResearchEffect> streamCodec() {
            return STREAM_CODEC;
        }
    }
    ```

2.  **Register the Serializer**: In your main mod class, use a `DeferredRegister` to register your serializer instance.

    ```java
    public static final DeferredRegister<ResearchEffectSerializer<?>> RESEARCH_EFFECT_SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER, YourModClass.MODID);

    public static final RegistryObject<ResearchEffectSerializer<?>> YOUR_EFFECT_SERIALIZER = RESEARCH_EFFECT_SERIALIZERS.register("your_effect", () -> YourResearchEffect.Serializer.INSTANCE);
    ```

## `ResearchMethod` Interface

A `ResearchMethod` defines how a research is performed. It's the "how" of the research process, whether it's consuming items, spending time, or any other custom logic.

To create a research method, you need to implement the `ResearchMethod` interface.

```java
public interface ResearchMethod {
    boolean canResearch(Player player, ResourceKey<Research> research);
    void onResearchStart(Player player, ResourceKey<Research> research);
    ResearchMethodProgress getDefaultProgress();
    ResourceLocation id();
    ResearchMethodSerializer<?> getSerializer();
    // ... other methods
}
```

### Key Methods

*   `boolean canResearch(Player player, ResourceKey<Research> research)`: This method is a check to see if a research can be started. It should **not** consume any items or resources. It's used by the UI to determine if the "start research" button should be enabled.

*   `void onResearchStart(Player player, ResourceKey<Research> research)`: This method is called when the player starts the research. This is where you should consume items, take resources, or perform any other initial action.

*   `ResearchMethodProgress getDefaultProgress()`: This method should return a `ResearchMethodProgress` object that represents the initial state of your research method. This is crucial for the progress tracking system. For simple one-off methods, you can return `ResearchMethodProgress.one(this)`. For methods with a specific progress goal, use `ResearchMethodProgress.empty(this, maxProgress)`.

### Serialization and Registration

Similar to `ResearchEffect`, your `ResearchMethod` needs a serializer.

1.  **Create the Serializer**: Create a class that implements `ResearchMethodSerializer<YourResearchMethod>`. Here is the example from `ConsumeItemResearchMethod`:

    ```java
    public static final class Serializer implements ResearchMethodSerializer<ConsumeItemResearchMethod> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<ConsumeItemResearchMethod> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("item").forGetter(ConsumeItemResearchMethod::toConsume),
                Codec.INT.fieldOf("count").forGetter(ConsumeItemResearchMethod::count)
        ).apply(instance, ConsumeItemResearchMethod::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeItemResearchMethod> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                ConsumeItemResearchMethod::toConsume,
                ByteBufCodecs.INT,
                ConsumeItemResearchMethod::count,
                ConsumeItemResearchMethod::new
        );
        // ...
    }
    ```

2.  **Register the Serializer**: In your main mod class, use a `DeferredRegister` to register your serializer.

    ```java
    public static final DeferredRegister<ResearchMethodSerializer<?>> RESEARCH_METHOD_SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER, YourModClass.MODID);

    public static final RegistryObject<ResearchMethodSerializer<?>> YOUR_METHOD_SERIALIZER = RESEARCH_METHOD_SERIALIZERS.register("your_method", () -> YourResearchMethod.Serializer.INSTANCE);
    ```

## Creating a Custom `ResearchMethod`

Let's walk through the `ConsumeItemResearchMethod` as an example of how to create a custom research method.

### 1. The Class Definition

`ConsumeItemResearchMethod` is a record that holds the `Ingredient` to consume and the `count`.

```java
public record ConsumeItemResearchMethod(Ingredient toConsume, int count) implements ResearchMethod {
    // ...
}
```

### 2. `canResearch`

This method checks if the player has the required items in their inventory. It does not remove the items.

```java
@Override
public boolean canResearch(Player player, ResourceKey<Research> research) {
    int amount = 0;
    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
        ItemStack item = player.getInventory().getItem(i);
        if (this.toConsume.test(item)) {
            amount += item.getCount();
            if (amount >= this.count) {
                return true;
            }
        }
    }
    return false;
}
```

### 3. `onResearchStart`

This method is called when the research begins. It removes the required items from the player's inventory.

```java
@Override
public void onResearchStart(Player player, ResourceKey<Research> research) {
    Inventory inventory = player.getInventory();
    for (int i = 0; i < inventory.getContainerSize(); i++) {
        ItemStack item = inventory.getItem(i);
        if (this.toConsume.test(item)) {
            int toRemove = Math.min(this.count, item.getMaxStackSize());
            inventory.removeItem(i, toRemove);
        }
    }
}
```

### 4. `getDefaultProgress`

This method sets up the initial progress state. The `maxProgress` is set to the number of items that need to be consumed.

```java
@Override
public ResearchMethodProgress getDefaultProgress() {
    return ResearchMethodProgress.empty(this, this.count);
}
```

## Understanding and Updating Progress

`ResearchMethod`s do not update their own progress directly. The progress is managed by a central system involving `ResearchTeam` and `TeamResearchProgress`.

`TeamResearchProgress` holds the progress for all researches for a team. You can get the progress for a specific research method and update it.

Here's a conceptual example of how you might update the progress for your custom research method from your own block entity or event handler:

```java
// Get the ResearchTeamMap for the current level
ResearchTeamMap teamMap = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);

// Get the team for the player
ResearchTeam team = teamMap.getTeamByPlayer(player);

if (team != null) {
    // Get the team's research progress
    TeamResearchProgress teamProgress = team.getResearchProgress();

    // Get all valid, non-completed research methods of your custom type
    List<ResearchMethodProgress> validMethods = teamProgress.getAllValidMethodProgress(YourCustomResearchMethod.class);

    if (validMethods != null) {
        for (ResearchMethodProgress progress : validMethods) {
            // Your logic to update the progress
            float currentProgress = progress.getProgress();
            progress.setProgress(currentProgress + 1); // Increment progress

            // The mod will handle checking if the progress is complete and finishing the research.
        }
    }
}
```

By using `teamProgress.getAllValidMethodProgress(YourCustomResearchMethod.class)`, you can get a list of all active research methods of your custom type and update their progress as needed.
