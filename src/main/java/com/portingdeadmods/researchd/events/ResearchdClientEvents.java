package com.portingdeadmods.researchd.events;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.client.ResearchdKeybinds;
import com.portingdeadmods.researchd.client.cache.ResearchGraphCache;
import com.portingdeadmods.researchd.client.screens.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.team.ResearchTeamScreen;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.networking.cache.AskServerPlayers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;

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

		RecipeUnlockEffectData recipeData = player.getData(ResearchdAttachments.RECIPE_PREDICATE.get());
		UniqueArray<Item> blockedItems = new UniqueArray<>();

		recipeData.blockedRecipes().forEach(recipe -> {
			blockedItems.add(recipe.value().getResultItem(player.registryAccess()).getItem());
		});

		if (blockedItems.contains(item)) {
			event.getToolTip().add(Component.literal("")); // Add a blank line for spacing
			event.getToolTip().add(Component.literal("This item is blocked by a research!").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
		}
	}

	@SubscribeEvent
	public static void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
		LocalPlayer player = event.getPlayer();
		RegistryAccess registryAccess = player.registryAccess();
		HolderLookup.RegistryLookup<ResearchPack> packs = registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY);

		if (Researchd.RESEARCH_PACKS.isEmpty()) {
			Researchd.RESEARCH_PACKS.addAll(packs.listElements().map(Holder.Reference::value).sorted(Comparator.comparingInt(ResearchPack::sorting_value)).toList());
			Researchd.debug("Researchd Constants Client", "Initialized research packs: ", Researchd.RESEARCH_PACKS, "");
		}

		if (!Researchd.RESEARCH_PACK_COUNT.isInitialized()) {
			Researchd.RESEARCH_PACK_COUNT.initialize((int) packs.listElements().count());
			Researchd.debug("Researchd Constants Client", "Initialized research pack count: ", Researchd.RESEARCH_PACK_COUNT.get());
		}

		if (!Researchd.RESEARCH_PACK_REGISTRY.isInitialized()) {
			Researchd.RESEARCH_PACK_REGISTRY.initialize(registryAccess.lookupOrThrow(ResearchdRegistries.RESEARCH_PACK_KEY));
			Researchd.debug("Researchd Constants Client", "Initialized research pack registry.");
		}

		PacketDistributor.sendToServer(AskServerPlayers.UNIT);
	}

	@SubscribeEvent
	public static void onClientPlayerLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		ResearchGraphCache.clearCache();
	}
}
