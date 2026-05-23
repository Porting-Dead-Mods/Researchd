package com.portingdeadmods.researchd.client.impl.editor.methods;

import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import com.portingdeadmods.researchd.utils.NumberUtils;
import net.minecraft.client.gui.components.EditBox;

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
