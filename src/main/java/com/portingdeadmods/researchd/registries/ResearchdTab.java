package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.stream.Stream;

public final class ResearchdTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Researchd.MODID);

    static {
        TABS.register("main", () -> CreativeModeTab.builder()
                .icon(Items.DIAMOND::getDefaultInstance)
                .title(Component.literal(Researchd.MODID))
                .displayItems((params, output) -> {
                    Optional<HolderLookup.RegistryLookup<SimpleResearchPack>> lookup = params.holders().lookup(ResearchdRegistries.RESEARCH_PACK_KEY);
                    if (lookup.isPresent()) {
                        Stream<ResourceKey<SimpleResearchPack>> resourceKeyStream = lookup.get().listElementIds();
                        resourceKeyStream.forEach(researchPack -> {
                            addResearchPack(output, params.holders(), researchPack);
                        });
                    }
                })
                .build());
    }

    private static void addResearchPack(CreativeModeTab.Output output, HolderLookup.Provider lookup, ResourceKey<SimpleResearchPack> elem) {
        ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();

        Optional<Holder.Reference<SimpleResearchPack>> holder = lookup.holder(elem);
        stack.set(ResearchdDataComponents.RESEARCH_PACK.get(), new ResearchPackComponent(holder.map(Holder::getKey)));
        output.accept(stack);
    }
}
