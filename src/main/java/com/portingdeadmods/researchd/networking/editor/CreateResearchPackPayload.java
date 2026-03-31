package com.portingdeadmods.researchd.networking.editor;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import com.portingdeadmods.researchd.networking.research.ResearchCacheReloadPayload;
import com.portingdeadmods.researchd.resources.editor.EditorResearchPackProvider;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperServer;
import com.portingdeadmods.researchd.utils.researches.ResearchdManagers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.nio.file.Path;
import java.util.Collections;

public record CreateResearchPackPayload(ResourceKey<ResearchPack> key, ResearchPack researchPack,
                                        boolean reloadData) implements CustomPacketPayload {
    public static final Type<CreateResearchPackPayload> TYPE = new Type<>(Researchd.rl("create_research_pack"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, CreateResearchPackPayload> STREAM_CODEC = StreamCodec.composite(
            ResearchPack.RESOURCE_KEY_STREAM_CODEC,
            CreateResearchPackPayload::key,
            ResearchPack.STREAM_CODEC,
            CreateResearchPackPayload::researchPack,
            ByteBufCodecs.BOOL,
            CreateResearchPackPayload::reloadData,
            CreateResearchPackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                EditModeSettingsImpl settings = serverPlayer.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
                Path datapacksDirectoryPath = settings.currentDatapack().rootPath();
                EditorResearchPackProvider researchProvider = settings.getWriter().getOrAddProvider(new EditorResearchPackProvider());
                researchProvider.putResearchPack(this.key(), researchPack);
                Result<Path, ? extends Exception> result = settings.getWriter().write(datapacksDirectoryPath, null, settings.currentDatapack().namespace());
                switch (result) {
                    case Result.Err<Path, ? extends Exception>(Exception error) -> Researchd.LOGGER.error("Failed to write datapack", error);
                    case Result.Ok<Path, ? extends Exception> ignored -> Researchd.LOGGER.info("Successfully wrote researchPack pack {} to datapack {}", key.location(), datapacksDirectoryPath);
                }

                if (this.reloadData) {
                    ResearchdManagers.getResearchPacksManager(serverPlayer.level()).mergeContents(Collections.singletonMap(key.location(), researchPack));
                    // Reload researches on the server
                    ResearchHelperServer.reloadResearches(serverPlayer.server, null, serverPlayer.server.getPlayerList().getPlayers());
                    // Reload researches on the client
                    PacketDistributor.sendToPlayer(serverPlayer, new ResearchCacheReloadPayload());
                }
            } else {
                throw new IllegalStateException("Handling payload on client");
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle CreateResearchPackPayload", err);
            return null;
        });
    }

}
