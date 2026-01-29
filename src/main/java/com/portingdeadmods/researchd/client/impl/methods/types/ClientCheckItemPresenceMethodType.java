package com.portingdeadmods.researchd.client.impl.methods.types;

import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.ClientResearchMethodType;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.impl.research.method.CheckItemPresenceResearchMethod;
import com.portingdeadmods.researchd.impl.research.method.ConsumeItemResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import net.minecraft.client.gui.components.EditBox;

public class ClientCheckItemPresenceMethodType extends ClientConsumeItemResearchMethodType {
    public static final ClientCheckItemPresenceMethodType INSTANCE = new ClientCheckItemPresenceMethodType();

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CHECK_ITEM_PRESENCE.get();
    }

    @Override
    public ResearchMethod createResearchEffect(RememberingLinearLayout layout) {
        return new CheckItemPresenceResearchMethod(
                layout.getChild("item_selector", ItemSelectorWidget.class).createIngredient(),
                Integer.parseInt(layout.getChild("count", EditBox.class).getValue())
        );
    }

}
