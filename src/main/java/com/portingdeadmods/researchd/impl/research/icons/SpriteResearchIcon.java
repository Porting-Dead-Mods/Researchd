package com.portingdeadmods.researchd.impl.research.icons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.serializers.ResearchIconSerializer;
import net.minecraft.resources.ResourceLocation;

public record SpriteResearchIcon(ResourceLocation sprite, int width, int height) implements ResearchIcon {
    public static final ResearchIconSerializer<SpriteResearchIcon> SERIALIZER = ResearchIconSerializer.simple(RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(SpriteResearchIcon::sprite),
            Codec.INT.fieldOf("width").forGetter(SpriteResearchIcon::width),
            Codec.INT.fieldOf("height").forGetter(SpriteResearchIcon::height)
    ).apply(inst, SpriteResearchIcon::new)));
    public static final ResourceLocation ID = Researchd.rl("sprite_research_icon");
    public static final SpriteResearchIcon EMPTY = SpriteResearchIcon.spriteIcon(Researchd.MODID, "missing_sprite", 16, 16);

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public ResearchIconSerializer<SpriteResearchIcon> getSerializer() {
        return SERIALIZER;
    }

    public static SpriteResearchIcon spriteIcon(String namespace, String path, int width, int height) {
        return new SpriteResearchIcon(ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/sprites/icon_sprites/" + path + ".png"), width, height);
    }

}
