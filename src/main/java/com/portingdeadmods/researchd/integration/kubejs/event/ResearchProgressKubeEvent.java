package com.portingdeadmods.researchd.integration.kubejs.event;

import com.portingdeadmods.researchd.api.research.Research;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

public class ResearchProgressKubeEvent implements KubePlayerEvent {
    private final ServerPlayer player;
    private final ResourceKey<Research> research;
    private final double progress;
    
    public ResearchProgressKubeEvent(ServerPlayer player, ResourceKey<Research> research, double progress) {
        this.player = player;
        this.research = research;
        this.progress = progress;
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
    
    public double getProgress() {
        return progress;
    }
    
    public int getProgressPercent() {
        return (int) (progress * 100);
    }
}