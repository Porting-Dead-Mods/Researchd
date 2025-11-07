package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.EditModeSettings;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.impl.editor.EditModeSettingsImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.function.Consumer;

public class SelectPackPopupWidget extends PopupWidget {
    public static final ResourceLocation SPRITE = Researchd.rl("widget/editor_popup");
    private static final WidgetSprites EDITOR_BUTTON_SPRITES = new WidgetSprites(
            Researchd.rl("editor_button"),
            Researchd.rl("editor_button_disabled"),
            Researchd.rl("editor_button_highlighted")
    );

    private final LinearLayout layout;
    private final ResearchScreen screen;
    private EditModeSettings editModeSettings;

    public SelectPackPopupWidget(ResearchScreen screen, EditModeSettings settings) {
        this(0, 0, screen, settings);
    }

    public SelectPackPopupWidget(int x, int y, ResearchScreen screen, EditModeSettings settings) {
        super(x, y, 256, 192, CommonComponents.EMPTY);
        this.editModeSettings = settings;
        this.screen = screen;

        boolean canStartEditing = this.editModeSettings.currentDatapack() != null && this.editModeSettings.currentResourcePack() != null;

        Font font = Minecraft.getInstance().font;

        this.layout = LinearLayout.vertical();
        FrameLayout header = new FrameLayout(this.width, 15);
        LinearLayout headerLayout = header.addChild(LinearLayout.vertical());
        headerLayout.addChild(new StringWidget(Component.literal("Select or Create Pack"), font), LayoutSettings::alignHorizontallyCenter);
        this.layout.addChild(header);

        FrameLayout contents = new FrameLayout(this.width, 155);
        LinearLayout contentsLayout = contents.addChild(LinearLayout.vertical().spacing(2));
        MultiLineTextWidget introductionTextWidget = contentsLayout.addChild(new MultiLineTextWidget(Component.literal("To start creating Researches and Research Packs, you need to first select or create a data- and resource pack where everything will be stored to").withColor(FastColor.ARGB32.color(125, 110, 77)), font));
        introductionTextWidget.setMaxWidth(192);
        introductionTextWidget.setMaxRows(5);
        contentsLayout.addChild(new SpacerElement(0, 4));
        contentsLayout.addChild(PDLButton.builder(this::onCreateNewProjectPressed)
                .message(Component.literal("Create new project"))
                .sprites(EDITOR_BUTTON_SPRITES)
                .size(128, 16)
                .build(), LayoutSettings::alignHorizontallyCenter);
        contentsLayout.addChild(new SpacerElement(0, 4));
        contentsLayout.addChild(new StringWidget(Component.literal("Datapack:").withStyle(ChatFormatting.WHITE), font), LayoutSettings::alignHorizontallyCenter);
        contentsLayout.addChild(new SelectPackSearchBarWidget(this.editModeSettings.currentDatapack()), LayoutSettings::alignHorizontallyCenter);
        contentsLayout.addChild(new StringWidget(Component.literal("Resource Pack:").withStyle(ChatFormatting.WHITE), font), LayoutSettings::alignHorizontallyCenter);
        LinearLayout linearLayout = contentsLayout.addChild(new SelectPackSearchBarWidget(this.editModeSettings.currentResourcePack()), LayoutSettings::alignHorizontallyCenter);
        this.layout.addChild(contents);

        FrameLayout footer = new FrameLayout(this.width, 22);
        LinearLayout footerLayout = footer.addChild(LinearLayout.vertical());
        PDLButton startEditingButton = footerLayout.addChild(PDLButton.builder(this::onStartEditingPressed)
                .message(Component.literal("Start Editing"))
                .tooltip(Tooltip.create(canStartEditing ? Component.literal("Start Editing") : Component.literal("Both Paths need to be filled in")))
                .sprites(EDITOR_BUTTON_SPRITES)
                .size(128, 16)
                .build(), s -> s.alignHorizontallyCenter().alignVerticallyBottom());
        this.layout.addChild(footer);

        this.layout.arrangeElements();
        FrameLayout.alignInRectangle(header, x, y + 3, this.width, 11, 0.5f, 0.25f);
        FrameLayout.alignInRectangle(footer, x, footer.getY() - 1, this.width, 20, 0.5f, 0.25f);

        startEditingButton.active = canStartEditing;

    }

    private void onCreateNewProjectPressed(PDLButton button) {
        this.screen.openPopupCentered(new CreatePackPopupWidget());
    }

    private void onStartEditingPressed(PDLButton button) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.layout.setX(x);
        //FrameLayout.alignInRectangle(this.layout, x, y, this.width, this.height, 0.5F, 0.25F);
        //FrameLayout.alignInDimension(x, width, this.layout.getWidth(), this.layout::setX, 0.5f);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.layout.setY(y);
        //FrameLayout.alignInRectangle(this.layout, x, y, this.width, this.height, 0.5F, 0.25F);
        //FrameLayout.alignInDimension(y, height, this.layout.getHeight(), this.layout::setY, 0.5f);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        this.layout.visitWidgets(consumer);
    }
}
