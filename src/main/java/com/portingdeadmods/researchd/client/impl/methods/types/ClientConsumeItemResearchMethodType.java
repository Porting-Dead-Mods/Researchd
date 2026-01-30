package com.portingdeadmods.researchd.client.impl.methods.types;

import com.portingdeadmods.researchd.api.client.editor.ClientResearchMethodType;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import com.portingdeadmods.researchd.utils.ResearchdUtils;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

public class ClientConsumeItemResearchMethodType implements ClientResearchMethodType {
    public static final ClientConsumeItemResearchMethodType INSTANCE = new ClientConsumeItemResearchMethodType();
    public static final WidgetSprites SPRITES = new WidgetSprites(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE);

    protected ClientConsumeItemResearchMethodType() {
    }

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CONSUME_ITEM.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, ClientResearchMethodType.Context context) {
        layout.getLayout().spacing(2);
        // TODO: The ability to select multiple && tag support
        layout.addWidget(null, new StringWidget(Component.literal("Item:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        layout.addWidget("item_selector", new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 24, 24, true, true), LayoutSettings::alignHorizontallyCenter);
        layout.addWidget(null, new StringWidget(Component.literal("Count:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        EditBox editBox = layout.addWidget("count", new BackgroundEditBox(PopupWidget.getFont(), SPRITES, 24, 16, Component.literal("1")), LayoutSettings::alignHorizontallyCenter);
        editBox.setValue("1");
        editBox.setFilter(this::isValid);
    }

    private boolean isValid(String newVal) {
        return ResearchdUtils.isValidInt(newVal) || newVal.isEmpty();
    }

    @Override
    public ResearchMethod createResearchEffect(RememberingLinearLayout layout) {
        return new ConsumeItemResearchMethod(
                layout.getChild("item_selector", ItemSelectorWidget.class).getSelected(),
                Integer.parseInt(layout.getChild("count", EditBox.class).getValue())
        );
    }
}
