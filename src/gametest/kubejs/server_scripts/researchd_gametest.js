// Declares the Research Pack and researches that KubeJSTests look for. It calls Researchd's KubeJS bindings the way
// the PlanetaryFactory pack does, JS arrays into varargs included. The gameTestServer run copies it into its
// kubejs/server_scripts before it starts.

ResearchdEvents.registerResearchPacks(event => {
  event.create('researchd_kjs_test:pack')
    .literalName('KubeJS Test Pack')
    .color(200, 60, 60)
    .sortingValue(100);
});

ResearchdEvents.registerResearches(event => {
  event.create('researchd_kjs_test:root')
    .icon('minecraft:book')
    .literalName('KubeJS Root')
    .method(ResearchMethodHelper.checkItemPresence('minecraft:dirt', 1))
    .effect(ResearchEffectHelper.unlockRecipes(['minecraft:gold_block']));

  event.create('researchd_kjs_test:child')
    .icon('minecraft:iron_ingot')
    .literalName('KubeJS Child')
    .parents(['researchd_kjs_test:root'])
    .method(ResearchMethodHelper.consumePack('researchd_kjs_test:pack', 10, 20))
    .effect(ResearchEffectHelper.and([
      ResearchEffectHelper.unlockRecipes(['minecraft:iron_block', 'minecraft:iron_ingot_from_iron_block']),
      ResearchEffectHelper.unlockDimensions(['minecraft:the_nether'])
    ]));
});
