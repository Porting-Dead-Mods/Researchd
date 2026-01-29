package com.portingdeadmods.researchd.client.impl;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.editor.ClientResearch;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchEffectSelectionWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchMethodSelectionWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchSelectorListWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.effect.EmptyResearchEffect;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPages;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import oshi.hardware.Display;

import java.util.Collections;

public class SimpleClientResearch implements ClientResearch {
    public static final SimpleClientResearch INSTANCE = new SimpleClientResearch();

    private SimpleClientResearch() {
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, Context context) {
        layout.getLayout().spacing(2);
        layout.addWidget(null, new StringWidget(Component.literal("Icon:"), PopupWidget.getFont()));
        layout.addWidget("icon", new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 20, 20, false, true));
        layout.addWidget(null, new StringWidget(Component.literal("Parents:"), PopupWidget.getFont()));
        layout.addWidget("parents_selector", new ResearchSelectorListWidget(context.parentPopupWidget(), context.innerWidth() - 4, 24, Collections.emptyList(), true));
        layout.addWidget("requires_parents", Checkbox.builder(Component.literal("Requires Parents"), PopupWidget.getFont()).build());
        layout.addWidget(null, new StringWidget(Component.literal("Method:"), PopupWidget.getFont()));
        layout.addWidget("method", new ResearchMethodSelectionWidget(context.parentPopupWidget(), 0, 0, context.innerWidth() - 4, 32, CommonComponents.EMPTY));
        layout.addWidget(null, new StringWidget(Component.literal("Effect:"), PopupWidget.getFont()));
        layout.addWidget("effect", new ResearchEffectSelectionWidget(context.parentPopupWidget(), 0, 0, context.innerWidth() - 4, 32, CommonComponents.EMPTY));
    }

    @Override
    public ResourceLocation createId(RememberingLinearLayout layout) {
        return Researchd.rl("test");
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Research createResearch(RememberingLinearLayout layout) {
        //DisplayImpl display = ClientEditorHelper.createDisplay(
        //        layout.getChild("name_edit_box", EditBox.class),
        //        layout.getChild("desc_edit_box", MultiLineEditBox.class)
        //);
        return new SimpleResearch(
                layout.getChild("icon", ItemSelectorWidget.class).createIcon(),
                layout.getChild("method", ResearchMethodSelectionWidget.class).getMethod(),
                layout.getChild("effect", ResearchEffectSelectionWidget.class).getEffect(),
                layout.getChild("parents_selector", ResearchSelectorListWidget.class).getResearches(),
                layout.getChild("requires_parents", Checkbox.class).selected(),
		        ResearchdResearchPages.DEFAULT, // TODO: Research Page into editor
                DisplayImpl.EMPTY
        );
    }
    
}
