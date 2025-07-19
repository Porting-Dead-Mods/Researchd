package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.PDLClientSavedData;
import com.portingdeadmods.researchd.commands.ResearchdCommands;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

@EventBusSubscriber(modid = Researchd.MODID)
public final class ResearchdCommonEvents {
    @SubscribeEvent
    private static void entityPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (entity instanceof Player player) {
            UUID uuid = player.getUUID();
            if (level.getBlockEntity(event.getPos()) != null)
                level.getBlockEntity(event.getPos()).setData(ResearchdAttachments.PLACED_BY_UUID, uuid);
        }
    }

    @SubscribeEvent
    private static void onCommandRegister(RegisterCommandsEvent event) {
        ResearchdCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    private static void onLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        PDLClientSavedData.CLIENT_SAVED_DATA_CACHE.clear();
    }

    public static void onJoinLevel(EntityJoinLevelEvent entity) {

    }
//
//        private static List<ResearchInstance> getChildren(Level level, ResearchInstance instance) {
//            List<ResearchInstance> children = new ArrayList<>();
//            for (Holder<Research> levelResearch : ResearchHelper.getLevelResearches(level)) {
//                if (levelResearch.value().parents().contains(instance.getResearch())) {
//                    ResearchStatus status;
//                    if (instance.getResearchStatus() == ResearchStatus.RESEARCHED) {
//                        status = ResearchStatus.RESEARCHABLE;
//                    } else if (ResearchdSavedData.PLAYER_RESEARCH.get().getData(level).isCompleted(levelResearch.getKey())) {
//                        status = ResearchStatus.RESEARCHED;
//                    }else {
//                        status = ResearchStatus.LOCKED;
//                    }
//                    children.add(new ResearchInstance(levelResearch.getKey(), status));
//                }
//            }
//            return children;
//        }
}