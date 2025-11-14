package com.portingdeadmods.researchd.networking.edit;

import com.portingdeadmods.portingdeadlibs.api.config.PDLConfigHelper;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.resources.ExampleDatapack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.nio.file.Path;

public record CreateDatapackPayload(String name, String description,
                                    boolean generateExamples) implements CustomPacketPayload {
    public static final Type<CreateDatapackPayload> TYPE = new Type<>(Researchd.rl("create_datapack"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, CreateDatapackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            CreateDatapackPayload::name,
            ByteBufCodecs.STRING_UTF8,
            CreateDatapackPayload::description,
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

            ExampleDatapack.createExample(datapackDir, this.name(), this.description(), PDLConfigHelper.camelToSnake(this.name()), this.generateExamples());
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle CreatePackPayload", err);
            return null;
        });
    }

}
