package com.portingdeadmods.researchd.api.team;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import net.minecraft.resources.ResourceKey;

public interface ResearchQueue {
    boolean add(ResearchInstance research);

    boolean remove(ResourceKey<Research> research, boolean removeChild);

    boolean remove(int index, boolean removeChildren);

    boolean contains(ResourceKey<Research> research);

    ResourceKey<Research> get(int index);

    boolean isEmpty();

    int size();

    ResourceKey<Research> getFirst();

}
