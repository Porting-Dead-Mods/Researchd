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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class ResearchdTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Researchd.MODID);

    static {
        TABS.register("main", () -> CreativeModeTab.builder()
                .icon(() -> {
                    ItemStack pack = ResearchdItems.RESEARCH_PACK.toStack();
                    pack.set(ResearchdDataComponents.RESEARCH_PACK, new ResearchPackComponent(Optional.of(ResearchdResearchPacks.OVERWORLD)));
                    return pack;
                })
                .title(Component.literal(Researchd.MODID))
                .displayItems((params, output) -> {
                    output.accept(ResearchdItems.RESEARCH_LAB.toStack());
                    Optional<HolderLookup.RegistryLookup<SimpleResearchPack>> lookup = params.holders().lookup(ResearchdRegistries.RESEARCH_PACK_KEY);
                    if (lookup.isPresent()) {
                        List<ResourceKey<SimpleResearchPack>> resourceKeyStream = lookup.get().listElementIds().toList();
                        List<Holder.Reference<SimpleResearchPack>> holders = new ArrayList<>(resourceKeyStream.stream().map(p -> lookup.get().get(p)).filter(Optional::isPresent).map(Optional::get).toList());
                        holders.sort(Comparator.comparingInt(a -> a.value().sorting_value()));

                        holders.forEach(researchPack -> {
                            addResearchPack(output, params.holders(), researchPack);
                        });
                    }
                })
                .build());
    }

    private static void addResearchPack(CreativeModeTab.Output output, HolderLookup.Provider lookup, Holder.Reference<SimpleResearchPack> elem) {
        ItemStack stack = ResearchdItems.RESEARCH_PACK.toStack();

        stack.set(ResearchdDataComponents.RESEARCH_PACK.get(), new ResearchPackComponent(Optional.of(elem.key())));
        output.accept(stack);
    }
}
