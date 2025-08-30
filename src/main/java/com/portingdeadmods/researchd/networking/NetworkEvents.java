package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.pdl.data.PDLSavedData;
import com.portingdeadmods.researchd.api.pdl.data.SavedDataHolder;
import com.portingdeadmods.researchd.networking.research.ResearchCompleteProgressSyncPayload;
import com.portingdeadmods.researchd.networking.research.ResearchFinishedPayload;
import com.portingdeadmods.researchd.networking.research.ResearchQueueAddPayload;
import com.portingdeadmods.researchd.networking.research.ResearchQueueRemovePayload;
import com.portingdeadmods.researchd.networking.team.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Researchd.MODID, bus = EventBusSubscriber.Bus.MOD)
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
                ResearchQueueAddPayload::researchQueueAddAction
        );
        registrar.playToServer(
                ResearchQueueRemovePayload.TYPE,
                ResearchQueueRemovePayload.STREAM_CODEC,
                ResearchQueueRemovePayload::researchQueueRemoveAction
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
                ResearchCompleteProgressSyncPayload.TYPE,
                ResearchCompleteProgressSyncPayload.STREAM_CODEC,
                ResearchCompleteProgressSyncPayload::researchCompleteProgressSyncAction
        );

        for (PDLSavedData<?> savedData : ResearchdRegistries.SAVED_DATA) {
            SavedDataHolder<?> holder = SavedDataHolder.fromValue(savedData);
            registrar.playToClient(SyncSavedDataPayload.type(holder), SyncSavedDataPayload.streamCodec(holder), SyncSavedDataPayload::handle);
        }

    }
}
