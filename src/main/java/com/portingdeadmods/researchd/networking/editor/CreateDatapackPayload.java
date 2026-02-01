package com.portingdeadmods.researchd.networking.editor;

import com.portingdeadmods.portingdeadlibs.api.config.PDLConfigHelper;
import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.editor.Datapack;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import com.portingdeadmods.researchd.resources.ExampleDatapackWriter;
import com.portingdeadmods.researchd.utils.PrettyPath;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
            Result<Path, Exception> result = writer.write(datapackDir, this.name(), this.description(), PDLConfigHelper.camelToSnake(this.name()));

            if (result instanceof Result.Ok(Path value)) {
                EditModeSettingsImpl settings = context.player().getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
                Path shortPath = Path.of("..." + value.toString().substring(datapackDir.toString().length() - LevelResource.DATAPACK_DIR.getId().length() - 1));
                context.player().setData(ResearchdAttachments.EDIT_MODE_SETTINGS, new EditModeSettingsImpl(new Datapack(new PrettyPath(value, shortPath), this.namespace), settings.currentResourcePack()));
            }

        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle CreatePackPayload", err);
            return null;
        });
    }

}
