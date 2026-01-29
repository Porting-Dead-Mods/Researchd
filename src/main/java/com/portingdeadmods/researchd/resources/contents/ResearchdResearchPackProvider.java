package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.resources.ResearchdDatagenProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface ResearchdResearchPackProvider extends ResearchdDatagenProvider<ResearchPack> {
    default ResourceKey<ResearchPack> researchPack(String name, UnaryOperator<ResearchPackImpl.Builder> builder) {
        ResourceKey<ResearchPack> key = packKey(name);
        this.contents().put(key, builder.apply(ResearchPackImpl.builder()).build());
        return key;
    }

    default ResourceKey<ResearchPack> packKey(String name) {
        return ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.fromNamespaceAndPath(this.modid(), name));
    }

    static ItemStack asStack(ResourceKey<ResearchPack> key, int count) {
        ItemStack pack = ResearchdItems.RESEARCH_PACK.toStack();
        pack.set(ResearchdDataComponents.RESEARCH_PACK.get(), new ResearchPackComponent(Optional.of(key)));
        pack.setCount(count);
        return pack;
    }

    static ItemStack asStack(ResourceKey<ResearchPack> key) {
        return asStack(key, 1);
    }

    static ItemStack asStack(ResourceLocation key) {
        return asStack(ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, key), 1);
    }
}
