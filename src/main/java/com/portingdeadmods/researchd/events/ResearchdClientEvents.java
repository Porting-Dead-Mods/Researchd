package com.portingdeadmods.researchd.events;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClientConfig;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.UnlockItemEffectData;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Map;

@EventBusSubscriber(modid = Researchd.MODID, value = Dist.CLIENT)
public final class ResearchdClientEvents {
	@SubscribeEvent
	public static void preClientTick(ClientTickEvent.Pre event) {
		if (ResearchdKeybinds.OPEN_RESEARCH_SCREEN.get().consumeClick()) {
			Minecraft.getInstance().setScreen(new ResearchScreen());
		}

		if (ResearchdKeybinds.OPEN_RESEARCH_TEAM_SCREEN.get().consumeClick()) {
			Minecraft.getInstance().setScreen(new ResearchTeamScreen());
		}
	}

//	@SubscribeEvent
//	public static void postClientTick(ClientTickEvent.Post event) {
//		Level level = Minecraft.getInstance().level;
//		LocalPlayer player = Minecraft.getInstance().player;
//		if (player == null || level == null) return;
//
//		ResearchTeamMap map = ResearchdSavedData.TEAM_RESEARCH.get().getData(level);
//		if (map == null) return;
//
//		ResearchTeam team = map.getTeamByPlayer(player);
//		if (team == null) return;
//
//		TeamResearchProgress researchProgress = team.getResearchProgress();
//		ResearchQueue queue = researchProgress.researchQueue();
//	}

	@SubscribeEvent
	public static void onToolTipEvent(ItemTooltipEvent event) {
		if (event.getEntity() == null) return;
		LocalPlayer player = (LocalPlayer) event.getEntity();
		Item item = event.getItemStack().getItem();

		UnlockItemEffectData itemData = player.getData(ResearchdAttachments.ITEM_PREDICATE.get());
		if (itemData.isBlocked(item)) {
			event.getToolTip().add(Component.literal("")); // Add a blank line for spacing
			event.getToolTip().add(Component.literal("This item is blocked by a research!").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
		}
	}

	@SubscribeEvent
	public static void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
		LocalPlayer player = event.getPlayer();

		if (ResearchdClientConfig.showJoinMessage)
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

		RegistryAccess registryAccess = player.registryAccess();
		Map<ResourceKey<ResearchPack>, ResearchPack> packs = ResearchHelperCommon.getResearchPacks(player.level());

//		if (Researchd.RESEARCH_PACKS.isEmpty()) {
//			Researchd.RESEARCH_PACKS.addAll(packs.values().stream().sorted(Comparator.comparingInt(ResearchPack::sortingValue)).toList());
//			Researchd.debug("Researchd Constants Client", "Initialized research packs: ", Researchd.RESEARCH_PACKS, "");
//		}
//
//		if (!Researchd.RESEARCH_PACK_COUNT.isInitialized()) {
//			Researchd.RESEARCH_PACK_COUNT.initialize(packs.size());
//			Researchd.debug("Researchd Constants Client", "Initialized research pack count: ", Researchd.RESEARCH_PACK_COUNT.get());
//		}
//
//		if (!Researchd.RESEARCH_PACK_REGISTRY.isInitialized()) {
//			Researchd.RESEARCH_PACK_REGISTRY.initialize(registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY));
//			Researchd.debug("Researchd Constants Client", "Initialized research pack registry.");
//		}
	}

	@SubscribeEvent
	public static void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		ResearchGraphCache.clearCache();
	}
}
