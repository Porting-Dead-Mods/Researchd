# KubeJS Data Generation

Researchd provides full support for KubeJS for both event scripting and data generation.

## Creating Researches and Packs

You can create new researches and research packs using the `ResearchdEvents` group in your KubeJS startup scripts.

### Creating a Research Pack

Use the `ResearchdEvents.registerResearchPacks` event to create new packs.

```javascript
ResearchdEvents.registerResearchPacks(event => {
    event.create("kubejs:my_custom_pack")
        .sortingValue(100)
        .color(123, 45, 67);
    
    event.create("kubejs:another_pack")
        .sortingValue(101)
        .colorRGB(12, 34, 56);
});
```

#### ResearchPackBuilder Methods

| Method             | Description                                                                              |
| ------------------ | ---------------------------------------------------------------------------------------- |
| `color(color)`     | Sets the color of the pack. Can be a hex integer or a `KubeColor`.                       |
| `colorRGB(r, g, b)`| Sets the color of the pack using RGB values.                                               |
| `customTexture(texture)` | Sets a custom texture for the pack. The texture should be a `ResourceLocation` string. |
| `sortingValue(value)` | A value to dictate where in the progression the research pack should be. Lower = earlier, higher = later. |

### Creating a Research

Use the `ResearchdEvents.registerResearches` event to create new researches.

```javascript
ResearchdEvents.registerResearches(event => {
    event.create("kubejs:my_custom_research")
        .icon("minecraft:diamond")
        .parents("researchd:wood") // You can reference existing researches here
        .method(ResearchMethodHelper.consumeItem("minecraft:dirt", 16))
        .effect(ResearchEffectHelper.unlockRecipe("minecraft:diamond_block"));

    event.create("kubejs:another_research")
        .icon("minecraft:stone_bricks")
        .parent("researchd:cobblestone")
        .consumePack("kubejs:my_custom_pack", 5, 10)
        .effect(ResearchEffectHelper.unlockRecipe("minecraft:stone_bricks"))
        .literalName('Example Research')
        .literalDescription('This is an example research created with KubeJS.');
});
```

#### ResearchBuilder Methods

| Method                      | Description                                                                  |
| --------------------------- | ---------------------------------------------------------------------------- |
| `icon(itemId)`              | Sets the icon for the research.                                              |
| `method(researchMethod)`    | Sets the research method. Use `ResearchMethodHelper` to create methods.      |
| `consumeItem(itemId, count)`| Shortcut for `.method(ResearchMethodHelper.consumeItem(itemId, count))`.     |
| `consumePack(packId, ...args)` | Shortcut for `.method(ResearchMethodHelper.consumePack(packId, ...args))`. |
| `requireAllMethods(...methods)` | Shortcut for `.method(ResearchMethodHelper.and(...methods))`.            |
| `requireAnyMethod(...methods)`  | Shortcut for `.method(ResearchMethodHelper.or(...methods))`.             |
| `effect(researchEffect)`    | Sets the research effect. Use `ResearchEffectHelper` to create effects.      |
| `unlockRecipe(recipeId)`    | Shortcut for `.effect(ResearchEffectHelper.unlockRecipe(recipeId))`.         |
| `unlockMultipleRecipes(...recipeIds)` | Shortcut for `.effect(ResearchEffectHelper.unlockRecipes(...recipeIds))`. |
| `unlockDimension(dimensionId, ...args)` | Shortcut for `.effect(ResearchEffectHelper.unlockDimension(dimensionId, ...args))`. |
| `combineEffects(...effects)`| Shortcut for `.effect(ResearchEffectHelper.and(...effects))`.                |
| `parent(researchId)`        | Adds a parent research.                                                      |
| `parents(...researchIds)`   | Adds multiple parent researches.                                             |
| `noParentRequired()`        | Sets that this research does not require its parents to be completed.        |
| `literalName(name)`         | Sets a literal name for the research, ignoring translations.                 |
| `literalDescription(desc)`  | Sets a literal description for the research, ignoring translations.          |

### Helpers

#### ResearchMethodHelper

| Method                               | Description                                                               |
| ------------------------------------ | ------------------------------------------------------------------------- |
| `consumeItem(itemId, count)`         | Creates a method that requires consuming a certain amount of an item.     |
| `consumePack(packId, count)`         | Creates a method that requires consuming a certain amount of a research pack. |
| `consumePack(packId, count, duration)` | Creates a method that requires consuming a certain amount of a research pack with a specific duration. |
| `and(...methods)`                    | Creates a method that requires all of the specified methods to be completed. |
| `or(...methods)`                     | Creates a method that requires any of the specified methods to be completed. |

#### ResearchEffectHelper

| Method                         | Description                                                              |
| ------------------------------ | ------------------------------------------------------------------------ |
| `empty()`                      | Creates an empty effect that does nothing.                               |
| `unlockRecipe(recipeId)`       | Creates an effect that unlocks a single recipe.                          |
| `unlockRecipes(...recipeIds)`  | Creates an effect that unlocks multiple recipes.                         |
| `unlockDimension(dimensionId)` | Creates an effect that unlocks a dimension.                              |
| `unlockNether()`               | Creates an effect that unlocks the Nether.                               |
| `unlockEnd()`                  | Creates an effect that unlocks the End.                                  |
| `and(...effects)`              | Combines multiple effects into one, executing all of them.               |
| `combine(effects)`             | Combines a list of effects into one, executing all of them.              |

## Event Scripting

You can listen for the following events in your KubeJS scripts:

### Research Completed

This event is fired when a player completes a research.

```javascript
// Example event listener
ResearchdEvents.researchCompleted(event => {
    console.log(`Player ${event.player.username} completed research ${event.researchId}`);
});
```

### Research Progress

This event is fired when a player makes progress on a research.

```javascript
// Example event listener
ResearchdEvents.researchProgress(event => {
    console.log(`Player ${event.player.username} has ${event.progressPercent}% progress on research ${event.researchId}`);
});
```