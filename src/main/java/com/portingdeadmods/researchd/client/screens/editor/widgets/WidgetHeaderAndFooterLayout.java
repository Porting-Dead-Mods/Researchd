package com.portingdeadmods.researchd.client.screens.editor.widgets;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WidgetHeaderAndFooterLayout implements Layout {
    private final LinearLayout wrapped;
    private final LinearLayout header;
    private final LinearLayout contents;
    private final LinearLayout footer;
    private final int width;
    private final int headerHeight;
    private final int contentsHeight;
    private final int footerHeight;
    private final List<AbstractWidget> children;

    public WidgetHeaderAndFooterLayout(int width, int headerHeight, int contentsHeight, int footerHeight) {
        this.children = new ArrayList<>();

        this.width = width;
        this.headerHeight = headerHeight;
        this.contentsHeight = contentsHeight;
        this.footerHeight = footerHeight;
        this.wrapped = LinearLayout.vertical();
        FrameLayout header = this.wrapped.addChild(new FrameLayout(width, headerHeight));
        this.header = header.addChild(LinearLayout.vertical());
        FrameLayout contents = this.wrapped.addChild(new FrameLayout(width, contentsHeight));
        this.contents = contents.addChild(LinearLayout.vertical());
        FrameLayout footer = this.wrapped.addChild(new FrameLayout(width, footerHeight));
        this.footer = footer.addChild(LinearLayout.vertical());

        this.updateChildren();
    }

    public LinearLayout withHeader(Consumer<LinearLayout> layoutConsumer) {
        layoutConsumer.accept(this.header);
        this.updateChildren();
        return this.header;
    }

    public LinearLayout withContents(Consumer<LinearLayout> layoutConsumer) {
        layoutConsumer.accept(this.contents);
        this.updateChildren();
        return this.contents;
    }

    public LinearLayout withFooter(Consumer<LinearLayout> layoutConsumer) {
        layoutConsumer.accept(this.footer);
        this.updateChildren();
        return this.footer;
    }

    @Override
    public void visitChildren(@NotNull Consumer<LayoutElement> visitor) {
        this.wrapped.visitChildren(visitor);
    }

    private void updateChildren() {
        this.children.clear();
        this.visitWidgets(this.children::add);
    }

    @Override
    public void arrangeElements() {
        Layout.super.arrangeElements();

        FrameLayout.alignInRectangle(this.header, this.header.getX(), this.header.getY(), this.header.getWidth(), this.headerHeight, 0.5f, 0.25f);
        FrameLayout.alignInRectangle(this.contents, this.contents.getX(), this.header.getY() + this.headerHeight, this.contents.getWidth(), this.contentsHeight, 0.5f, 0.25f);
        FrameLayout.alignInRectangle(this.footer, this.footer.getX(), this.header.getY() + this.headerHeight + this.contentsHeight, this.footer.getWidth(), this.footerHeight, 0.5f, 0.25f);

    }

    public List<AbstractWidget> getChildren() {
        return children;
    }

    @Override
    public void setX(int x) {
        this.wrapped.setX(x);
    }

    @Override
    public void setY(int y) {
        this.wrapped.setY(y);
    }

    @Override
    public int getX() {
        return this.wrapped.getX();
    }

    @Override
    public int getY() {
        return this.wrapped.getY();
    }

    @Override
    public int getWidth() {
        return this.wrapped.getWidth();
    }

    @Override
    public int getHeight() {
        return this.wrapped.getHeight();
    }

}
