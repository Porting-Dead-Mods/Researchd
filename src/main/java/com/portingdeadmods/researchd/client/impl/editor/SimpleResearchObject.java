package com.portingdeadmods.researchd.client.impl.editor;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.StandaloneEditorObject;
import com.portingdeadmods.researchd.api.research.ResearchPage;
import com.portingdeadmods.researchd.client.screens.editor.widgets.EmbeddedEffectCreationWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.EmbeddedMethodCreationWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchSelectorListWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import com.portingdeadmods.researchd.utils.TextUtils;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collections;
import java.util.List;

public class SimpleResearchObject implements StandaloneEditorObject<SimpleResearch> {
    public static final SimpleResearchObject INSTANCE = new SimpleResearchObject();

    private SimpleResearchObject() {
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, @Nullable SimpleResearch previous, @UnknownNullability EditorContext context) {
        layout.getLayout().spacing(2);
        layout.addWidget(null, new StringWidget(Component.literal("Display:"), PopupWidget.getFont()));
        BackgroundEditBox nameEditBox = layout.addWidget("name_edit_box", new BackgroundEditBox(PopupWidget.getFont(), context.innerWidth() - 4, 16));
        nameEditBox.setHint(Component.literal("<Name>"));
        //nameEditBox.setFilter(TextUtils::isValidNamespace);
        nameEditBox.setResponder(newVal -> this.update(layout, context));
        BackgroundEditBox descEditBox = layout.addWidget("desc_edit_box", new BackgroundEditBox(PopupWidget.getFont(), context.innerWidth() - 4, 16));
        descEditBox.setHint(Component.literal("<Desc>"));
        layout.addWidget(null, new StringWidget(Component.literal("Icon:"), PopupWidget.getFont()));
        ItemSelectorWidget itemSelectorWidget = layout.addWidget("icon", new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 20, 20, false, true));
        layout.addWidget(null, new StringWidget(Component.literal("Page:"), PopupWidget.getFont()));
        BackgroundEditBox pageEditBox = layout.addWidget("page", new BackgroundEditBox(PopupWidget.getFont(), context.innerWidth() - 4, 16));
        layout.addWidget(null, new StringWidget(Component.literal("Parents:"), PopupWidget.getFont()));
        ResearchSelectorListWidget parentsSelector = layout.addWidget("parents_selector", new ResearchSelectorListWidget(context.parentPopupWidget(), context.innerWidth() - 4, 24, Collections.emptyList(), true));
        layout.addWidget("requires_parents", Checkbox.builder(Component.literal("Requires Parents"), PopupWidget.getFont())
                .selected(previous != null && previous.requiresParent())
                .build());
        layout.addWidget(null, new StringWidget(Component.literal("Method:"), PopupWidget.getFont()));
        EmbeddedMethodCreationWidget methodWidget = layout.addWidget("method", new EmbeddedMethodCreationWidget(context.parentPopupWidget(), 0, 0, context.innerWidth() - 4, 32, CommonComponents.EMPTY));
        methodWidget.setResponder(() -> this.update(layout, context));
        layout.addWidget(null, new StringWidget(Component.literal("Effect:"), PopupWidget.getFont()));
        EmbeddedEffectCreationWidget effectWidget = layout.addWidget("effect", new EmbeddedEffectCreationWidget(context.parentPopupWidget(), 0, 0, context.innerWidth() - 4, 32, CommonComponents.EMPTY));

        if (previous != null) {
            nameEditBox.setValue(previous.display().name().orElse(Component.empty()).getString());
            descEditBox.setValue(previous.display().desc().orElse(Component.empty()).getString());
            if (previous.researchIcon() instanceof ItemResearchIcon(List<ItemStack> items)) {
                itemSelectorWidget.setSelected(items, false);
            }
            pageEditBox.setValue(previous.researchPage().toString());
            pageEditBox.setValue(ResearchPage.DEFAULT_PAGE_ID.toString());
            parentsSelector.setPrevious(previous.parents());
            methodWidget.setCreatedMethod(previous.researchMethod());
            effectWidget.setCreatedEffect(previous.researchEffect());
        }
    }

    @Override
    public ResourceLocation createId(RememberingLinearLayout layout) {
        String nameEditBox = TextUtils.camelToSnake(layout.getChild("name_edit_box", BackgroundEditBox.class).getValue());
        return ResourceLocation.parse(nameEditBox);
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        boolean nameEditBoxNotEmpty = !layout.getChild("name_edit_box", BackgroundEditBox.class).getValue().isEmpty();
        boolean researchMethodNotEmpty = layout.getChild("method", EmbeddedMethodCreationWidget.class).getMethod() != null;
        if (!nameEditBoxNotEmpty) {
            return Result.err("Research needs a name");
        }

        if (!researchMethodNotEmpty) {
            return Result.err("Research needs a method");
        }

        return Result.ok(Unit.INSTANCE);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public SimpleResearch create(RememberingLinearLayout layout) {
        DisplayImpl display = ClientEditorHelper.createDisplay(
                layout.getChild("name_edit_box", BackgroundEditBox.class),
                layout.getChild("desc_edit_box", BackgroundEditBox.class)
        );
        String page = layout.getChild("page", BackgroundEditBox.class).getValue();
        return new SimpleResearch(
                layout.getChild("icon", ItemSelectorWidget.class).createIcon(),
                layout.getChild("method", EmbeddedMethodCreationWidget.class).getMethod(),
                layout.getChild("effect", EmbeddedEffectCreationWidget.class).getEffect(),
                layout.getChild("parents_selector", ResearchSelectorListWidget.class).getResearches(),
                layout.getChild("requires_parents", Checkbox.class).selected(),
		        !page.isEmpty() ? ResourceLocation.parse(page) : ResearchPage.DEFAULT_PAGE_ID,
                display
        );
    }
    
}
