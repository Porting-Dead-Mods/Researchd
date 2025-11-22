package com.portingdeadmods.researchd.networking.edit;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import com.portingdeadmods.researchd.utils.PrettyPath;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetResourcePackPayload(PrettyPath resourcePackDir) implements CustomPacketPayload {
    public static final Type<SetResourcePackPayload> TYPE = new Type<>(Researchd.rl("set_resource_pack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetResourcePackPayload> STREAM_CODEC = StreamCodec.composite(
            PrettyPath.STREAM_CODEC,
            SetResourcePackPayload::resourcePackDir,
            SetResourcePackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            EditModeSettingsImpl settings = context.player().getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
            context.player().setData(ResearchdAttachments.EDIT_MODE_SETTINGS, new EditModeSettingsImpl(settings.currentDatapack(), resourcePackDir));
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle SetResourcePackPayload");
            return null;
        });
    }

}
