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
import com.portingdeadmods.researchd.translations.NumberUtils;
import com.portingdeadmods.researchd.utils.TextUtils;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.UnknownNullability;

public class ConsumeItemMethodObject extends AbstractItemMethodObject<ConsumeItemResearchMethod> {
    public static final ConsumeItemMethodObject INSTANCE = new ConsumeItemMethodObject();

    protected ConsumeItemMethodObject() {
    }

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CONSUME_ITEM.get();
    }

    @Override
    public ConsumeItemResearchMethod create(RememberingLinearLayout layout) {
        return new ConsumeItemResearchMethod(
                layout.getChild("item_selector", ItemSelectorWidget.class).getSelected(),
                NumberUtils.parseIntOr(layout.getChild("count", EditBox.class).getValue(), 1)
        );
    }
}
