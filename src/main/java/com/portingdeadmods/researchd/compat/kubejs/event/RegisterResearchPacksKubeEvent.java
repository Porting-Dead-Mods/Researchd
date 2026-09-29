package com.portingdeadmods.researchd.compat.kubejs.event;

import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.compat.kubejs.builders.ResearchPackBuilder;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

public class RegisterResearchPacksKubeEvent implements KubeEvent {
    private final Map<Identifier, ResearchPackImpl> researchPacks = new HashMap<>();
    private final List<ResearchPackBuilder> builders = new ArrayList<>();

    public ResearchPackBuilder create(String id) {
        Identifier location = Identifier.parse(id);
        ResearchPackBuilder builder = new ResearchPackBuilder(location);
        builder.sourceLine = SourceLine.UNKNOWN;
        builders.add(builder);
        return builder;
    }

    public ItemStack createItem(String packId) {
        Identifier location = Identifier.parse(packId);
        ResourceKey<ResearchPack> key =
                ResourceKey.create(com.portingdeadmods.researchd.ResearchdRegistries.RESEARCH_PACK_KEY, location);
        return ResearchPackImpl.asStack(key);
    }

    public Map<Identifier, ResearchPackImpl> getResearchPacks() {
        for (ResearchPackBuilder builder : builders) {
            try {
                ResearchPackImpl pack = builder.createObject();
                researchPacks.put(builder.id, pack);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create researchPack pack " + builder.id, e);
            }
        }
        return researchPacks;
    }
}
