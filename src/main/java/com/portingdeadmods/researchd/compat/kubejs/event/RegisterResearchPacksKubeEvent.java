package com.portingdeadmods.researchd.compat.kubejs.event;

import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.compat.kubejs.ResearchPackBuilder;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RegisterResearchPacksKubeEvent implements KubeEvent {
    private final Map<ResourceLocation, ResearchPack> researchPacks = new HashMap<>();
    private final List<ResearchPackBuilder> builders = new ArrayList<>();

    public ResearchPackBuilder create(String id) {
        ResourceLocation location = ResourceLocation.parse(id);
        ResearchPackBuilder builder = new ResearchPackBuilder(location);
        builder.sourceLine = SourceLine.UNKNOWN;
        builders.add(builder);
        return builder;
    }

    public ItemStack createItem(String packId) {
        ResourceLocation location = ResourceLocation.parse(packId);
        ResourceKey<ResearchPack> key = ResourceKey.create(com.portingdeadmods.researchd.ResearchdRegistries.RESEARCH_PACK_KEY, location);
        return ResearchPack.asStack(key);
    }

    public Map<ResourceLocation, ResearchPack> getResearchPacks() {
        for (ResearchPackBuilder builder : builders) {
            try {
                ResearchPack pack = builder.createObject();
                researchPacks.put(builder.id, pack);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create research pack " + builder.id, e);
            }
        }
        return researchPacks;
    }
}
