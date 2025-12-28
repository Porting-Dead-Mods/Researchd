package com.portingdeadmods.researchd.client.impl.methods.types;

import com.portingdeadmods.researchd.api.client.ClientResearch;
import com.portingdeadmods.researchd.api.client.ClientResearchMethodType;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ClientConsumeItemResearchMethodType implements ClientResearchMethodType {
    public static final ClientConsumeItemResearchMethodType INSTANCE = new ClientConsumeItemResearchMethodType();
    public static final WidgetSprites SPRITES = new WidgetSprites(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE);

    private ClientConsumeItemResearchMethodType() {
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, ClientResearch.Context context) {
        layout.getLayout().spacing(2);
        layout.addWidget(null, new StringWidget(Component.literal("Item:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        layout.addWidget("item_selector", new ItemSelectorWidget(context.parentScreen(), context.parentPopupWidget(), 0, 0, CommonComponents.EMPTY), LayoutSettings::alignHorizontallyCenter);
        layout.addWidget(null, new StringWidget(Component.literal("Count:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        EditBox editBox = layout.addWidget("count", new BackgroundEditBox(PopupWidget.getFont(), SPRITES, 24, 16, CommonComponents.EMPTY), LayoutSettings::alignHorizontallyCenter);
        editBox.setFilter(this::isValid);
    }

    private boolean isValid(String newVal) {
        return isValidInt(newVal) || newVal.isEmpty();
    }

    private static boolean isValidInt(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public ResearchMethod createResearchMethod(RememberingLinearLayout layout) {
        return new ConsumeItemResearchMethod(
                layout.getChild("item_selector", ItemSelectorWidget.class).createIngredient(),
                Integer.parseInt(layout.getChild("count", EditBox.class).getValue())
        );
    }
}
