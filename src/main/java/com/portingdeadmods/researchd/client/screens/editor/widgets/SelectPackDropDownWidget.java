package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.client.screens.lib.widgets.DropDownWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;

public class SelectPackDropDownWidget extends DropDownWidget<SelectPackSearchBarWidget> {
    private final PackRepository packRepository;

    public SelectPackDropDownWidget(PackRepository packRepository) {
        this.packRepository = packRepository;
    }

    @Override
    protected void buildOptions() {
        for (Pack pack : packRepository.getAvailablePacks()) {
            this.addOption(new StringOption(pack.getTitle(), Minecraft.getInstance().font));
        }
    }
}
