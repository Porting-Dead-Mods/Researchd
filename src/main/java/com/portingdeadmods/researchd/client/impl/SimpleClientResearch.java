package com.portingdeadmods.researchd.client.impl;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.client.editor.ClientResearch;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.BaseResearchEffectCreationWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.BaseResearchMethodCreationWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchSelectorListWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.pdl.config.PDLConfigHelper;
import com.portingdeadmods.researchd.resources.contents.ResearchdResearchPages;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.List;

public class SimpleClientResearch implements ClientResearch {
    public static final SimpleClientResearch INSTANCE = new SimpleClientResearch();

    private SimpleClientResearch() {
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, Context context) {
        layout.getLayout().spacing(2);
        layout.addWidget(null, new StringWidget(Component.literal("Display:"), PopupWidget.getFont()));
        BackgroundEditBox nameEditBox = layout.addWidget("name_edit_box", new BackgroundEditBox(PopupWidget.getFont(), context.innerWidth() - 4, 16, CommonComponents.EMPTY));
        nameEditBox.setHint(Component.literal("<Name>"));
        nameEditBox.setResponder(newVal -> this.updateResearch(layout, context));
        BackgroundEditBox descEditBox = layout.addWidget("desc_edit_box", new BackgroundEditBox(PopupWidget.getFont(), context.innerWidth() - 4, 16, CommonComponents.EMPTY));
        descEditBox.setHint(Component.literal("<Desc>"));
        layout.addWidget(null, new StringWidget(Component.literal("Icon:"), PopupWidget.getFont()));
        layout.addWidget("icon", new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 20, 20, false, true));
        layout.addWidget(null, new StringWidget(Component.literal("Parents:"), PopupWidget.getFont()));
        layout.addWidget("parents_selector", new ResearchSelectorListWidget(context.parentPopupWidget(), context.innerWidth() - 4, 24, Collections.emptyList(), true));
        layout.addWidget("requires_parents", Checkbox.builder(Component.literal("Requires Parents"), PopupWidget.getFont()).build());
        layout.addWidget(null, new StringWidget(Component.literal("Method:"), PopupWidget.getFont()));
        BaseResearchMethodCreationWidget methodWidget = layout.addWidget("method", new BaseResearchMethodCreationWidget(context.parentPopupWidget(), 0, 0, context.innerWidth() - 4, 32, CommonComponents.EMPTY));
        methodWidget.setResponder(() -> this.updateResearch(layout, context));
        layout.addWidget(null, new StringWidget(Component.literal("Effect:"), PopupWidget.getFont()));
        layout.addWidget("effect", new BaseResearchEffectCreationWidget(context.parentPopupWidget(), 0, 0, context.innerWidth() - 4, 32, CommonComponents.EMPTY));
    }

    @Override
    public ResourceLocation createId(RememberingLinearLayout layout) {
        return ResourceLocation.parse(PDLConfigHelper.camelToSnake(layout.getChild("name_edit_box", BackgroundEditBox.class).getValue()));
    }

    @Override
    public Result<Unit, Exception> validResearch(RememberingLinearLayout layout) {
        boolean nameEditBoxNotEmpty = !layout.getChild("name_edit_box", BackgroundEditBox.class).getValue().isEmpty();
        boolean researchMethodNotEmpty = layout.getChild("method", BaseResearchMethodCreationWidget.class).getMethod() != null;
        if (!nameEditBoxNotEmpty) {
            return Result.err("Research needs a name");
        }

        if (!researchMethodNotEmpty) {
            return Result.err("Research needs a method");
        }

        return Result.ok(Unit.INSTANCE);
    }

    @Override
    public void updateResearch(RememberingLinearLayout layout, Context context) {
        if (this.validResearch(layout) instanceof Result.Err<Unit, Exception>(Exception error)) {
            context.setCreateButtonActive(false);
            context.setCreateButtonTooltip(Tooltip.create(Component.literal(error.getMessage())));
        } else {
            context.setCreateButtonActive(true);
            context.setCreateButtonTooltip(null);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Research createResearch(RememberingLinearLayout layout) {
        DisplayImpl display = ClientEditorHelper.createDisplay(
                layout.getChild("name_edit_box", BackgroundEditBox.class),
                layout.getChild("desc_edit_box", BackgroundEditBox.class)
        );
        return new SimpleResearch(
                layout.getChild("icon", ItemSelectorWidget.class).createIcon(),
                layout.getChild("method", BaseResearchMethodCreationWidget.class).getMethod(),
                layout.getChild("effect", BaseResearchEffectCreationWidget.class).getEffect(),
                layout.getChild("parents_selector", ResearchSelectorListWidget.class).getResearches(),
                layout.getChild("requires_parents", Checkbox.class).selected(),
		        ResearchdResearchPages.DEFAULT, // TODO: Research Page into editor
                display
        );
    }
    
}
