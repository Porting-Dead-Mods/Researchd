package com.portingdeadmods.researchd.networking;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.networking.cache.ClearGraphCachePayload;
import com.portingdeadmods.researchd.networking.client.RefreshResearchScreenData;
import com.portingdeadmods.researchd.networking.editor.CreateDatapackPayload;
import com.portingdeadmods.researchd.networking.editor.CreateResearchPayload;
import com.portingdeadmods.researchd.networking.editor.SetPackPayload;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchPacksPayload;
import com.portingdeadmods.researchd.networking.registries.UpdateResearchesPayload;
import com.portingdeadmods.researchd.networking.research.*;
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
                EnterTeamPayload::handle
        );
        registrar.playToServer(
                LeaveTeamPayload.TYPE,
                LeaveTeamPayload.STREAM_CODEC,
                LeaveTeamPayload::handle
        );

        registrar.playToServer(
                ManageMemberPayload.TYPE,
                ManageMemberPayload.STREAM_CODEC,
                ManageMemberPayload::handle
        );
        registrar.playToServer(
                ManageModeratorPayload.TYPE,
                ManageModeratorPayload.STREAM_CODEC,
                ManageModeratorPayload::handle
        );
        registrar.playToServer(
                TransferOwnershipPayload.TYPE,
                TransferOwnershipPayload.STREAM_CODEC,
                TransferOwnershipPayload::handle
        );

        registrar.playToServer(
                TeamSetNamePayload.TYPE,
                TeamSetNamePayload.STREAM_CODEC,
                TeamSetNamePayload::handle
        );

        registrar.playToServer(
                InvitePlayerPayload.TYPE,
                InvitePlayerPayload.STREAM_CODEC,
                InvitePlayerPayload::handle
        );
        registrar.playToServer(
                RequestToJoinPayload.TYPE,
                RequestToJoinPayload.STREAM_CODEC,
                RequestToJoinPayload::handle
        );

        registrar.playToServer(
                CreateDatapackPayload.TYPE,
                CreateDatapackPayload.STREAM_CODEC,
                CreateDatapackPayload::handle
        );
        registrar.playToServer(
                SetPackPayload.TYPE,
                SetPackPayload.STREAM_CODEC,
                SetPackPayload::handle
        );

        registrar.playToServer(
                CreateResearchPayload.TYPE,
                CreateResearchPayload.STREAM_CODEC,
                CreateResearchPayload::handle
        );

        registrar.playToServer(
                ResearchQueueAddPayload.TYPE,
                ResearchQueueAddPayload.STREAM_CODEC,
                ResearchQueueAddPayload::handle
        );
        registrar.playToServer(
                ResearchQueueRemovePayload.TYPE,
                ResearchQueueRemovePayload.STREAM_CODEC,
                ResearchQueueRemovePayload::handle
        );

        registrar.playToClient(
                ClearGraphCachePayload.TYPE,
                ClearGraphCachePayload.STREAM_CODEC,
                ClearGraphCachePayload::handle
        );

        registrar.playToClient(
                ResearchCacheReloadPayload.TYPE,
                ResearchCacheReloadPayload.STREAM_CODEC,
                ResearchCacheReloadPayload::handle
        );

	    registrar.playToClient(
			    RefreshResearchScreenData.TYPE,
			    RefreshResearchScreenData.STREAM_CODEC,
			    RefreshResearchScreenData::handle
	    );
        registrar.playToClient(
                UpdateResearchesPayload.TYPE,
                UpdateResearchesPayload.STREAM_CODEC,
                UpdateResearchesPayload::handle
        );
        registrar.playToClient(
                UpdateResearchPacksPayload.TYPE,
                UpdateResearchPacksPayload.STREAM_CODEC,
                UpdateResearchPacksPayload::handle
        );

        registrar.playToClient(
                ResearchFinishedPayload.TYPE,
                ResearchFinishedPayload.STREAM_CODEC,
                ResearchFinishedPayload::handle
        );
        registrar.playToClient(
                RefreshResearchesPayload.TYPE,
                RefreshResearchesPayload.STREAM_CODEC,
                RefreshResearchesPayload::handle
        );
        registrar.playToClient(
                RefreshPlayerManagementPayload.TYPE,
                RefreshPlayerManagementPayload.STREAM_CODEC,
                RefreshPlayerManagementPayload::handle
        );
        registrar.playToClient(
                ResearchProgressSyncPayload.TYPE,
                ResearchProgressSyncPayload.STREAM_CODEC,
                ResearchProgressSyncPayload::handle
        );
    }
}
