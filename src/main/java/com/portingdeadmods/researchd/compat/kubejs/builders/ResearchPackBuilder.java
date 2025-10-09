package com.portingdeadmods.researchd.compat.kubejs.builders;

import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import dev.latvian.mods.kubejs.script.SourceLine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.Optional;

public class ResearchPackBuilder {
    public final ResourceLocation id;
    public SourceLine sourceLine;
    private int color = -1;
    private ResourceLocation customTexture;
    private int sorting_value = 0;

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
        this.sorting_value = value;
        return this;
    }

    public ResearchPack createObject() {
        return new ResearchPack(color, sorting_value, Optional.ofNullable(customTexture));
    }
}