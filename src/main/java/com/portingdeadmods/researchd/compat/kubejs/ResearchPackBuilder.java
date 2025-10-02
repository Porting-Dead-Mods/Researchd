package com.portingdeadmods.researchd.compat.kubejs;

import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.Optional;

@ReturnsSelf
public class ResearchPackBuilder extends BuilderBase<SimpleResearchPack> {
    private int color = -1;
    private ResourceLocation customTexture;
    private int sorting_value = 0;

    public ResearchPackBuilder(ResourceLocation id) {
        super(id);
    }

    public ResearchPackBuilder color(Object color) {
        if (color instanceof KubeColor kubeColor) {
            this.color = kubeColor.kjs$getRGB();
        } else if (color instanceof Number) {
            this.color = ((Number) color).intValue();
        } else {
            throw new IllegalArgumentException("Color must be a KubeColor or hex integer");
        }
        return this;
    }

    public ResearchPackBuilder colorRGB(int r, int g, int b) {
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
        this.sorting_value = value;
        return this;
    }

    @Override
    public SimpleResearchPack createObject() {
        return new SimpleResearchPack(color, sorting_value, Optional.ofNullable(customTexture));
    }
}