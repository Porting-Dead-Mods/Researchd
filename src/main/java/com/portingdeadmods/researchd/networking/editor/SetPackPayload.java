package com.portingdeadmods.researchd.networking.editor;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.editor.PackLocation;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetPackPayload(PackLocation packLocation) implements CustomPacketPayload {
    public static final Type<SetPackPayload> TYPE = new Type<>(Researchd.rl("set_pack"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SetPackPayload> STREAM_CODEC = StreamCodec.composite(
            PackLocation.STREAM_CODEC,
            SetPackPayload::packLocation,
            SetPackPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            EditModeSettingsImpl settings = context.player().getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
            if (packLocation.type() == PackType.SERVER_DATA) {
                context.player().setData(ResearchdAttachments.EDIT_MODE_SETTINGS, new EditModeSettingsImpl(packLocation, settings.currentResourcePack()));
            } else {
                context.player().setData(ResearchdAttachments.EDIT_MODE_SETTINGS, new EditModeSettingsImpl(settings.currentDatapack(), packLocation));
            }
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle CreatePackPayload", err);
            return null;
        });
    }

}
