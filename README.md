<p align="center"><img src="./assets/banner.png"></p>

# Researchd
[![](https://cf.way2muchnoise.eu/short_researchd_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/researchd)
[![](http://cf.way2muchnoise.eu/versions/Available%20for_researchd_full.svg)](https://www.curseforge.com/minecraft/mc-mods/researchd/files)

**This mod implements the Factorio's research system into Minecraft**

**The mod supports both KubeJS and Datapack customization, allowing
pack developers to add their own Researches and Research Packs**

![Research Screen](./assets/research_screen.png)

**The KubeJS code for the root research**
```javascript
ResearchdEvents.registerResearches(event => {
    event.create('rd_example_js:wood')
        .icon('minecraft:oak_log')
        .method(ResearchMethodHelper.and(
            ResearchMethodHelper.consumeItem('minecraft:wheat_seeds', 1),
            ResearchMethodHelper.consumeItem('minecraft:dirt', 8)
        ))
        .effect(ResearchEffectHelper.unlockRecipe('minecraft:oak_planks'));
}
```

Without holding a wrench:

<img src="./assets/without_wrench.png">

When holding a wrench:

<img src="./assets/with_wrench.png">

## Supported Versions

| NeoForge |        |
|----------|--------|
|          | 1.20.1 |
| Forge    |        |
|          | 1.20.1 |

1.21 coming soon!

## Pre-Supported Mods
* Computer Craft
* Create
* Cyclic
* Elemental Craft
* Embers
* GregTechCEu Modern
* LaserIO
* Mekanism
* Mekanism Extras
* Pipez
* Pneumatic Craft Repressurized
* Powah
* Pretty Pipes
* Pretty Pipes Fluids
* Thermal Series

## Dependencies
None! Cable Facades is a standalone mod

## Mod Authors
Tags are also available for blocks, just add the tag: `#cable_facades:supports_facade`

## Discord
[![](https://dcbadge.vercel.app/api/server/m4EHeRjfZ9)](https://discord.gg/m4EHeRjfZ9)

Share your configs in our [Discord](https://discord.gg/m4EHeRjfZ9) so that we can expand support !