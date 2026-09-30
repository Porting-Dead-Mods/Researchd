package com.portingdeadmods.researchd.client.renderers;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.data.ResearchdDataComponents;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Tints a Research Pack's overlay layer with its pack's color. */
public record ResearchPackTintSource() implements ItemTintSource {
    public static final Identifier ID = Researchd.rl("research_pack");
    public static final MapCodec<ResearchPackTintSource> MAP_CODEC = MapCodec.unit(new ResearchPackTintSource());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (ResearchdClient.previewRendererResearchPackColor != -1) {
            return ResearchdClient.previewRendererResearchPackColor;
        }

        ResearchPackComponent researchPackComponent = stack.get(ResearchdDataComponents.RESEARCH_PACK);
        if (researchPackComponent == null) return -1;

        if (researchPackComponent.researchPackKey().isPresent()) {
            ResearchPack researchPack = ResearchHelperCommon.getResearchPack(
                    researchPackComponent.researchPackKey().get(), Minecraft.getInstance().level);
            if (researchPack != null) {
                return researchPack.color();
            }
        }
        return -1;
    }

    @Override
    public MapCodec<ResearchPackTintSource> type() {
        return MAP_CODEC;
    }
}
