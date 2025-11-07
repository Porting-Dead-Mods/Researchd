package com.portingdeadmods.researchd.networking.edit;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncEditModeSettingsPayload(EditModeSettingsImpl editModeSettings) implements CustomPacketPayload {
    public static final Type<SyncEditModeSettingsPayload> TYPE = new Type<>(Researchd.rl("sync_edit_mode_settings"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(ResearchdAttachments.EDIT_MODE_SETTINGS, this.editModeSettings());
        }).exceptionally(err -> {
            Researchd.LOGGER.error("Failed to handle SyncEditModeSettingsPayload", err);
            return null;
        });
    }

}
