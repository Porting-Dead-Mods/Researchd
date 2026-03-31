package com.portingdeadmods.researchd.client.screens.editor.widgets.dropdowns;

import com.portingdeadmods.researchd.client.screens.lib.widgets.DropDownWidget;
import com.portingdeadmods.researchd.utils.Search;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RegistrySuggestionDropDownWidget extends DropDownWidget<LayoutElement> {
    private final Registry<?> registry;
    private int x;
    private int y;
    private final Consumer<StringOption> setSuggestionFunction;
    private final Supplier<String> getInputFunction;
    private final Search search;

    public RegistrySuggestionDropDownWidget(Registry<?> registry, int x, int y, Consumer<StringOption> setSuggestionFunction, Supplier<String> getInputFunction) {
        this.registry = registry;
        this.x = x;
        this.y = y;
        this.setSuggestionFunction = setSuggestionFunction;
        this.getInputFunction = getInputFunction;
        this.search = new Search();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

//    @Override
//    protected void optionClicked(Option option, int mouseX, int mouseY) {
//        super.optionClicked(option, mouseX, mouseY);
//    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, this.x, this.y, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void buildOptions() {
        String input = this.getInputFunction.get();
        boolean searchMatches = input.isEmpty();

        for (var id : this.registry.keySet()) {
            if ((searchMatches || this.search.matches(id.toString(), input)) && !this.search.matchesExactly(id.toString(), input)) {
                this.addOption(new StringOption(Component.literal(id.toString()), Minecraft.getInstance().font, setSuggestionFunction));
            }
        }

    }
}
