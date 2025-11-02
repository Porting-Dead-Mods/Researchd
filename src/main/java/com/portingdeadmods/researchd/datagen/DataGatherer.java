package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Researchd.MODID)
public class DataGatherer {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new EnUsLangProvider(output));
        generator.addProvider(event.includeClient(), new BlockModelProvider(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new RecipesProvider(output, lookupProvider));
        TagsProvider.createTagProviders(generator, output, lookupProvider, event.getExistingFileHelper(), event.includeServer());
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootProvider::new, LootContextParamSets.BLOCK)
        ), lookupProvider));

    }

}
