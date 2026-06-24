# Compat

## Immersive Engineering

All crafting is filtered. Multiblock formation can additionally be gated behind a research.

### RdImmersiveEngineering

A KubeJS binding for building Immersive Engineering specific research effects.

| Method                          | Description                                                                 |
| ------------------------------- | --------------------------------------------------------------------------- |
| `unlockMultiblock(multiblock)`  | Creates an effect that gates the formation of an IE multiblock. `multiblock` is the multiblock's id string (e.g. `"immersiveengineering:crusher"`). |

```javascript
ResearchdEvents.registerResearches(event => {
    event.create("kubejs:ie_crusher")
        .icon("immersiveengineering:crusher")
        .consumeItem("immersiveengineering:steel_ingot", 16)
        .effect(RdImmersiveEngineering.unlockMultiblock("immersiveengineering:crusher"));
});
```

## Create

All crafting is filtered.
