package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.resources.RegistryManagersGetter;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperClient;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class ResearchdTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Researchd.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .icon(ResearchdItems.RESEARCH_LAB::toStack)
            .title(Component.literal(Researchd.MODID))
            .displayItems((params, output) -> {
                output.accept(ResearchdItems.RESEARCH_LAB.toStack());
                Optional<HolderLookup.RegistryLookup<ResearchPack>> lookup = params.holders().lookup(ResearchdRegistries.RESEARCH_PACK_KEY);
                Collection<ResourceKey<ResearchPack>> packs;
                if (!FMLEnvironment.dist.isClient()) {
                    MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
                    if (currentServer != null) {
                        packs = ((RegistryManagersGetter) currentServer.getServerResources().managers()).researchd$getResearchPackManager().getLookup().keySet();
                    } else
                        packs = lookup.map(researchPackRegistryLookup -> researchPackRegistryLookup.listElements().map(Holder.Reference::key).toList())
                                .orElse(Collections.emptyList());
                } else {
                    packs = ResearchHelperClient.getResearchPacks().keySet();
                }
                for (ResourceKey<ResearchPack> pack : packs) {
                    addResearchPack(output, pack);
                }
            })
            .build());

    private static void addResearchPack(CreativeModeTab.Output output, ResourceKey<ResearchPack> elem) {
        ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();

        stack.set(ResearchdDataComponents.RESEARCH_PACK.get(), new ResearchPackComponent(Optional.of(elem)));
        output.accept(stack);
    }
}
