package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.utils.IOAction;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.data.team.ResearchTeam;
import com.portingdeadmods.researchd.api.data.team.TeamResearchProgress;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.packs.SimpleResearchPack;
import com.portingdeadmods.researchd.content.items.ResearchPackItem;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import com.portingdeadmods.researchd.data.helper.ResearchTeamHelper;
import com.portingdeadmods.researchd.impl.research.method.ConsumePackResearchMethod;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.registries.ResearchdValueEffects;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResearchLabControllerBE extends ContainerBlockEntity implements MenuProvider {
	public LazyFinal<List<BlockPos>> partPos = LazyFinal.create();
	public Map<ResourceKey<SimpleResearchPack>, Float> researchPackUsage = Researchd.RESEARCH_PACK_REGISTRY.getOrThrow().listElementIds()
			.collect(Collectors.toConcurrentMap(key -> key, k -> 0f)); // Usage is between 0 and 1. It decreases with 1/DURATION per tick.
	public int currentResearchDuration = -1; // Just initialized to -1

	public ResearchLabControllerBE(BlockPos pos, BlockState blockState) {
		super(ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get(), pos, blockState);
		addItemHandler(Researchd.RESEARCH_PACK_COUNT.getOrThrow(), (idx, stack) -> {
			Researchd.debug("Research Lab Item Handler", "Checking stack at idx: ", idx, " stack: ", stack);

			if (!(stack.getItem() instanceof ResearchPackItem pack)) {
				Researchd.debug("Research Lab Item Handler", "Not a ResearchPackItem at idx: ", idx);
				return false;
			}
			if (!stack.has(ResearchdDataComponents.RESEARCH_PACK)) {
				Researchd.debug("Research Lab Item Handler", "Stack doesn't have a Research Pack Data Component at idx: ", idx);
				return false;
			}

			ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK);
			Optional<ResourceKey<SimpleResearchPack>> optKey = component.researchPackKey();
			if (optKey.isEmpty()) {
				Researchd.debug("Research Lab Item Handler", "Component present, but no researchPackKey was found at idx: ", idx);
				return false;
			}
			ResourceKey<SimpleResearchPack> key = optKey.get();

			SimpleResearchPack expectedPack = Researchd.RESEARCH_PACKS.get(idx);
			SimpleResearchPack actualPack = Researchd.RESEARCH_PACK_REGISTRY.get().getOrThrow(key).value();
			boolean matches = expectedPack.equals(actualPack);
			Researchd.debug("Research Lab Item Handler", "Expected: ", expectedPack, " Actual: ", actualPack, " Match: ", matches, " at idx: ", idx);
			return matches;
		});
	}

	// Param passed is not mutated.
	private boolean containsNecessaryPacks(List<ResourceKey<SimpleResearchPack>> _packs) {
		List<ResourceKey<SimpleResearchPack>> packs = new ArrayList<>(_packs);

		IItemHandler handler = getItemHandler();
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if (stack.isEmpty() || !(stack.getItem() instanceof ResearchPackItem)) continue;

			ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK);
			ResourceKey<SimpleResearchPack> key = component.researchPackKey().get(); // Placing item into a lab slot condition alr makes it such that key is present

			if (packs.contains(key) || researchPackUsage.getOrDefault(key, 0f) > 0) {
				packs.remove(key);
			}
		}

		return packs.isEmpty();
	}

	private void decreaseNecessaryPackCount(List<ResourceKey<SimpleResearchPack>> packs) {
		IItemHandler handler = getItemHandler();
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if (stack.isEmpty() || !(stack.getItem() instanceof ResearchPackItem)) continue;

			ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK);
			ResourceKey<SimpleResearchPack> key = component.researchPackKey().get(); // Placing item into a lab slot condition alr makes it such that key is present

			if (packs.contains(key) && (researchPackUsage.getOrDefault(key, 0f) == 0)) { // Only decrease if the pack is necessary and not already used
				stack.shrink(1);
				researchPackUsage.put(key, researchPackUsage.getOrDefault(key, 0f) + 1f);
			}
		}
	}

	@Override
	public void commonTick() {
		super.commonTick();
		ResearchTeam team = ResearchTeamHelper.getResearchTeamByUUID(this.getLevel(), this.getData(ResearchdAttachments.PLACED_BY_UUID));
		if (team == null) return;

		TeamResearchProgress teamProgress = team.getMetadata().getResearchProgress();
        ResourceKey<Research> current = teamProgress.currentResearch();
		if (current == null) return;

		List<ResearchMethodProgress> validMethods = teamProgress.getAllValidMethodProgress(ConsumePackResearchMethod.class);
		if (validMethods == null) return;
		if (validMethods.isEmpty()) return;

		for (ResearchMethodProgress progress : validMethods) {
			ConsumePackResearchMethod method = (ConsumePackResearchMethod) progress.getMethod();
			List<ResourceKey<SimpleResearchPack>> packs = method.packs();
			this.currentResearchDuration = method.duration();

			if (!this.containsNecessaryPacks(packs)) continue;
			this.decreaseNecessaryPackCount(packs);

			for (ResourceKey<SimpleResearchPack> pack : packs) {
				researchPackUsage.put(pack, Math.max(researchPackUsage.get(pack) - ((1f / this.currentResearchDuration) / team.getTeamEffect(ResearchdValueEffects.RESEARCH_LAB_PRODUCTIVITY)), 0f));
			}
			progress.progress(1f / this.currentResearchDuration);
		}
	}

	@Override
	protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
		partPos.ifInitialized(pos -> {
			tag.putLongArray("PartPositions", pos.stream().mapToLong(BlockPos::asLong).toArray());
		});

		for (Map.Entry<ResourceKey<SimpleResearchPack>, Float> entry : researchPackUsage.entrySet()) {
			ResourceKey<SimpleResearchPack> key = entry.getKey();
			float usage = entry.getValue();
			tag.putFloat(key.location().toString(), usage);
		}
	}

	@Override
	protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {
		if (tag.contains("PartPositions")) {
			long[] partPositions = tag.getLongArray("PartPositions");
			List<BlockPos> positions = new UniqueArray<>();
			for (long posLong : partPositions) {
				BlockPos pos = BlockPos.of(posLong);
				positions.add(pos);
			}

			this.setPartPositions(positions);
		}

		for (ResourceKey<SimpleResearchPack> key : Researchd.RESEARCH_PACK_REGISTRY.getOrThrow().listElementIds().toList()) {
			if (tag.contains(key.location().toString())) {
				float usage = tag.getFloat(key.location().toString());
				researchPackUsage.put(key, usage);
			} else {
				researchPackUsage.put(key, 0f); // Default to 0 if not present
			}
		}
	}

	@Override
	public <T> Map<Direction, Pair<IOAction, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability) {
		return Map.of();
	}

	public void setPartPositions(List<BlockPos> partPositions) {
		if (!this.partPos.isInitialized())
			this.partPos.initialize(partPositions);
		else
			Researchd.debug("Research Lab Controller BE", "Part positions are already initialized, ignoring new values: ", partPositions);
	}

	@Override
	public @NotNull Component getDisplayName() {
		return Component.literal("Research Lab");
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
		return new ResearchLabMenu(i, inventory, this);
	}
}
