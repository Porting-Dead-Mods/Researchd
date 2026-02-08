package com.portingdeadmods.researchd.networking.editor;

import com.portingdeadmods.portingdeadlibs.api.config.PDLConfigHelper;
import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.editor.PackLocation;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import com.portingdeadmods.researchd.resources.ExampleDatapackWriter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.nio.file.Path;

public record CreateDatapackPayload(String name, String description, String namespace,
                                    boolean generateExamples) implements CustomPacketPayload {
    public static final Type<CreateDatapackPayload> TYPE = new Type<>(Researchd.rl("create_datapack"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, CreateDatapackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            CreateDatapackPayload::name,
            ByteBufCodecs.STRING_UTF8,
            CreateDatapackPayload::description,
            ByteBufCodecs.STRING_UTF8,
            CreateDatapackPayload::namespace,
            ByteBufCodecs.BOOL,
            CreateDatapackPayload::generateExamples,
            CreateDatapackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Path datapackDir = context.player().getServer().getWorldPath(LevelResource.DATAPACK_DIR);

            ExampleDatapackWriter writer = new ExampleDatapackWriter(this.generateExamples());
            String namespace1 = PDLConfigHelper.camelToSnake(this.name());
            Result<Path, Exception> result = writer.write(datapackDir, this.name(), this.description(), namespace1);

            if (result instanceof Result.Ok(Path value)) {
                EditModeSettingsImpl settings = context.player().getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
                context.player().setData(ResearchdAttachments.EDIT_MODE_SETTINGS, new EditModeSettingsImpl(new PackLocation(value, namespace1, PackType.SERVER_DATA), settings.currentResourcePack()));
            }

        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle CreatePackPayload", err);
            return null;
        });
    }

}
