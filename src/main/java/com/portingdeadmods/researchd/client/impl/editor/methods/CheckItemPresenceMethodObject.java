package com.portingdeadmods.researchd.client.impl.editor.methods;

import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.impl.research.method.CheckItemPresenceResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import net.minecraft.client.gui.components.EditBox;

public class CheckItemPresenceMethodObject extends ConsumeItemMethodObject {
    public static final CheckItemPresenceMethodObject INSTANCE = new CheckItemPresenceMethodObject();

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CHECK_ITEM_PRESENCE.get();
    }

    @Override
    public ResearchMethod create(RememberingLinearLayout layout) {
        return new CheckItemPresenceResearchMethod(
                layout.getChild("item_selector", ItemSelectorWidget.class).getSelected(),
                Integer.parseInt(layout.getChild("count", EditBox.class).getValue())
        );
    }

}
