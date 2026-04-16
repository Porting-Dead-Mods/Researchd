package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.client.screens.lib.widgets.BackgroundEditBox;
import com.portingdeadmods.researchd.client.screens.lib.widgets.PopupWidget;
import com.portingdeadmods.researchd.utils.GuiUtils;
import com.portingdeadmods.researchd.utils.TextUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class RegistryVerifyEditBox extends BackgroundEditBox {
    private final @Nullable Registry<?> registry;
    private final @Nullable Collection<ResourceLocation> ids;

    public RegistryVerifyEditBox(Font font, WidgetSprites sprites, @Nullable Registry<?> registry, @Nullable Collection<ResourceLocation> ids, int width, int height, Component message) {
        super(font, sprites, width, height, "");
        this.registry = registry;
        this.ids = ids;
        this.setFilter(this::isFilterValid);
    }

    private boolean isFilterValid(String s) {
        return TextUtils.isValidResourceLocation(s);
    }

    public static RegistryVerifyEditBox forRegistry(Registry<?> registry, int width, int height) {
        Objects.requireNonNull(registry);
        return new RegistryVerifyEditBox(GuiUtils.getFont(), BackgroundEditBox.SPRITES, registry, null, width, height, CommonComponents.EMPTY);
    }

    public static RegistryVerifyEditBox forIds(Collection<ResourceLocation> ids, int width, int height) {
        Objects.requireNonNull(ids);
        return new RegistryVerifyEditBox(GuiUtils.getFont(), BackgroundEditBox.SPRITES, null, ids, width, height, CommonComponents.EMPTY);
    }

    public @Nullable Registry<?> getRegistry() {
        return registry;
    }

    public @Nullable Collection<ResourceLocation> getIds() {
        return ids;
    }

    @Override
    public void onValueChangedExtra(String newText) {
        ResourceLocation id = ResourceLocation.parse(newText);
        if (!this.isValid(id)) {
            this.setTextColor(FastColor.ARGB32.color(211, 47, 47));
        } else {
            this.setTextColor(14737632);
        }
    }

    public ResourceLocation createId() {
        return ResourceLocation.parse(this.getValue());
    }

    public <T> T getObjectById() {
        if (this.isValid() && this.registry != null) {
            return (T) registry.get(this.createId());
        }
        return null;
    }

    public boolean isValid() {
        return this.isValid(ResourceLocation.parse(this.getValue()));
    }

    public boolean isValid(ResourceLocation id) {
        if (this.registry != null)
            return this.registry.containsKey(id);
        else if (this.ids != null)
            return this.ids.contains(id);
        return false;
    }

}
