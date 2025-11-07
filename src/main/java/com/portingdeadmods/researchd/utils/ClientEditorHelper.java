package com.portingdeadmods.researchd.utils;

import com.portingdeadmods.researchd.api.research.EditModeSettings;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.nio.file.Path;

public final class ClientEditorHelper {
    public static EditModeSettings getEditModeSettings() {
        return Minecraft.getInstance().player.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
    }

    public static void setCurrentDatapack(Path path) {
        LocalPlayer player = Minecraft.getInstance().player;
        EditModeSettingsImpl settings = player.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
        player.setData(ResearchdAttachments.EDIT_MODE_SETTINGS, new EditModeSettingsImpl(path, settings.currentResourcePack()));
    }

    public static void setCurrentResourcePack(Path path) {
        LocalPlayer player = Minecraft.getInstance().player;
        EditModeSettingsImpl settings = player.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
        player.setData(ResearchdAttachments.EDIT_MODE_SETTINGS, new EditModeSettingsImpl(settings.currentResourcePack(), path));
    }

}
