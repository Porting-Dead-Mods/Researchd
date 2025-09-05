package com.portingdeadmods.researchd.integration.kubejs.event;

import com.portingdeadmods.researchd.api.research.Research;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

public class ResearchCompletedKubeEvent implements KubePlayerEvent {
    private final ServerPlayer player;
    private final ResourceKey<Research> research;
    
    public ResearchCompletedKubeEvent(ServerPlayer player, ResourceKey<Research> research) {
        this.player = player;
        this.research = research;
    }
    
    @Override
    public ServerPlayer getEntity() {
        return player;
    }
    
    public ResourceKey<Research> getResearch() {
        return research;
    }
    
    public String getResearchId() {
        return research.location().toString();
    }
}