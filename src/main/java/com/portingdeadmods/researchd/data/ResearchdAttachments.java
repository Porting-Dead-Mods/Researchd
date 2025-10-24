package com.portingdeadmods.researchd.data;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.UnlockItemEffectData;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public final class ResearchdAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Researchd.MODID);

    public static final Supplier<AttachmentType<DimensionUnlockEffectData>> DIMENSION_PREDICATE = ATTACHMENTS.register("dimension_predicate",
            () -> AttachmentType.builder(() -> DimensionUnlockEffectData.EMPTY).serialize(DimensionUnlockEffectData.CODEC).build());

    public static final Supplier<AttachmentType<RecipeUnlockEffectData>> RECIPE_PREDICATE = ATTACHMENTS.register("recipe_predicate",
            () -> AttachmentType.builder(() -> RecipeUnlockEffectData.EMPTY).serialize(RecipeUnlockEffectData.CODEC).build());

    public static final Supplier<AttachmentType<UnlockItemEffectData>> ITEM_PREDICATE = ATTACHMENTS.register("item_predicate",
            () -> AttachmentType.builder(() -> UnlockItemEffectData.EMPTY).serialize(UnlockItemEffectData.CODEC).build());

    public static final Supplier<AttachmentType<UUID>> PLACED_BY_UUID = ATTACHMENTS.register("placed_by_uuid",
            () -> AttachmentType.builder(() -> UUID.fromString("00000000-0000-0000-0000-000000000000")).serialize(UUIDUtil.CODEC).build());
}

