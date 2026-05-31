package com.portingdeadmods.researchd.utils.researches;

import com.portingdeadmods.researchd.api.editmode.EditModeSettings;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class ResearchEditorHelperClient {
    public static EditModeSettings getEditModeSettings() {
        return Minecraft.getInstance().player.getData(ResearchdAttachments.EDIT_MODE_SETTINGS);
    }

    public static @Nullable ResourceKey<ResearchPack> getDefaultResearchPack() {
        return ResearchHelperClient.getResearchPacks().keySet().stream().findFirst().orElse(null);
    }

    public static DisplayImpl createDisplay(EditBox nameEditBox, EditBox descEditBox) {
        Component name = null;
        Component description = null;
        if (!nameEditBox.getValue().isEmpty()) {
            name = Component.literal(nameEditBox.getValue());
        }
        if (!descEditBox.getValue().isEmpty()) {
            description = Component.literal(descEditBox.getValue());
        }
        return new DisplayImpl(Optional.ofNullable(name), Optional.ofNullable(description));
    }

    public static Inventory getPlayerInventory() {
        return Minecraft.getInstance().player.getInventory();
    }
}
