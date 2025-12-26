package com.portingdeadmods.researchd.client.impl;

import com.portingdeadmods.researchd.api.client.ClientResearch;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchSelectorListWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPages;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Collections;

public class SimpleClientResearch implements ClientResearch {
    public static final SimpleClientResearch INSTANCE = new SimpleClientResearch();

    private SimpleClientResearch() {
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, Context context) {
        layout.getLayout().spacing(2);
        layout.addWidget(null, new StringWidget(Component.literal("Icon:"), PopupWidget.getFont()));
        layout.addWidget("icon", new ItemSelectorWidget(context.parentScreen(), context.parentPopupWidget(), 0, 0, CommonComponents.EMPTY));
        layout.addWidget(null, new StringWidget(Component.literal("Parents:"), PopupWidget.getFont()));
        layout.addWidget("parents_selector", new ResearchSelectorListWidget(context.parentPopupWidget(), context.innerWidth() - 2, 20, Collections.emptyList(), true));
        layout.addWidget("requires_parents", Checkbox.builder(Component.literal("Requires Parents"), PopupWidget.getFont()).build());
        layout.addWidget(null, new StringWidget(Component.literal("Method:"), PopupWidget.getFont()));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Research createResearch(RememberingLinearLayout layout) {
        DisplayImpl display = ClientEditorHelper.createDisplay(
                layout.getChild("name_edit_box", EditBox.class),
                layout.getChild("desc_edit_box", MultiLineEditBox.class)
        );
        return new SimpleResearch(
                layout.getChild("icon", ItemSelectorWidget.class).createIcon(),
                null,
                null,
                layout.getChild("parents_selector", ResearchSelectorListWidget.class).getResearches(),
                layout.getChild("requires_parents", Checkbox.class).selected(),
		        ResearchdResearchPages.DEFAULT, // TODO: Research Page into editor
                display
        );
    }
}
