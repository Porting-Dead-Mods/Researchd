package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResearchProgressSyncPayload(ResourceKey<Research> key, ResearchProgress progress) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ResearchProgressSyncPayload> TYPE = new CustomPacketPayload.Type<>(Researchd.rl("research_complete_progress_sync"));
	public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchProgressSyncPayload> STREAM_CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_KEY),
			ResearchProgressSyncPayload::key,
            ResearchProgress.STREAM_CODEC,
			ResearchProgressSyncPayload::progress,
			ResearchProgressSyncPayload::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientResearchTeamHelper.getTeam().getResearchProgresses().put(this.key, this.progress);
		}).exceptionally(err -> {
			Researchd.LOGGER.error("Failed to handle ResearchMethodProgressSyncPayload", err);
			return null;
		});
	}
}
