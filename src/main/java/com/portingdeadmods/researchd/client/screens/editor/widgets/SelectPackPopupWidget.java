package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.EditModeSettings;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
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
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.FastColor;

import java.util.function.Consumer;

public class SelectPackPopupWidget extends PopupWidget {
    public static final ResourceLocation SPRITE = Researchd.rl("widget/editor_popup");
    public static final WidgetSprites EDITOR_BUTTON_SPRITES = new WidgetSprites(
            Researchd.rl("editor_button"),
            Researchd.rl("editor_button_disabled"),
            Researchd.rl("editor_button_highlighted")
    );
    public static final String INTRODUCTION_TEXT = "To start creating Researches and Research Packs, you need to first select or create a data- and resource pack where everything will be stored to";

    private final WidgetHeaderAndFooterLayout layout;
    private final ResearchScreen screen;
    private SelectPackSearchBarWidget selectDatapackWidget;
    private EditModeSettings editModeSettings;
    private final PackRepository repository;

    public SelectPackPopupWidget(ResearchScreen screen, EditModeSettings settings) {
        this(0, 0, screen, settings);
    }

    public SelectPackPopupWidget(int x, int y, ResearchScreen screen, EditModeSettings settings) {
        super(x, y, 256, 192, CommonComponents.EMPTY);
        this.editModeSettings = settings;
        this.screen = screen;
        this.repository = new PackRepository(new ServerPacksSource(Minecraft.getInstance().directoryValidator()));
        this.repository.reload();

        boolean canStartEditing = this.editModeSettings.currentDatapack() != null && this.editModeSettings.currentResourcePack() != null;

        this.layout = new WidgetHeaderAndFooterLayout(this.width, 15, 155, 22);
        this.layout.withHeader(header -> {
            header.defaultCellSetting().paddingTop(1);
            header.addChild(new StringWidget(Component.literal("Select or Create Pack"), PopupWidget.getFont()), LayoutSettings::alignHorizontallyCenter);
        });

        this.layout.withContents(contents -> {
            contents.spacing(2);
            Font font = PopupWidget.getFont();
            MultiLineTextWidget introductionTextWidget = contents.addChild(new MultiLineTextWidget(Component.literal(INTRODUCTION_TEXT).withColor(FastColor.ARGB32.color(125, 110, 77)), font));
            introductionTextWidget.setMaxWidth(192);
            introductionTextWidget.setMaxRows(5);
            contents.addChild(new SpacerElement(0, 4));
            contents.addChild(PDLButton.builder(this::onCreateNewProjectPressed)
                    .message(Component.literal("Create new project"))
                    .sprites(EDITOR_BUTTON_SPRITES)
                    .size(128, 16)
                    .build(), LayoutSettings::alignHorizontallyCenter);
            contents.addChild(new SpacerElement(0, 4));
            contents.addChild(new StringWidget(Component.literal("Datapack:").withStyle(ChatFormatting.WHITE), font), LayoutSettings::alignHorizontallyCenter);
            this.selectDatapackWidget = contents.addChild(new SelectPackSearchBarWidget(this.editModeSettings.currentDatapack(), select_btn -> {
                this.dropDownFor(this.selectDatapackWidget);
            }, create_btn -> {
            }), LayoutSettings::alignHorizontallyCenter);
            this.attachDropDown(selectDatapackWidget, new SelectPackDropDownWidget(this.repository));
            contents.addChild(new StringWidget(Component.literal("Resource Pack:").withStyle(ChatFormatting.WHITE), font), LayoutSettings::alignHorizontallyCenter);
            contents.addChild(new SelectPackSearchBarWidget(this.editModeSettings.currentResourcePack(), select_btn -> {}, create_btn -> {}), LayoutSettings::alignHorizontallyCenter);
        });

        this.layout.withFooter(footer -> {
            footer.defaultCellSetting().paddingBottom(16);
            PDLButton startEditingButton = footer.addChild(PDLButton.builder(this::onStartEditingPressed)
                    .message(Component.literal("Start Editing"))
                    .tooltip(Tooltip.create(canStartEditing ? Component.literal("Start Editing") : Component.literal("Both Paths need to be filled in")))
                    .sprites(EDITOR_BUTTON_SPRITES)
                    .size(128, 17)
                    .build(), s -> s.alignHorizontallyCenter().alignVerticallyBottom());
            startEditingButton.active = canStartEditing;
        });

        this.layout.arrangeElements();

    }

    private void onCreateNewProjectPressed(PDLButton button) {
        this.screen.openPopupCentered(new CreatePackPopupWidget(this.screen));
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
        this.layout.arrangeElements();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.layout.setY(y);
        this.layout.arrangeElements();
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
