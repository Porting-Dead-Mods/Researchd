# Addons

This page provides an in-depth guide on how to create addons for Researchd, including creating custom research effects, methods, and icons.

## `ResearchEffect` Interface

A `ResearchEffect` is triggered when a research is successfully completed. It's how you grant rewards to the player, such as unlocking recipes, giving items, or applying any other custom effect.

To create a research effect, you need to implement the `ResearchEffect` interface.

```java
public interface ResearchEffect {
    void onUnlock(Level level, Player player, ResourceKey<Research> research);
    ResourceLocation id();
    ResearchEffectSerializer<?> getSerializer();
}
```

### Key Methods

*   `void onUnlock(Level level, Player player, ResourceKey<Research> research)`: This is the core method of the interface. It's called on the server side when the specified `research` is completed. You can use the `player` and `level` objects to apply your effects.

*   `ResearchEffectSerializer<?> getSerializer()`: Every research effect needs a serializer to handle networking and saving to disk. You'll need to create a serializer for your custom effect and return it here.

### Serialization and Registration

To allow your `ResearchEffect` to be saved and synced, you need to create a `ResearchEffectSerializer` and register it.

1.  **Create the Serializer**: Create a class that implements `ResearchEffectSerializer<YourResearchEffect>`. You'll need to implement `codec()` and `streamCodec()`.

2.  **Register the Serializer**: In your main mod class, use a `DeferredRegister` to register your serializer instance.

    ```java
    public static final DeferredRegister<ResearchEffectSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_EFFECT_SERIALIZER_KEY, YourModClass.MODID);

    public static final Supplier<ResearchEffectSerializer<?>> YOUR_EFFECT_SERIALIZER = SERIALIZERS.register("your_effect", () -> YourResearchEffect.SERIALIZER);
    ```

## `ResearchMethod` Interface

A `ResearchMethod` defines how a research is performed. It's the "how" of the research process, whether it's consuming items, spending time, or any other custom logic.

To create a research method, you need to implement the `ResearchMethod` interface.

```java
public interface ResearchMethod {
    void checkProgress(Level level, ResourceKey<Research> research, ResearchProgress.Task task, MethodContext context);
    ResearchProgress createProgress();
    float getMaxProgress();
    ResourceLocation id();
    ResearchMethodSerializer<?> getSerializer();
}
```

### Key Methods

*   `void checkProgress(Level level, ResourceKey<Research> research, ResearchProgress.Task task, MethodContext context)`: This method is called periodically to check and update the progress of a research. You can add progress to the `task` object here.
*   `ResearchProgress createProgress()`: This method should return a `ResearchProgress` object that represents the initial state of your research method.
*   `float getMaxProgress()`: The maximum progress value for this research method.

### Serialization and Registration

Similar to `ResearchEffect`, your `ResearchMethod` needs a serializer.

1.  **Create the Serializer**: Create a class that implements `ResearchMethodSerializer<YourResearchMethod>`.

2.  **Register the Serializer**: In your main mod class, use a `DeferredRegister` to register your serializer.

    ```java
    public static final DeferredRegister<ResearchMethodSerializer<?>> SERIALIZERS = DeferredRegister.create(ResearchdRegistries.RESEARCH_METHOD_SERIALIZER, YourModClass.MODID);

    public static final Supplier<ResearchMethodSerializer<?>> YOUR_METHOD_SERIALIZER = SERIALIZERS.register("your_method", () -> YourResearchMethod.Serializer.INSTANCE);
    ```

## `ResearchIcon` Interface

The `ResearchIcon` is a utility interface containing the id as well as the object that renders for the icon. The actual rendering of the icon is handled on the client side through a `ClientResearchIcon` class.

To create a custom research icon, you need to implement the `ResearchIcon` interface, and a corresponding `ClientResearchIcon` for the rendering.

### `ValueEffect` Interface

The `ValueEffect` interface allows you to create custom numerical values that can be associated with a research team and modified by research effects. This is useful for creating upgradeable stats or other dynamic properties.

To create a value effect, you need to implement the `ValueEffect` interface and register it.

```java
public interface ValueEffect {
    default ResourceLocation getKey() {
        return ResearchdRegistries.VALUE_EFFECT.getKey(this);
    }

    default void onUnlock(ResearchTeam team, Level level) {
    }
}
```

Then, you can use the `IncreaseValueEffect`, `DecreaseValueEffect`, `MultiplyValueEffect`, and `DivideValueEffect` research effects to modify your custom value.