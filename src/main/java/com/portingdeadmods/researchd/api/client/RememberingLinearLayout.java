package com.portingdeadmods.researchd.api.client;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;

import java.util.*;
import java.util.function.Consumer;

public final class RememberingLinearLayout {
    private final LinearLayout layout;
    private final Map<String, AbstractWidget> widgets;
    private final List<AbstractWidget> children;

    public RememberingLinearLayout(LinearLayout layout) {
        this.layout = layout;
        this.widgets = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public <W extends AbstractWidget> W getChild(String id, Class<W> clazz) {
        AbstractWidget widget = this.widgets.get(id);
        if (clazz.isInstance(widget)) {
            return clazz.cast(widget);
        }
        return null;
    }

    public <T extends LayoutElement> T addChild(T element) {
        return this.getLayout().addChild(element);
    }

    public <T extends LayoutElement> T addChild(T element, Consumer<LayoutSettings> layoutSettingsFactory) {
        return this.getLayout().addChild(element, layoutSettingsFactory);
    }

    public <W extends AbstractWidget> W addWidget(String id, W child) {
        if (id != null) {
            this.widgets.put(id, child);
        }
        this.children.add(child);
        this.getLayout().addChild(child);
        return child;
    }

    public <W extends AbstractWidget> W addWidget(String id, W child, Consumer<LayoutSettings> layoutSettingsFactory) {
        if (id != null) {
            this.widgets.put(id, child);
        }
        this.children.add(child);
        this.getLayout().addChild(child, layoutSettingsFactory);
        return child;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public List<AbstractWidget> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RememberingLinearLayout) obj;
        return Objects.equals(this.layout, that.layout) &&
                Objects.equals(this.widgets, that.widgets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layout, widgets);
    }

    @Override
    public String toString() {
        return "RememberingLinearLayout[" +
                "layout=" + layout + ", " +
                "widgets=" + widgets + ']';
    }


}
