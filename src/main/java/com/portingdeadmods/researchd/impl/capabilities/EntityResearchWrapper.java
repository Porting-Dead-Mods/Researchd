package com.portingdeadmods.researchd.impl.capabilities;

import com.portingdeadmods.researchd.api.capabilties.EntityResearch;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.utils.researches.data.ResearchQueue;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntityResearchWrapper implements EntityResearch {
    private final Player player;

    public EntityResearchWrapper(Player player) {
        this.player = player;
    }

    @Override
    public ResearchQueue researchQueue() {
        return player.getData(ResearchdAttachments.ENTITY_RESEARCH).researchQueue();
    }

    @Override
    public Set<ResearchInstance> researches() {
        return player.getData(ResearchdAttachments.ENTITY_RESEARCH).researches();
    }

    @Override
    public void addResearch(ResearchInstance instance) {
        Set<ResearchInstance> set = new LinkedHashSet<>(researches());
        set.add(instance);
        player.setData(ResearchdAttachments.ENTITY_RESEARCH, new EntityResearchImpl(researchQueue(), set));
    }

    @Override
    public void removeResearch(ResearchInstance instance) {
        Set<ResearchInstance> set = new LinkedHashSet<>(researches());
        set.remove(instance);
        player.setData(ResearchdAttachments.ENTITY_RESEARCH, new EntityResearchImpl(researchQueue(), set));
    }
}
