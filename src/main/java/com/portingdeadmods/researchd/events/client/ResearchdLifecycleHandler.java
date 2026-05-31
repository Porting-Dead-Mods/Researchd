package com.portingdeadmods.researchd.events.client;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdConfig;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = Researchd.MODID, value = Dist.CLIENT)
public class ResearchdLifecycleHandler {
    @SubscribeEvent
    public static void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer player = event.getPlayer();

        if (ResearchdConfig.Client.showJoinMessage) {
            player.sendSystemMessage(
                    ResearchdTranslations.Game.JOIN_MESSAGE.component(Researchd.MODID)
                            .append(Component.literal("\n> ").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD))
                            .append(
                                    ResearchdTranslations.Game.GITHUB.component(Researchd.MODID)
                                            .withStyle(Style.EMPTY
                                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Porting-Dead-Mods/Researchd"))
                                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Go to the GitHub page")))
                                            )
                            )
                            .append(Component.literal("\n> ").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD))
                            .append(
                                    ResearchdTranslations.Game.WIKI.component(Researchd.MODID)
                                            .withStyle(Style.EMPTY
                                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://porting-dead-mods.github.io/Researchd/"))
                                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Go to the Wiki")))
                                            )
                            )
            );
        }
    }

    @SubscribeEvent
    public static void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ResearchGraphCache.clearCache();
        ResearchTeamCache.researchTeamMap = null;
        ResearchTeamCache.teamResearchEffectDataMap = null;
    }
}
