package com.portingdeadmods.researchd.client.screens.lib.widgets;

import com.portingdeadmods.researchd.utils.GuiUtils;
import com.portingdeadmods.researchd.utils.TextUtils;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

public class RegistryVerifyEditBox extends BackgroundEditBox {
    private final @Nullable Registry<?> registry;
    /** With neither a registry nor ids, any well-formed id is valid */
    private final @Nullable Collection<Identifier> ids;

    public RegistryVerifyEditBox(
            Font font,
            WidgetSprites sprites,
            @Nullable Registry<?> registry,
            @Nullable Collection<Identifier> ids,
            int width,
            int height,
            Component message) {
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
        return new RegistryVerifyEditBox(
                GuiUtils.getFont(), BackgroundEditBox.SPRITES, registry, null, width, height, CommonComponents.EMPTY);
    }

    public static RegistryVerifyEditBox forIds(Collection<Identifier> ids, int width, int height) {
        Objects.requireNonNull(ids);
        return new RegistryVerifyEditBox(
                GuiUtils.getFont(), BackgroundEditBox.SPRITES, null, ids, width, height, CommonComponents.EMPTY);
    }

    public @Nullable Registry<?> getRegistry() {
        return registry;
    }

    public @Nullable Collection<Identifier> getIds() {
        return ids;
    }

    @Override
    public void onValueChangedExtra(String newText) {
        Identifier id = Identifier.parse(newText);
        if (!this.isValid(id)) {
            this.setTextColor(ARGB.color(211, 47, 47));
        } else {
            this.setTextColor(0xFFE0E0E0);
        }
    }

    public Identifier createId() {
        return Identifier.parse(this.getValue());
    }

    public <T> T getObjectById() {
        if (this.isValid() && this.registry != null) {
            return (T) registry.getValue(this.createId());
        }
        return null;
    }

    public boolean isValid() {
        return this.isValid(Identifier.parse(this.getValue()));
    }

    public boolean isValid(Identifier id) {
        if (this.registry != null) return this.registry.containsKey(id);
        else if (this.ids != null) return this.ids.contains(id);
        return true;
    }
}
