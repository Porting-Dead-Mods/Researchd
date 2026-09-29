package com.portingdeadmods.researchd.resources.contents;

import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.resources.ResearchdDatagenProvider;
import java.util.function.UnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;

public interface ResearchdResearchPackProvider extends ResearchdDatagenProvider<ResearchPack> {
    default ResourceKey<ResearchPack> researchPack(String name, UnaryOperator<ResearchPackImpl.Builder> builder) {
        ResourceKey<ResearchPack> key = packKey(name);
        this.contents().put(key, builder.apply(ResearchPackImpl.builder()).build());
        return key;
    }

    default ResourceKey<ResearchPack> packKey(String name) {
        return ResourceKey.create(
                ResearchdRegistries.RESEARCH_PACK_KEY, Identifier.fromNamespaceAndPath(this.modid(), name));
    }

    static ItemStackTemplate asTemplate(Identifier key) {
        return ResearchPackImpl.asTemplate(ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, key));
    }
}
