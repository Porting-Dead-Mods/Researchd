package com.portingdeadmods.researchd.client.impl.editor;

import com.portingdeadmods.portingdeadlibs.api.utils.RGBAColor;
import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.api.client.editor.EditorContext;
import com.portingdeadmods.researchd.api.client.editor.StandaloneEditorObject;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchPackPreviewWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import com.portingdeadmods.researchd.utils.GuiUtils;
import com.portingdeadmods.researchd.utils.TextUtils;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.Line;
import java.util.Optional;

public class ResearchPackObject implements StandaloneEditorObject<ResearchPack> {
    public static final ResearchPackObject INSTANCE = new ResearchPackObject();

    @Override
    public void buildLayout(RememberingLinearLayout layout, EditorContext context) {
        layout.getLayout().spacing(2);
        layout.addWidget(null, new StringWidget(Component.literal("Display:"), PopupWidget.getFont()));
        BackgroundEditBox nameEditBox = layout.addWidget("name_edit_box", new BackgroundEditBox(PopupWidget.getFont(), context.innerWidth() - 4, 16));
        nameEditBox.setHint(Component.literal("<Name>"));
        nameEditBox.setResponder(newVal -> this.update(layout, context));
        BackgroundEditBox descEditBox = layout.addWidget("desc_edit_box", new BackgroundEditBox(PopupWidget.getFont(), context.innerWidth() - 4, 16));
        descEditBox.setHint(Component.literal("<Desc>"));

        layout.addWidget(null, new StringWidget(Component.literal("Color (r,g,b,a):"), PopupWidget.getFont()));
        LinearLayout colorLayout = layout.addChild(LinearLayout.horizontal());
        BackgroundEditBox rEditBox = colorLayout.addChild(new BackgroundEditBox(GuiUtils.getFont(), 26, 16));
        rEditBox.setValue("255");
        rEditBox.setHint(Component.literal("<r>"));
        rEditBox.setFilter(newVal -> TextUtils.isValidIntInRange(newVal, 0, 255));
        layout.getWidgets().put("r_edit_box", rEditBox);
        BackgroundEditBox gEditBox = colorLayout.addChild(new BackgroundEditBox(GuiUtils.getFont(), 26, 16));
        gEditBox.setValue("255");
        gEditBox.setHint(Component.literal("<g>"));
        gEditBox.setFilter(newVal -> TextUtils.isValidIntInRange(newVal, 0, 255));
        layout.getWidgets().put("g_edit_box", gEditBox);
        BackgroundEditBox bEditBox = colorLayout.addChild(new BackgroundEditBox(GuiUtils.getFont(), 26, 16));
        bEditBox.setValue("255");
        bEditBox.setHint(Component.literal("<b>"));
        bEditBox.setFilter(newVal -> TextUtils.isValidIntInRange(newVal, 0, 255));
        layout.getWidgets().put("b_edit_box", bEditBox);
        BackgroundEditBox aEditBox = colorLayout.addChild(new BackgroundEditBox(GuiUtils.getFont(), 26, 16));
        aEditBox.setValue("255");
        aEditBox.setHint(Component.literal("<a>"));
        aEditBox.setFilter(newVal -> TextUtils.isValidIntInRange(newVal, 0, 255));
        layout.getWidgets().put("a_edit_box", aEditBox);

        layout.addWidget(null, new ResearchPackPreviewWidget(() -> getRgbaColor(layout), 16, 16), LayoutSettings::alignHorizontallyCenter);

        layout.addWidget(null, GuiUtils.stringWidget("Sorting Value:"));
        BackgroundEditBox valueEditBox = layout.addWidget("sorting_value_edit_box", new BackgroundEditBox(GuiUtils.getFont(), context.innerWidth() - 8, 16));
        valueEditBox.setFilter(TextUtils::isValidInt);
        valueEditBox.setResponder(newVal -> this.update(layout, context));
        valueEditBox.setValue("1");

        layout.addWidget(null, GuiUtils.stringWidget("Custom Texture:"));
        BackgroundEditBox textureEditBox = layout.addWidget("custom_texture_edit_box", new BackgroundEditBox(GuiUtils.getFont(), context.innerWidth() - 8, 16));
        textureEditBox.setFilter(TextUtils::isValidResourceLocation);
        textureEditBox.setResponder(newVal -> this.update(layout, context));
        textureEditBox.setHint(Component.literal("<Optional>"));

    }

    private @NotNull RGBAColor getRgbaColor(RememberingLinearLayout layout) {
        int r = Integer.parseInt(layout.getChild("r_edit_box", BackgroundEditBox.class).getValue());
        int g = Integer.parseInt(layout.getChild("g_edit_box", BackgroundEditBox.class).getValue());
        int b = Integer.parseInt(layout.getChild("b_edit_box", BackgroundEditBox.class).getValue());
        int a = Integer.parseInt(layout.getChild("a_edit_box", BackgroundEditBox.class).getValue());
        return new RGBAColor(r, g, b, a);
    }

    @Override
    public ResearchPack create(RememberingLinearLayout layout) {
        RGBAColor color = getRgbaColor(layout);
        int sortingValue = Integer.parseInt(layout.getChild("sorting_value_edit_box", BackgroundEditBox.class).getValue());
        String customTextureLoc = layout.getChild("custom_texture_edit_box", BackgroundEditBox.class).getValue();
        Optional<ResourceLocation> customTexture = customTextureLoc.isBlank() ? Optional.empty() : Optional.of(ResourceLocation.parse(customTextureLoc));
        DisplayImpl display = ClientEditorHelper.createDisplay(
                layout.getChild("name_edit_box", BackgroundEditBox.class),
                layout.getChild("desc_edit_box", BackgroundEditBox.class)
        );
        return new ResearchPackImpl(
                color,
                sortingValue,
                customTexture,
                display
        );
    }

    @Override
    public ResourceLocation createId(RememberingLinearLayout layout) {
        return null;
    }

    @Override
    public Result<Unit, Exception> valid(RememberingLinearLayout layout) {
        boolean nameEditBoxNotEmpty = !layout.getChild("name_edit_box", BackgroundEditBox.class).getValue().isEmpty();
        if (!nameEditBoxNotEmpty) {
            return Result.err("Research Pack needs a name");
        }

        return Result.ok(Unit.INSTANCE);
    }
}
