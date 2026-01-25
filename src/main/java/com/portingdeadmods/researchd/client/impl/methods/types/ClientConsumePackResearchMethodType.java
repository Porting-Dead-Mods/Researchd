package com.portingdeadmods.researchd.client.impl.methods.types;

import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.ClientResearchMethodType;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.client.screens.editor.EditorSharedSprites;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ItemSelectorWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.category.PackItemSelectorCategory;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.ItemSelectorPopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import com.portingdeadmods.researchd.utils.ResearchdUtils;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClientConsumePackResearchMethodType implements ClientResearchMethodType {
    public static final ClientConsumePackResearchMethodType INSTANCE = new ClientConsumePackResearchMethodType();
    public static final WidgetSprites SPRITES = new WidgetSprites(EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE, EditorSharedSprites.EDITOR_BACKGROUND_INVERTED_SPRITE);

    protected ClientConsumePackResearchMethodType() {
    }

    @Override
    public ResearchMethodType type() {
        return ResearchMethodTypes.CONSUME_PACK.get();
    }

    @Override
    public int getHeight() {
        return 152;
    }

    @Override
    public void buildLayout(RememberingLinearLayout layout, Context context) {
        layout.getLayout().spacing(2);
        // TODO: The ability to select multiple packs
        layout.addWidget(null, new StringWidget(Component.literal("Pack:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        ResourceKey<ResearchPack> defaultPack = ClientEditorHelper.getDefaultResearchPack();
        ItemStack defaultSelectedPack = defaultPack != null ? ResearchPackImpl.asStack(defaultPack) : ResearchdItems.GREEN_RESEARCH_PACK_ICON.toStack();
        layout.addWidget("pack_selector", new ItemSelectorWidget(context.parentPopupWidget(), 0, 0, 24, 24, List.of(defaultSelectedPack), this::createItemSelectorPopup), LayoutSettings::alignHorizontallyCenter);
        layout.addWidget(null, new StringWidget(Component.literal("Time:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        EditBox timeEditBox = layout.addWidget("time", new BackgroundEditBox(PopupWidget.getFont(), SPRITES, 36, 16, Component.literal("1")), LayoutSettings::alignHorizontallyCenter);
        timeEditBox.setValue("200t");
        timeEditBox.setFilter(this::isTimeValid);
        timeEditBox.setResponder(val -> this.onTimeValueChanged(val, timeEditBox));
        layout.addWidget(null, new StringWidget(Component.literal("Count:"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        EditBox countEditBox = layout.addWidget("count", new BackgroundEditBox(PopupWidget.getFont(), SPRITES, 24, 16, Component.literal("1")), LayoutSettings::alignHorizontallyCenter);
        countEditBox.setValue("1");
        countEditBox.setFilter(this::isCountValid);
    }

    private void onTimeValueChanged(String newVal, EditBox timeEditBox) {
        if (newVal.endsWith("s")) {
            int newValInt = Integer.parseInt(newVal.substring(0, newVal.length() - 1));
            timeEditBox.setValue(newValInt * 20 + "t");
        }
    }

    private boolean isTimeValid(String newVal) {
        return ResearchdUtils.isValidInt(newVal) || newVal.isEmpty() || newVal.endsWith("s") || newVal.endsWith("t");
    }

    private ItemSelectorPopupWidget createItemSelectorPopup(ItemSelectorWidget selectorWidget, @Nullable PopupWidget parent) {
        return new ItemSelectorPopupWidget(selectorWidget, parent, List.of(PackItemSelectorCategory.INSTANCE), PackItemSelectorCategory.INSTANCE, 0, 0);
    }

    private boolean isCountValid(String newVal) {
        return ResearchdUtils.isValidInt(newVal) || newVal.isEmpty();
    }

    @Override
    public ResearchMethod createResearchMethod(RememberingLinearLayout layout) {
        String time = layout.getChild("time", EditBox.class).getValue();
        return new ConsumePackResearchMethod(
                List.of(layout.getChild("pack_selector", ItemSelectorWidget.class).getSelected().getFirst().get(ResearchdDataComponents.RESEARCH_PACK).researchPackKey().get()),
                Integer.parseInt(layout.getChild("count", EditBox.class).getValue()),
                Integer.parseInt(time.substring(0, time.length() - 1))
        );
    }
}
