package com.portingdeadmods.researchd.events.client;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.RegistryDisplay;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectManager;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.compat.ResearchdCompatHandler;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.ItemUnlockEffectData;
import com.portingdeadmods.researchd.data.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchEffectHelper;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = Researchd.MODID, value = Dist.CLIENT)
public final class ResearchdClientEvents {
    @SubscribeEvent
    public static void preClientTick(ClientTickEvent.Pre event) {
        if (ResearchdKeybinds.OPEN_RESEARCH_SCREEN.get().consumeClick()) {
            ResearchdApi.openScreen();
        }

        if (ResearchdKeybinds.OPEN_RESEARCH_TEAM_SCREEN.get().consumeClick()) {
            if (ResearchdCompatHandler.isFTBTeamsEnabled()) {
                // TODO: Open ftb team screen?
                Minecraft.getInstance().player.sendSystemMessage(ResearchdTranslations.component(ResearchdTranslations.Game.FTB_TEAMS_INSTALLED));
            } else {
                ResearchdApi.openTeamScreen();
            }
        }
    }

    @SubscribeEvent
    public static void onToolTipEvent(ItemTooltipEvent event) {
        if (event.getEntity() == null) return;
        LocalPlayer player = (LocalPlayer) event.getEntity();
        ItemStack itemStack = event.getItemStack();

        ItemUnlockEffectData itemData = ResearchEffectHelper.getEffectDataForPlayer(player, ResearchdEffectDataTypes.ITEM_UNLOCK);

        if (itemData != null && itemData.isBlocked(itemStack.getItem())) {
            event.getToolTip().add(Component.empty()); // Add a blank line for spacing
            event.getToolTip().add(Component.literal("This item is blocked by a researchPack!").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
        }

        if (itemStack.has(ResearchdDataComponents.RESEARCH_PACK)) {
            Optional<ResourceKey<ResearchPack>> key = itemStack.get(ResearchdDataComponents.RESEARCH_PACK).researchPackKey();
            if (key.isPresent()) {
                ResearchPack pack = ResearchHelperCommon.getResearchPack(key.get(), Minecraft.getInstance().level);
                if (pack instanceof RegistryDisplay<?> display) {
                    event.getToolTip().set(0, display.getDisplayNameUnsafe(key.get()));
                    event.getToolTip().add(1, display.getDisplayDescriptionUnsafe(key.get()));
                }
            }
        }
    }

}
