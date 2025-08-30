package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchCompletionProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResearchCompleteProgressSyncPayload(Float progress) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ResearchCompleteProgressSyncPayload> TYPE = new CustomPacketPayload.Type<>(Researchd.rl("research_complete_progress_sync"));
	public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchCompleteProgressSyncPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			ResearchCompleteProgressSyncPayload::progress,
			ResearchCompleteProgressSyncPayload::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void researchCompleteProgressSyncAction(IPayloadContext context) {
		context.enqueueWork(() -> {
			ResearchCompletionProgress researchProgress = ClientResearchTeamHelper.getTeam().getResearchingProgressInQueue(Minecraft.getInstance().player.registryAccess());
			if (researchProgress == null) return;

			researchProgress.setProgress(progress);
		}).exceptionally(err -> {
			Researchd.LOGGER.error("Failed to handle ResearchCompleteProgressSyncPayload", err);
			return null;
		});
	}
}
