package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResearchMethodProgressSyncPayload(Float progress) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ResearchMethodProgressSyncPayload> TYPE = new CustomPacketPayload.Type<>(Researchd.rl("research_complete_progress_sync"));
	public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchMethodProgressSyncPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			ResearchMethodProgressSyncPayload::progress,
			ResearchMethodProgressSyncPayload::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void researchMethodProgressSyncAction(IPayloadContext context) {
		context.enqueueWork(() -> {
			ResearchMethodProgress researchProgress = ClientResearchTeamHelper.getTeam().getResearchingProgressInQueue(Minecraft.getInstance().player.registryAccess());
			if (researchProgress == null) return;

			researchProgress.setProgress(progress);
		}).exceptionally(err -> {
			Researchd.LOGGER.error("Failed to handle ResearchMethodProgressSyncPayload", err);
			return null;
		});
	}
}
