package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.ResearchIcon;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.impl.research.icons.SpriteResearchIcon;
import com.portingdeadmods.researchd.impl.research.icons.TextResearchIcon;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class ResearchEffectTypes {
    public static final DeferredRegister<ResearchEffectType> TYPES = DeferredRegister.create(ResearchdRegistries.RESEARCH_EFFECT_TYPE, Researchd.MODID);

    public static final Supplier<ResearchEffectType> EMPTY = registerEffectType("empty", SpriteResearchIcon.EMPTY);
    public static final Supplier<ResearchEffectType> AND = registerEffectType("and", new TextResearchIcon(Component.literal("&")));
    public static final Supplier<ResearchEffectType> DECREASE_VALUE = registerEffectType("decrease_value", new TextResearchIcon(Component.literal("-")));
    public static final Supplier<ResearchEffectType> INCREASE_VALUE = registerEffectType("increase_value", new TextResearchIcon(Component.literal("+")));
    public static final Supplier<ResearchEffectType> MULTIPLE_VALUE = registerEffectType("multiply_value", new TextResearchIcon(Component.literal("*")));
    public static final Supplier<ResearchEffectType> DIVIDE_VALUE = registerEffectType("divide_value", new TextResearchIcon(Component.literal("/")));
    public static final Supplier<ResearchEffectType> DIMENSION_UNLOCK = registerEffectType(
            "dimension_unlock",
            SpriteResearchIcon.spriteIcon(Researchd.MODID, "dimension_unlock_icon", 16, 16)
    );
    public static final Supplier<ResearchEffectType> RECIPE_UNLOCK = registerEffectType(
            "recipe_unlock",
            SpriteResearchIcon.spriteIcon(Researchd.MODID, "recipe_unlock_icon", 16, 16)
    );
    public static final Supplier<ResearchEffectType> ITEM_UNLOCK = registerEffectType(
            "item_unlock",
            SpriteResearchIcon.spriteIcon(Researchd.MODID, "item_unlock_icon", 16, 16)
    );

    private static @NotNull DeferredHolder<ResearchEffectType, ResearchEffectType> registerEffectType(String id, ResearchIcon icon) {
        return TYPES.register(id, () -> ResearchEffectType.single(Researchd.rl(id), icon));
    }
}
