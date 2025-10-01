package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.networking.cache.AskServerPlayers;
import com.portingdeadmods.researchd.networking.cache.ClearGraphCachePayload;
import com.portingdeadmods.researchd.networking.cache.ReceiveServerPlayers;
import com.portingdeadmods.researchd.networking.research.ResearchFinishedPayload;
import com.portingdeadmods.researchd.networking.research.ResearchMethodProgressSyncPayload;
import com.portingdeadmods.researchd.networking.research.ResearchQueueAddPayload;
import com.portingdeadmods.researchd.networking.research.ResearchQueueRemovePayload;
import com.portingdeadmods.researchd.networking.team.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Researchd.MODID)
public class NetworkEvents {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Researchd.MODID);
        registrar.playToServer(
                EnterTeamPayload.TYPE,
                EnterTeamPayload.STREAM_CODEC,
                EnterTeamPayload::enterTeamAction
        );
        registrar.playToServer(
                LeaveTeamPayload.TYPE,
                LeaveTeamPayload.STREAM_CODEC,
                LeaveTeamPayload::leaveTeamAction
        );

        registrar.playToServer(
                ManageMemberPayload.TYPE,
                ManageMemberPayload.STREAM_CODEC,
                ManageMemberPayload::manageMemberAction
        );
        registrar.playToServer(
                ManageModeratorPayload.TYPE,
                ManageModeratorPayload.STREAM_CODEC,
                ManageModeratorPayload::manageModeratorAction
        );
        registrar.playToServer(
                TransferOwnershipPayload.TYPE,
                TransferOwnershipPayload.STREAM_CODEC,
                TransferOwnershipPayload::transferOwnershipAction
        );

        registrar.playToServer(
                TeamSetNamePayload.TYPE,
                TeamSetNamePayload.STREAM_CODEC,
                TeamSetNamePayload::setNameAction
        );

        registrar.playToServer(
                InvitePlayerPayload.TYPE,
                InvitePlayerPayload.STREAM_CODEC,
                InvitePlayerPayload::invitePlayerAction
        );
        registrar.playToServer(
                RequestToJoinPayload.TYPE,
                RequestToJoinPayload.STREAM_CODEC,
                RequestToJoinPayload::requestToJoinAction
        );

        registrar.playToServer(
                ResearchQueueAddPayload.TYPE,
                ResearchQueueAddPayload.STREAM_CODEC,
                ResearchQueueAddPayload::handle
        );
        registrar.playToServer(
                ResearchQueueRemovePayload.TYPE,
                ResearchQueueRemovePayload.STREAM_CODEC,
                ResearchQueueRemovePayload::researchQueueRemoveAction
        );
        registrar.playToServer(
                AskServerPlayers.TYPE,
                AskServerPlayers.STREAM_CODEC,
                AskServerPlayers::ping
        );

        registrar.playToClient(
                ClearGraphCachePayload.TYPE,
                ClearGraphCachePayload.STREAM_CODEC,
                ClearGraphCachePayload::clearCache
        );

        registrar.playToClient(
                ResearchFinishedPayload.TYPE,
                ResearchFinishedPayload.STREAM_CODEC,
                ResearchFinishedPayload::researchFinishedAction
        );
        registrar.playToClient(
                RefreshResearchesPayload.TYPE,
                RefreshResearchesPayload.STREAM_CODEC,
                RefreshResearchesPayload::refreshResearchesAction
        );
        registrar.playToClient(
                RefreshPlayerManagementPayload.TYPE,
                RefreshPlayerManagementPayload.STREAM_CODEC,
                RefreshPlayerManagementPayload::refreshPlayerManagementAction
        );
        registrar.playToClient(
                ResearchMethodProgressSyncPayload.TYPE,
                ResearchMethodProgressSyncPayload.STREAM_CODEC,
                ResearchMethodProgressSyncPayload::researchMethodProgressSyncAction
        );
        registrar.playToClient(
                ReceiveServerPlayers.TYPE,
                ReceiveServerPlayers.STREAM_CODEC,
                ReceiveServerPlayers::pong
        );
    }
}
