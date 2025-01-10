package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.researchd.api.research.serializers.ResearchPredicateSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ResearchPredicate {
    void onUnlock(Level level, Player player, ResourceKey<Research> research);

    ResearchPredicateSerializer<?> getSerializer();
}
