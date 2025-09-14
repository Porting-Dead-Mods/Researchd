# KubeJS Data Generation

Researchd provides full support for KubeJS for both event scripting and data generation.

## Creating Researches and Packs

You can create new researches and research packs using the `ServerEvents.registry` event in your KubeJS startup scripts. The KubeJS builders are wrappers around the Java builders. For a full list of available methods, see `com.portingdeadmods.researchd.integration.kubejs.ResearchBuilder`.

### Creating a Research Pack

Use the `researchd:research_pack` registry to create new packs.

```javascript
ServerEvents.registry("researchd:research_pack", event => {
    event.create("kubejs:my_custom_pack")
        .sortingValue(100)
        .color(123, 45, 67);
});
```

### Creating a Research

Use the `researchd:research` registry to create new researches. You can use the ` prefix to access Java classes like `ConsumeItemResearchMethod` and `Ingredient` directly in your script.

```javascript
ServerEvents.registry("researchd:research", event => {
    event.create("kubejs:my_custom_research")
        .icon("minecraft:diamond")
        .parents("researchd:wood") // You can reference existing researches here
        .method(new $ConsumeItemResearchMethod($Ingredient.of("minecraft:dirt"), 16))
        .effect(new $RecipeUnlockEffect("minecraft:diamond_block"));
});
```

## Event Scripting

You can listen for the following events in your KubeJS scripts:

### Research Completed

This event is fired when a player completes a research.

```javascript
// Example event listener
ResearchdEvents.RESEARCH_COMPLETED.listen(event => {
    console.log(`Player ${event.player.username} completed research ${event.research}`);
});
```

### Research Progress

This event is fired when a player makes progress on a research.

```javascript
// Example event listener
ResearchdEvents.RESEARCH_PROGRESS.listen(event => {
    console.log(`Player ${event.player.username} has ${event.progress}% progress on research ${event.research}`);
});
```

The event handling is set up in `com.portingdeadmods.researchd.integration.KubeJSIntegration`.
