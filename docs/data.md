# Data Structure

This page shows an example of the JSON structure for a research file.
This is an in-game example that can be created with the command `/researchd example datapack`.

This example is for a research that unlocks oak planks after the player consumes 8 dirt and 1 wheat seeds.

```json
{
  "type": "researchd:simple",
  "parents": [],
  "requires_parent": false,
  "icon": [
    {
      "id": "minecraft:oak_log",
      "count": 1
    }
  ],
  "method": {
    "methods": [
      {
        "item": {
          "item": "minecraft:dirt"
        },
        "count": 8,
        "type": "researchd:consume_item"
      },
      {
        "item": {
          "item": "minecraft:wheat_seeds"
        },
        "count": 1,
        "type": "researchd:consume_item"
      }
    ],
    "type": "researchd:and"
  },
  "effect": {
    "recipes": [
      "minecraft:oak_planks"
    ],
    "type": "researchd:unlock_recipe"
  }
}
```

## Field Explanations

### `type`

-   **Description**: The type of the research. For now, this should always be `researchd:simple`.
-   **Type**: `String`

### `parents`

-   **Description**: A list of parent researches that must be completed before this research can be started (unless `requires_parent` is `false`).
-   **Type**: `Array` of `String` (Resource Locations)
-   **Example**: `"parents": ["researchd:cobblestone"]`

### `requires_parent`

-   **Description**: If `true`, all parent researches must be completed before this research is unlocked. If `false`, this research is unlocked at the start.
-   **Type**: `Boolean`

### `icon`

-   **Description**: The icon to be displayed for this research. It is a list of `ItemStack` objects, which will be cycled through in the UI.
-   **Type**: `Array` of `Object`
-   **Object Structure**:
    -   `id`: The item's resource location.
    -   `count`: The item's stack size.

### `method`

-   **Description**: The method required to complete the research. This is an object that can be one of several types.
-   **Type**: `Object`

#### Method Types

-   **`researchd:consume_item`**: Requires the player to have a certain amount of an item in their inventory, which will be consumed.
    -   `item`: An `Ingredient` object representing the item to consume.
    -   `count`: The number of items to consume.
-   **`researchd:consume_pack`**: Requires the player to use a research pack in the Research Lab.
    -   `packs`: A list of research pack resource locations.
    -   `count`: The number of packs to consume.
    -   `duration`: The time in ticks it takes to consume one pack.
-   **`researchd:and`**: A list of methods that all need to be completed.
    -   `methods`: An array of `method` objects.
-   **`researchd:or`**: A list of methods where only one needs to be completed.
    -   `methods`: An array of `method` objects.

### `effect`

-   **Description**: The effect that occurs when the research is completed.
-   **Type**: `Object`

#### Effect Types

-   **`researchd:unlock_recipe`**: Unlocks one or more recipes.
    -   `recipes`: An array of recipe resource locations.
-   **`researchd:dimension_unlock`**: Unlocks a dimension for the player.
    -   `dimension`: The resource location of the dimension to unlock.
    -   `icon_sprite` (optional): The resource location of the icon to display for this effect.
-   **`researchd:and`**: A list of effects that will all be triggered.
    -   `effects`: An array of `effect` objects.
-   **`researchd:empty`**: No effect.
