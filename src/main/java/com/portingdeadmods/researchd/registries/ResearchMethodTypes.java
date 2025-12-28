package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.impl.research.icons.SpriteResearchIcon;
import com.portingdeadmods.researchd.impl.research.icons.TextResearchIcon;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class ResearchMethodTypes {
    public static final DeferredRegister<ResearchMethodType> TYPES = DeferredRegister.create(ResearchdRegistries.RESEARCH_METHOD_TYPE, Researchd.MODID);

    public static final Supplier<ResearchMethodType> AND = registerMultipleMethodType("and", new TextResearchIcon(Component.literal("&")));
    public static final Supplier<ResearchMethodType> OR = registerMultipleMethodType("or", new TextResearchIcon(Component.literal("|")));
    public static final Supplier<ResearchMethodType> CONSUME_ITEM = registerSingleMethodType("consume_item",
            SpriteResearchIcon.spriteIcon(Researchd.MODID, "consume_item_method_icon", 16, 16));
    public static final Supplier<ResearchMethodType> CONSUME_PACK = registerSingleMethodType("consume_pack",
            SpriteResearchIcon.spriteIcon(Researchd.MODID, "consume_pack_method_icon", 16, 16));
    public static final Supplier<ResearchMethodType> CHECK_ITEM_PRESENCE = registerSingleMethodType("check_item_presence",
            SpriteResearchIcon.spriteIcon(Researchd.MODID, "check_item_presence_method_icon", 16, 16));

    private static @NotNull DeferredHolder<ResearchMethodType, ResearchMethodType> registerSingleMethodType(String id, ResearchIcon icon) {
        return TYPES.register(id, () -> ResearchMethodType.single(Researchd.rl(id), icon));
    }

    private static @NotNull DeferredHolder<ResearchMethodType, ResearchMethodType> registerMultipleMethodType(String id, ResearchIcon icon) {
        return TYPES.register(id, () -> ResearchMethodType.multiple(Researchd.rl(id), icon));
    }
}
