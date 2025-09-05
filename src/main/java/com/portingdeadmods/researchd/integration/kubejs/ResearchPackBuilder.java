package com.portingdeadmods.researchd.integration.kubejs;

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

    public ResearchPackBuilder customTexture(ResourceLocation texture) {
        this.customTexture = texture;
        return this;
    }

    @Override
    public SimpleResearchPack createObject() {
        return new SimpleResearchPack(color, Optional.ofNullable(customTexture));
    }
}