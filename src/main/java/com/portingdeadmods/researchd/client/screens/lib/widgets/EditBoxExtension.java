package com.portingdeadmods.researchd.client.screens.lib.widgets;

import net.minecraft.client.gui.components.WidgetSprites;

public interface EditBoxExtension {
    WidgetSprites getSprites(WidgetSprites original);

    default void onValueChangedExtra(String newText) {

    }

}
