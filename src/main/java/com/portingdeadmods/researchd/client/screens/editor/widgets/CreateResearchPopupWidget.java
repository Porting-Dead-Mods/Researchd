package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.lib.layout.WidgetHeaderAndFooterLayout;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.impl.research.ItemResearchIcon;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CreateResearchPopupWidget extends DraggablePopupWidget {
    public static final ResourceLocation SPRITE = Researchd.rl("widget/pack_creation_popup");
    private final WidgetHeaderAndFooterLayout layout;
    private final ResearchScreen screen;

    private ItemSelectorWidget iconSelector;
    private EditBox researchNameEditBox;
    private MultiLineEditBox researchDescEditBox;
    private ResearchSelectorWidget parentSelector;
    private Checkbox requiresParents;

    private PDLButton createResearchButton;

    public CreateResearchPopupWidget(ResearchScreen screen) {
        super(0, 0, 160, 176, CommonComponents.EMPTY);
        this.screen = screen;
        this.layout = new WidgetHeaderAndFooterLayout(this.width, 15, 139, 19);

//        this.layout.withHeader(header -> {
//            header.addChild(new StringWidget(Component.literal("Create Research"), PopupWidget.getFont()));
//        });
//        this.layout.withContents(contents -> {
//            contents.spacing(2);
//            contents.addChild(new StringWidget(Component.literal("Icon:"), PopupWidget.getFont()));
//            this.iconSelector = contents.addChild(new ItemSelectorWidget(0, 0, CommonComponents.EMPTY));
//            this.researchNameEditBox = contents.addChild(new EditBox(PopupWidget.getFont(), 128, 16, Component.literal("Slaaay")));
//            this.researchNameEditBox.setHint(Component.literal("<Research Name>"));
//            this.researchDescEditBox = contents.addChild(new MultiLineEditBox(PopupWidget.getFont(), 0, 0, 128, 64, Component.literal("<Research Description>"), CommonComponents.EMPTY));
//            this.parentSelector = contents.addChild(new ResearchSelectorWidget(0, 0, 0, 0, CommonComponents.EMPTY));
//            this.requiresParents = contents.addChild(Checkbox.builder(Component.literal("Requires Parents"), PopupWidget.getFont()).build());
//        });
//        this.layout.withFooter(footer -> {
//            footer.defaultCellSetting().paddingBottom(16);
//
//             this.createResearchButton = footer.addChild(PDLButton.builder(this::createResearchPressed)
//                    .message(Component.literal("Create Research"))
//                    .sprites(SelectPackPopupWidget.EDITOR_BUTTON_SPRITES)
//                    .size(128, 17)
//                    .build());
//            this.createResearchButton.active = false;
//        });
//
//        this.layout.arrangeElements();
    }

    private void createResearchPressed(PDLButton pdlButton) {

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

    public Research buildResearch() {
        return new SimpleResearch(
                new ItemResearchIcon(this.iconSelector.getSelected()),
                null,
                null,
                List.of(),
                this.requiresParents.selected(),
                new DisplayImpl(Optional.of(
                        Component.literal(this.researchNameEditBox.getValue())
                ), Optional.of(
                        Component.literal(this.researchDescEditBox.getValue())
                ))
        );
    }

}
