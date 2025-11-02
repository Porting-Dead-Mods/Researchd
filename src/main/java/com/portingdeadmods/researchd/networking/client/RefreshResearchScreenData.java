package com.portingdeadmods.researchd.networking.client;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RefreshResearchScreenData(boolean graphData, boolean techListData, boolean researchQueueData) implements CustomPacketPayload {
	public static RefreshResearchScreenData ALL = new RefreshResearchScreenData(true, true, true);

	public static final StreamCodec<? super RegistryFriendlyByteBuf, RefreshResearchScreenData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL,
			RefreshResearchScreenData::graphData,
			ByteBufCodecs.BOOL,
			RefreshResearchScreenData::techListData,
			ByteBufCodecs.BOOL,
			RefreshResearchScreenData::researchQueueData,
			RefreshResearchScreenData::new
	);
	public static final Type<RefreshResearchScreenData> TYPE = new Type<>(Researchd.rl("refresh_research_screen_data"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			if (graphData) ClientResearchTeamHelper.refreshGraphData();
			if (techListData) ClientResearchTeamHelper.refreshTechListData();
			if (researchQueueData) ClientResearchTeamHelper.refreshResearchQueueData();
		}).exceptionally(err -> {
			Researchd.LOGGER.error("Failed to handle RefreshResearchScreenData", err);
			return null;
		});
	}

}
