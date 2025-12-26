package com.portingdeadmods.researchd.client.screens.editor.widgets.popups;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.EditModeSettings;
import com.portingdeadmods.researchd.client.screens.editor.widgets.SelectPackDropDownWidget;
import com.portingdeadmods.researchd.client.screens.editor.widgets.SelectPackSearchBarWidget;
import com.portingdeadmods.researchd.client.screens.lib.layout.WidgetHeaderAndFooterLayout;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.utils.ClientEditorHelper;
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
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

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
    private PDLButton startEditingButton;
    private SelectPackSearchBarWidget selectResourcePackWidget;
    private SelectPackSearchBarWidget selectDatapackWidget;
    private final PackRepository repository;

    public SelectPackPopupWidget(ResearchScreen screen) {
        this(0, 0, screen);
    }

    public SelectPackPopupWidget(int x, int y, ResearchScreen screen) {
        super(x, y, 256, 192, CommonComponents.EMPTY);
        EditModeSettings settings = ClientEditorHelper.getEditModeSettings();
        this.screen = screen;
        this.repository = new PackRepository(new ServerPacksSource(Minecraft.getInstance().directoryValidator()));
        this.repository.reload();

        boolean canStartEditing = settings.currentDatapack() != null && settings.currentResourcePack() != null;

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
            this.selectDatapackWidget = contents.addChild(new SelectPackSearchBarWidget(ClientEditorHelper.getEditModeSettings().currentDatapack(), select_btn -> {
                this.dropDownFor(this.selectDatapackWidget);
            }, create_btn -> {
                this.screen.openPopupCentered(new CreatePackPopupWidget(this.screen, PackType.SERVER_DATA));
            }), LayoutSettings::alignHorizontallyCenter);
            this.attachDropDown(selectDatapackWidget, new SelectPackDropDownWidget(this.repository));
            contents.addChild(new StringWidget(Component.literal("Resource Pack:").withStyle(ChatFormatting.WHITE), font), LayoutSettings::alignHorizontallyCenter);
            this.selectResourcePackWidget = contents.addChild(new SelectPackSearchBarWidget(ClientEditorHelper.getEditModeSettings().currentResourcePack(), select_btn -> {}, create_btn -> {
                this.screen.openPopupCentered(new CreatePackPopupWidget(this.screen, PackType.CLIENT_RESOURCES));
            }), LayoutSettings::alignHorizontallyCenter);
        });

        this.layout.withFooter(footer -> {
            footer.defaultCellSetting().paddingBottom(20);
            this.startEditingButton = footer.addChild(PDLButton.builder(this::onStartEditingPressed)
                    .message(Component.literal("Start Editing"))
                    .tooltip(Tooltip.create(canStartEditing ? CommonComponents.EMPTY : Component.literal("Both Paths need to be filled in")))
                    .sprites(EDITOR_BUTTON_SPRITES)
                    .size(128, 17)
                    .build(), s -> s.alignHorizontallyCenter().alignVerticallyBottom());
            this.startEditingButton.active = canStartEditing;
        });

        this.layout.arrangeElements();

    }

    @Override
    public WidgetHeaderAndFooterLayout getLayout() {
        return layout;
    }

    private void onCreateNewProjectPressed(PDLButton button) {
        this.screen.openPopupCentered(new CreatePackPopupWidget(this.screen, PackType.SERVER_DATA));
    }

    private void onStartEditingPressed(PDLButton button) {
        this.screen.closePopup(this);
        this.screen.setFocused(null);
        this.screen.setEditorOpen(false);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.selectDatapackWidget.updateSearchBarText(ClientEditorHelper.getEditModeSettings().currentDatapack());
        this.selectResourcePackWidget.updateSearchBarText(ClientEditorHelper.getEditModeSettings().currentResourcePack());

        if (this.selectResourcePackWidget.hasPack() && this.selectDatapackWidget.hasPack()) {
            this.startEditingButton.active = true;
            this.startEditingButton.setTooltip(Tooltip.create(CommonComponents.EMPTY));
        } else {
            this.startEditingButton.setTooltip(Tooltip.create(Component.literal("Both Paths need to be filled in")));
        }

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
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        this.layout.visitWidgets(consumer);
    }
}
