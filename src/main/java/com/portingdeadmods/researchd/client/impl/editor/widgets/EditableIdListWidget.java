package com.portingdeadmods.researchd.client.impl.editor.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.RegistryVerifyEditBox;
import com.portingdeadmods.researchd.utils.GuiUtils;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class EditableIdListWidget extends ContainerWidget<EditableIdListWidget.Element> {
    private Element focusedElement;
    private UniqueArray<Element> items;
    /** The valid ids, or null to accept any well-formed id */
    private final @Nullable Collection<Identifier> ids;

    private final Consumer<String> editBoxResponder;

    public EditableIdListWidget(
            int width, int height, @Nullable Collection<Identifier> ids, Consumer<String> editBoxResponder) {
        super(
                width,
                height,
                72,
                16,
                Orientation.VERTICAL,
                1,
                ids != null ? ids.size() : Math.ceilDiv(height, 16),
                List.of(),
                false);
        this.ids = ids;
        this.editBoxResponder = editBoxResponder;
        // setItems(List.of(new Element.SimpleElement(this, ids, 72, 16), new Element.SelectorElement()));
        this.items = new UniqueArray<>();
        this.addItem(new Element.SimpleElement(this, ids, 72, 16));
        this.getItems().add(Element.SelectorElement.INSTANCE);
    }

    public Stream<String> getIds() {
        return this.getItems().stream()
                .filter(Element.SimpleElement.class::isInstance)
                .map(e -> ((Element.SimpleElement) e).idEditBox.getValue());
    }

    @Override
    public UniqueArray<Element> getItems() {
        return items;
    }

    @Override
    public void setItems(Collection<Element> items) {
        super.setItems(items);
        this.items = new UniqueArray<>(items);
    }

    public void addItem(Element item) {
        if (!this.getItems().isEmpty()) {
            this.getItems().set(this.getItems().size() - 1, item);
            this.getItems().addLast(Element.SelectorElement.INSTANCE);
        } else {
            this.getItems().add(item);
            this.getItems().add(Element.SelectorElement.INSTANCE);
        }
    }

    @Override
    public void clickedItem(
            EditableIdListWidget.Element item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
        if (item instanceof Element.SimpleElement) {
            item.clicked(mouseX, mouseY);
        } else {
            this.addItem(new Element.SimpleElement(this, ids, 72, 16));
        }
    }

    @Override
    protected void internalRenderItem(
            GuiGraphicsExtractor guiGraphics,
            EditableIdListWidget.Element item,
            int xIndex,
            int yIndex,
            int left,
            int top,
            int mouseX,
            int mouseY) {
        item.render(
                guiGraphics,
                left,
                top,
                this.getItemWidth(),
                this.getItemHeight(),
                this.isItemHovered(xIndex, yIndex, mouseX, mouseY),
                mouseX,
                mouseY,
                0);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.focusedElement != null) {
            this.focusedElement.keyPressed(event);
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.focusedElement != null) {
            this.focusedElement.charTyped(event);
        }
        return super.charTyped(event);
    }

    public sealed interface Element
            permits EditableIdListWidget.Element.SimpleElement, EditableIdListWidget.Element.SelectorElement {
        WidgetSprites SPRITES =
                new WidgetSprites(Researchd.rl("editor_background"), Researchd.rl("editor_background_highlighted"));

        void render(
                GuiGraphicsExtractor guiGraphics,
                int x,
                int y,
                int width,
                int height,
                boolean hovered,
                int mouseX,
                int mouseY,
                float partialTick);

        void clicked(int mouseX, int mouseY);

        boolean isValid();

        default void keyPressed(KeyEvent event) {}

        default void charTyped(CharacterEvent event) {}

        final class SimpleElement implements Element {
            public static final Identifier REMOVE_ELEMENT_HOVER_SPRITE = Researchd.rl("remove_element_hover");
            private final RegistryVerifyEditBox idEditBox;
            private EditableIdListWidget parentWidget;

            public SimpleElement(
                    EditableIdListWidget parentWidget,
                    @Nullable Collection<Identifier> ids,
                    int itemWidth,
                    int itemHeight) {
                this.parentWidget = parentWidget;
                this.idEditBox = new RegistryVerifyEditBox(
                        GuiUtils.getFont(),
                        BackgroundEditBox.SPRITES,
                        null,
                        ids,
                        itemWidth,
                        itemHeight,
                        CommonComponents.EMPTY);
                this.idEditBox.setResponder(this.parentWidget.editBoxResponder);
            }

            public SimpleElement(RegistryVerifyEditBox idEditBox) {
                this.idEditBox = idEditBox;
            }

            @Override
            public void render(
                    GuiGraphicsExtractor guiGraphics,
                    int x,
                    int y,
                    int width,
                    int height,
                    boolean hovered,
                    int mouseX,
                    int mouseY,
                    float partialTick) {
                this.idEditBox.setPosition(x, y);
                this.idEditBox.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

                //                if (hovered) {
                //                    PoseStack poseStack = guiGraphics.pose();
                //                    poseStack.pushPose();
                //                    {
                //                        poseStack.translate(0, 0, 160);
                //                        guiGraphics.blitSprite(REMOVE_ELEMENT_HOVER_SPRITE, x + 2, y + 2, 14, 14);
                //                    }
                //                    poseStack.popPose();
                //                }
            }

            @Override
            public boolean isValid() {
                return this.idEditBox.isValid();
            }

            @Override
            public void clicked(int mouseX, int mouseY) {
                this.parentWidget.setFocusedElement(this);
                this.idEditBox.mouseClicked(
                        new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(InputConstants.MOUSE_BUTTON_LEFT, 0)),
                        false);
            }

            @Override
            public void keyPressed(KeyEvent event) {
                this.idEditBox.keyPressed(event);
            }

            @Override
            public void charTyped(CharacterEvent event) {
                this.idEditBox.charTyped(event);
            }
        }

        final class SelectorElement implements EditableIdListWidget.Element {
            public static final WidgetSprites SPRITES = new WidgetSprites(
                    Researchd.rl("editor_new_id_editbox"), Researchd.rl("editor_new_id_editbox_highlighted"));
            public static final SelectorElement INSTANCE = new SelectorElement();

            private SelectorElement() {}

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public void render(
                    GuiGraphicsExtractor guiGraphics,
                    int x,
                    int y,
                    int width,
                    int height,
                    boolean hovered,
                    int mouseX,
                    int mouseY,
                    float partialTick) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(true, hovered), x, y, width, height);
            }

            @Override
            public void clicked(int mouseX, int mouseY) {}
        }
    }

    private void setFocusedElement(Element element) {
        for (Element elem : this.getItems()) {
            if (elem instanceof Element.SimpleElement simpleElement) {
                if (element == elem) {
                    simpleElement.idEditBox.setFocused(true);
                    this.focusedElement = element;
                } else {
                    simpleElement.idEditBox.setFocused(false);
                }
            }
        }
    }
}
