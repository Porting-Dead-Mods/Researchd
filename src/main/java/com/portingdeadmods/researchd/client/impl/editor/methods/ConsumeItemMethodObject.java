package com.portingdeadmods.researchd.client.impl.editor.methods;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import com.portingdeadmods.researchd.utils.TextUtils;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.UnknownNullability;

public class ConsumeItemMethodObject implements TypedEditorObject<ResearchMethod, ResearchMethodType> {
    public static final ConsumeItemMethodObject INSTANCE = new ConsumeItemMethodObject();
    public static final WidgetSprites SPRITES = new WidgetSprites(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE);

    protected ConsumeItemMethodObject() {
    }

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CONSUME_ITEM.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @UnknownNullability EditorContext context) {
        layout.getLayout().spacing(2);
        // TODO: The ability to select multiple && tag support
        layout.addWidget(null, new StringWidget(Component.literal("Item:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        layout.addWidget("item_selector", new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 25, 24, true, true), LayoutSettings::alignHorizontallyCenter);
        layout.addWidget(null, new StringWidget(Component.literal("Count:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        EditBox editBox = layout.addWidget("count", new BackgroundEditBox(PopupWidget.getFont(), SPRITES, 24, 16, "1"), LayoutSettings::alignHorizontallyCenter);
        editBox.setValue("1");
        editBox.setFilter(this::isCountValid);
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        return Result.ok(Unit.INSTANCE);
    }

    private boolean isCountValid(String newVal) {
        return TextUtils.isValidInt(newVal) || newVal.isEmpty();
    }

    @Override
    public ResearchMethod create(RememberingLinearLayout layout) {
        return new ConsumeItemResearchMethod(
                layout.getChild("item_selector", ItemSelectorWidget.class).getSelected(),
                Integer.parseInt(layout.getChild("count", EditBox.class).getValue())
        );
    }
}
