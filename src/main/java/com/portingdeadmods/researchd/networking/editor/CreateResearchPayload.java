package com.portingdeadmods.researchd.networking.editor;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import com.portingdeadmods.researchd.resources.editor.EditorResearchProvider;
import com.portingdeadmods.researchd.utils.PrettyPath;
import com.portingdeadmods.researchd.utils.Spaghetti;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.nio.file.Path;
import java.util.List;

public record CreateResearchPayload(ResourceKey<Research> key, Research research,
                                    boolean reloadData) implements CustomPacketPayload {
    public static final Type<CreateResearchPayload> TYPE = new Type<>(Researchd.rl("create_research"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, CreateResearchPayload> STREAM_CODEC = StreamCodec.composite(
            Research.RESOURCE_KEY_STREAM_CODEC,
            CreateResearchPayload::key,
            Research.STREAM_CODEC,
            CreateResearchPayload::research,
            ByteBufCodecs.BOOL,
            CreateResearchPayload::reloadData,
            CreateResearchPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                EditModeSettingsImpl settings = serverPlayer.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
                PrettyPath prettyPath = settings.currentDatapack();
                Path datapacksDirectoryPath = prettyPath.fullPath();
                EditorResearchProvider researchProvider = settings.getWriter().getOrAddProvider(new EditorResearchProvider());
                researchProvider.putResearch(this.key(), research);
                Result<Path, ? extends Exception> result = settings.getWriter().write(datapacksDirectoryPath, "Test", "example");
                switch (result) {
                    case Result.Err<Path, ? extends Exception>(Exception error) -> Researchd.LOGGER.error("Failed to write datapack", error);
                    case Result.Ok<Path, ? extends Exception> ok -> Researchd.LOGGER.info("Successfully wrote research {} to datapack {}", key.location(), datapacksDirectoryPath);
                }

                if (this.reloadData) {
                    MinecraftServer server = serverPlayer.getServer();
                    // TODO: Use proper datapack id
                    String[] split = prettyPath.toString().split("/");
                    server.reloadResources(List.of("file/" + split[split.length - 1]));
                }
            } else {
                throw new IllegalStateException("Handling payload on client");
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle CreateResearchPayload", err);
            return null;
        });
    }

}
