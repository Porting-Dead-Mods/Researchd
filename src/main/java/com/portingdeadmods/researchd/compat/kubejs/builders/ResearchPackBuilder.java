package com.portingdeadmods.researchd.compat.kubejs.builders;

import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.impl.utils.DisplayImpl;
import dev.latvian.mods.kubejs.script.SourceLine;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.Optional;

public class ResearchPackBuilder {
    public final ResourceLocation id;
    public SourceLine sourceLine;
    private int color = -1;
    private ResourceLocation customTexture;
    private int sortingValue = 0;
    private Component literalName;
    private Component literalDescription;

    public ResearchPackBuilder(ResourceLocation id) {
        this.id = id;
        this.sourceLine = SourceLine.UNKNOWN;
    }

    public ResearchPackBuilder color(int r, int g, int b) {
        this.color = FastColor.ARGB32.color(r, g, b);
        return this;
    }

    public ResearchPackBuilder customTexture(String texture) {
        this.customTexture = ResourceLocation.parse(texture);
        return this;
    }

    /**
     * A value to dictate where in the progression the research pack should be. <br>
     * Lower = earlier, higher = later
     */
    public ResearchPackBuilder sortingValue(int value) {
        this.sortingValue = value;
        return this;
    }

    public ResearchPackBuilder literalName(String name) {
        this.literalName = Component.literal(name);
        return this;
    }

    public ResearchPackBuilder literalDescription(String name) {
        this.literalDescription = Component.literal(name);
        return this;
    }

    public ResearchPackBuilder translatableName(String key) {
        this.literalName = Component.translatable(key);
        return this;
    }

    public ResearchPackBuilder translatableDescription(String key) {
        this.literalDescription = Component.translatable(key);
        return this;
    }

    public ResearchPackImpl createObject() {
        return new ResearchPackImpl(color, sortingValue, Optional.ofNullable(customTexture), new DisplayImpl(Optional.ofNullable(this.literalName), Optional.ofNullable(this.literalDescription)));
    }
}