package com.portingdeadmods.researchd.api.research;

import com.mojang.serialization.Codec;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreenWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ResearchStatus implements StringRepresentable {
    RESEARCHED("researched", "entry_green", 4),
    RESEARCHABLE("researchable", "entry_yellow", 1),
    RESEARCHABLE_AFTER_QUEUE("researchable_after_queue", "entry_orange", 2),
    LOCKED("locked", "entry_red", 3);

    public static final Codec<ResearchStatus> CODEC = StringRepresentable.fromEnum(ResearchStatus::values);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ResearchStatus> STREAM_CODEC = CodecUtils.enumStreamCodec(ResearchStatus.class);

    private final String name;
    private final ResourceLocation spriteSmallTexture;
    private final ResourceLocation spriteTexture;
    private final ResourceLocation spriteTallTexture;
    private final int sortingValue;

    /**
     * @param spriteTexture The texture name without the file extension.
     * @param sortingValue  A comparative value for sorting purposes. Higher values are drawn on top of lower values.
     */
    ResearchStatus(String name, String spriteTexture, int sortingValue) {
        this.name = name;
        this.spriteSmallTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + "_small.png");
        this.spriteTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + ".png");
        this.spriteTallTexture = Researchd.rl("textures/gui/sprites/" + spriteTexture + "_tall.png");
        this.sortingValue = sortingValue;
    }

    public ResourceLocation getSpriteTexture() {
        return spriteTexture;
    }

    public ResourceLocation getSpriteTexture(ResearchScreenWidget.PanelSpriteType spriteType) {
        return switch (spriteType) {
            case TALL -> this.spriteTallTexture;
            case NORMAL -> this.spriteTexture;
            case SMALL -> this.spriteSmallTexture;
        };
    }

    public int getSortingValue() {
        return this.sortingValue;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
