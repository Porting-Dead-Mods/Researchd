package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// The data run is a client data run, which generates client assets and server data together.
@EventBusSubscriber(modid = Researchd.MODID, value = Dist.CLIENT)
public class DataGatherer {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new EnUsLangProvider(output));
        generator.addProvider(true, new ModelsProvider(output));
        generator.addProvider(true, new RecipesProvider.Runner(output, lookupProvider));
        TagsProvider.createTagProviders(generator, output, lookupProvider);
        generator.addProvider(
                true,
                new LootTableProvider(
                        output,
                        Collections.emptySet(),
                        List.of(new LootTableProvider.SubProviderEntry(
                                BlockLootProvider::new, LootContextParamSets.BLOCK)),
                        lookupProvider));
    }
}
