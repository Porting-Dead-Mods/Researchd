package com.portingdeadmods.researchd.client.impl.editor.methods;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.TypedEditorObject;
import com.portingdeadmods.researchd.api.research.methods.ItemResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import com.portingdeadmods.researchd.utils.GuiUtils;
import com.portingdeadmods.researchd.utils.TextUtils;
import dev.ftb.mods.ftbteams.data.TeamType;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public abstract class AbstractItemMethodObject<T extends ItemResearchMethod> implements TypedEditorObject<T, ResearchMethodType> {
    public static final WidgetSprites SPRITES = new WidgetSprites(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE);

    protected AbstractItemMethodObject() {
    }

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CONSUME_ITEM.get();
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @Nullable T previous, @UnknownNullability EditorContext context) {
        layout.getLayout().spacing(2);
        // TODO: The ability to select multiple && tag support
        layout.addWidget(null, new StringWidget(Component.literal("Item:"), GuiUtils.getFont()), LayoutSettings::alignHorizontallyCenter);
        ItemSelectorWidget itemSelector = layout.addWidget("item_selector", new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 25, 24, true, true), LayoutSettings::alignHorizontallyCenter);
        if (previous != null) {
            itemSelector.setSelected(previous.item(), false);
        }
        layout.addWidget(null, new StringWidget(Component.literal("Count:"), GuiUtils.getFont()), LayoutSettings::alignHorizontallyCenter);
        EditBox editBox = layout.addWidget("count", new BackgroundEditBox(GuiUtils.getFont(), SPRITES, 24, 16, "1"), LayoutSettings::alignHorizontallyCenter);
        if (previous != null) {
            editBox.setValue(String.valueOf(previous.count()));
        } else {
            editBox.setValue("1");
        }
        editBox.setFilter(TextUtils::isValidIntOrEmpty);
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        return Result.ok(Unit.INSTANCE);
    }
}