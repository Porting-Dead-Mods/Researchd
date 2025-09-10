package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Researchd.MODID)
public class DataGatherer {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new DatapackRegistryProvider(output, lookupProvider));
        generator.addProvider(event.includeClient(), new EnUsLangProvider(output));
        generator.addProvider(event.includeClient(), new BlockModelProvider(output, event.getExistingFileHelper()));
    }
}
