package com.portingdeadmods.researchd.events.common;

import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdBEPlacementHandler {
    @SubscribeEvent
    private static void entityPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (entity instanceof Player player) {
            BlockEntity be = level.getBlockEntity(event.getPos());
            if (be == null) return;

            ResearchTeamManager mgr = ResearchdApi.getTeamManager(level);
            ResearchTeam team = mgr != null ? mgr.getTeamByPlayer(player) : null;
            UUID teamId = team != null ? team.getId() : PlayerUtils.EmptyUUID;
            be.setData(ResearchdAttachments.PLACED_BY_UUID, teamId);
        }
    }
}
