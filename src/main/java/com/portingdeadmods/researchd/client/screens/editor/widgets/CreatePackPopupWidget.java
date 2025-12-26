package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.portingdeadlibs.utils.Result;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.lib.layout.WidgetHeaderAndFooterLayout;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.networking.edit.CreateDatapackPayload;
import com.portingdeadmods.researchd.networking.edit.SetResourcePackPayload;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
import com.portingdeadmods.researchd.utils.PrettyPath;
import com.portingdeadmods.researchd.utils.ResearchdUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CreatePackPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation SPRITE = Researchd.rl("widget/pack_creation_popup");

    private final WidgetHeaderAndFooterLayout layout;
    private final ResearchScreen screen;
    private final PackType packType;

    private EditBox nameEditBox;
    private MultiLineEditBox descEditBox;
    private Checkbox checkbox;
    private PDLButton createPackButton;

    public CreatePackPopupWidget(ResearchScreen screen, PackType packType) {
        super(0, 0, 160, 176, CommonComponents.EMPTY);
        this.screen = screen;
        this.packType = packType;
        this.layout = new WidgetHeaderAndFooterLayout(this.width, 15, 139, 19);

        this.layout.withHeader(header -> {
            header.addChild(new StringWidget(Component.literal("Create Pack"), PopupWidget.getFont()));
        });
        this.layout.withContents(contents -> {
            contents.spacing(4);
            this.nameEditBox = contents.addChild(new EditBox(PopupWidget.getFont(), 128, 16, Component.literal("Slaaay")));
            this.nameEditBox.setHint(Component.literal("<Pack Name>"));
            this.nameEditBox.setResponder(val -> this.onNameChanged(this.nameEditBox, val));
            this.descEditBox = contents.addChild(new MultiLineEditBox(PopupWidget.getFont(), 0, 0, 128, 80, Component.literal("<Pack Description>"), Component.literal("msg")));
            //this.descEditBox.setValueListener(val -> this.onValueChanged(this.descEditBox, val));
            this.checkbox = contents.addChild(Checkbox.builder(Component.literal("Generate Examples"), getFont()).build());
        });
        this.layout.withFooter(footer -> {
            footer.defaultCellSetting().paddingBottom(16);
            this.createPackButton = footer.addChild(PDLButton.builder(this::createPackPressed)
                    .message(Component.literal("Create Pack"))
                    .tooltip(Tooltip.create(Component.literal("Pack name cannot be empty")))
                    .sprites(SelectPackPopupWidget.EDITOR_BUTTON_SPRITES)
                    .size(128, 17)
                    .build(), LayoutSettings::alignHorizontallyCenter);
            this.createPackButton.active = false;
        });

        this.layout.arrangeElements();
    }

    private void onNameChanged(AbstractWidget widget, String val) {
        this.createPackButton.active = !this.nameEditBox.getValue().isEmpty();
        this.createPackButton.setTooltip(Tooltip.create(this.createPackButton.active ? Component.empty() : Component.literal("Pack name cannot be empty")));
    }

    private void createPackPressed(PDLButton button) {
        String name = this.nameEditBox.getValue();
        String description = this.descEditBox.getValue();
        boolean generateExamples = this.checkbox.selected();

        if (this.packType == PackType.SERVER_DATA) {
            PacketDistributor.sendToServer(new CreateDatapackPayload(name, description, generateExamples));
        } else if (this.packType == PackType.CLIENT_RESOURCES) {
            Result<PrettyPath, Exception> resourcePack = ClientEditorHelper.createResourcePack(name, description, ResearchdUtils.trimSpecialCharacterAndConvertToSnake(name), generateExamples);
            if (resourcePack instanceof Result.Ok(PrettyPath value)) {
                PacketDistributor.sendToServer(new SetResourcePackPayload(value));
            }
        }
        this.screen.closePopup(this);
    }

    @Override
    public @Nullable WidgetHeaderAndFooterLayout getLayout() {
        return this.layout;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blitSprite(SPRITE, this.getX(), this.getY(), this.width, this.height);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        this.layout.visitWidgets(consumer);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.getLayout() != null) {
            for (AbstractWidget child : this.getLayout().getChildren()) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    this.screen.setFocused(child);
                    if (button == 0) {
                        this.screen.setDragging(true);
                    }
                    return true;
                }
            }
        }
        //return false;
        if (super.mouseClicked(mouseX, mouseY, button)) {
            this.screen.setFocused(this);
            if (button == 0) {
                this.screen.setDragging(true);
            }
            return true;
        }
        return false;
    }

}
