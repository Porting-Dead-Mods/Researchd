package com.portingdeadmods.researchd.client.impl.editor.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.editor.widgets.RegistryVerifyEditBox;
import com.portingdeadmods.researchd.client.screens.editor.widgets.ResearchSelectorListWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ContainerWidget;
import com.portingdeadmods.researchd.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EditableIdListWidget extends ContainerWidget<EditableIdListWidget.Element> {
    private Element focusedElement;
    private UniqueArray<Element> items;
    private final Collection<ResourceLocation> ids;
    private final Consumer<String> editBoxResponder;

    public EditableIdListWidget(int width, int height, Collection<ResourceLocation> ids, Consumer<String> editBoxResponder) {
        super(width, height, 72, 16, Orientation.VERTICAL, 1, ids.size(), List.of(), false);
        this.ids = ids;
        this.editBoxResponder = editBoxResponder;
        //setItems(List.of(new Element.SimpleElement(this, ids, 72, 16), new Element.SelectorElement()));
        this.items = new UniqueArray<>();
        this.addItem(new Element.SimpleElement(this, ids, 72, 16));
        this.getItems().add(Element.SelectorElement.INSTANCE);
    }

    public Stream<String> getIds() {
        return this.getItems().stream().filter(Element.SimpleElement.class::isInstance).map(e -> ((Element.SimpleElement)e).idEditBox.getValue());
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
    public void clickedItem(EditableIdListWidget.Element item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
        if (item instanceof Element.SimpleElement) {
            item.clicked(mouseX, mouseY);
        } else {
            this.addItem(new Element.SimpleElement(this, ids, 72, 16));
        }
    }

    @Override
    protected void internalRenderItem(GuiGraphics guiGraphics, EditableIdListWidget.Element item, int xIndex, int yIndex, int left, int top, int mouseX, int mouseY) {
        item.render(guiGraphics, left, top, this.getItemWidth(), this.getItemHeight(), this.isItemHovered(xIndex, yIndex, mouseX, mouseY), mouseX, mouseY, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focusedElement != null) {
            this.focusedElement.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.focusedElement != null) {
            this.focusedElement.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    public sealed interface Element permits EditableIdListWidget.Element.SimpleElement, EditableIdListWidget.Element.SelectorElement {
        WidgetSprites SPRITES = new WidgetSprites(Researchd.rl("editor_background"), Researchd.rl("editor_background_highlighted"));

        void render(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, int mouseX, int mouseY, float partialTick);

        void clicked(int mouseX, int mouseY);

        boolean isValid();

        default void keyPressed(int keyCode, int scanCode, int modifiers) {
        }

        default void charTyped(char codePoint, int modifiers) {
        }

        final class SimpleElement implements Element {
            public static final ResourceLocation REMOVE_ELEMENT_HOVER_SPRITE = Researchd.rl("remove_element_hover");
            private final RegistryVerifyEditBox idEditBox;
            private EditableIdListWidget parentWidget;

            public SimpleElement(EditableIdListWidget parentWidget, Collection<ResourceLocation> ids, int itemWidth, int itemHeight) {
                this.parentWidget = parentWidget;
                this.idEditBox = new RegistryVerifyEditBox(GuiUtils.getFont(), BackgroundEditBox.SPRITES, null, ids, itemWidth, itemHeight, CommonComponents.EMPTY);
                this.idEditBox.setResponder(this.parentWidget.editBoxResponder);
            }

            public SimpleElement(RegistryVerifyEditBox idEditBox) {
                this.idEditBox = idEditBox;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, int mouseX, int mouseY, float partialTick) {
                this.idEditBox.setPosition(x, y);
                this.idEditBox.render(guiGraphics, mouseX, mouseY, partialTick);

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
                this.idEditBox.mouseClicked(mouseX, mouseY, 0);
            }

            @Override
            public void keyPressed(int keyCode, int scanCode, int modifiers) {
                this.idEditBox.keyPressed(keyCode, scanCode, modifiers);
            }

            @Override
            public void charTyped(char codePoint, int modifiers) {
                this.idEditBox.charTyped(codePoint, modifiers);
            }

        }

        final class SelectorElement implements EditableIdListWidget.Element {
            public static final WidgetSprites SPRITES = new WidgetSprites(Researchd.rl("editor_new_id_editbox"), Researchd.rl("editor_new_id_editbox_highlighted"));
            public static final SelectorElement INSTANCE = new SelectorElement();

            private SelectorElement() {
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean hovered, int mouseX, int mouseY, float partialTick) {
                guiGraphics.blitSprite(SPRITES.get(true, hovered), x, y, width, height);
            }

            @Override
            public void clicked(int mouseX, int mouseY) {

            }
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
