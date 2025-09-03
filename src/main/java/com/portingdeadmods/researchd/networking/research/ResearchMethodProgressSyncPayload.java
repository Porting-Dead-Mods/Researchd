package com.portingdeadmods.researchd.networking.research;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.utils.ClientResearchTeamHelper;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResearchMethodProgressSyncPayload(ResourceKey<Research> res, ResearchMethodProgress prog) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ResearchMethodProgressSyncPayload> TYPE = new CustomPacketPayload.Type<>(Researchd.rl("research_complete_progress_sync"));
	public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchMethodProgressSyncPayload> STREAM_CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(ResearchdRegistries.RESEARCH_KEY),
			ResearchMethodProgressSyncPayload::res,
			ResearchMethodProgress.STREAM_CODEC,
			ResearchMethodProgressSyncPayload::prog,
			ResearchMethodProgressSyncPayload::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void researchMethodProgressSyncAction(IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientResearchTeamHelper.getTeam().getResearchProgress().progress().put(res, prog);
		}).exceptionally(err -> {
			Researchd.LOGGER.error("Failed to handle ResearchMethodProgressSyncPayload", err);
			return null;
		});
	}
}
