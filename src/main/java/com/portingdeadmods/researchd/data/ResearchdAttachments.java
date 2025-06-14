package com.portingdeadmods.researchd.data;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.predicates.DimensionPredicateData;
import com.portingdeadmods.researchd.content.predicates.RecipePredicateData;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public final class ResearchdAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Researchd.MODID);

    public static final Supplier<AttachmentType<DimensionPredicateData>> DIMENSION_PREDICATE = ATTACHMENTS.register("dimension_predicate",
            () -> AttachmentType.builder(() -> DimensionPredicateData.EMPTY).serialize(DimensionPredicateData.CODEC).build());

    public static final Supplier<AttachmentType<RecipePredicateData>> RECIPE_PREDICATE = ATTACHMENTS.register("recipe_predicate",
            () -> AttachmentType.builder(() -> RecipePredicateData.EMPTY).serialize(RecipePredicateData.CODEC).build());

    public static final Supplier<AttachmentType<UUID>> PLACED_BY_UUID = ATTACHMENTS.register("placed_by_uuid",
            () -> AttachmentType.builder(() -> UUID.fromString("00000000-0000-0000-0000-000000000000")).serialize(UUIDUtil.CODEC).build());
}

