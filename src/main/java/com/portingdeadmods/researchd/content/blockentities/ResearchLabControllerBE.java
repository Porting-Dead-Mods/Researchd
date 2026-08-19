package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.api.ghost.GhostMultiblockControllerBE;
import com.portingdeadmods.portingdeadlibs.api.gui.menus.PDLAbstractContainerMenu;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.portingdeadlibs.utils.capabilities.HandlerUtils;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.ResearchdApi;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.content.items.ResearchPackItem;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.data.ResearchdDataComponents;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchLabControllerBE extends GhostMultiblockControllerBE implements MenuProvider {
    public LazyFinal<List<BlockPos>> partPos;
    public Map<ResourceKey<ResearchPack>, Float>
            researchPackUsage; // Usage is between 0 and 1. It decreases with 1/DURATION per tick.
    public int currentResearchDuration; // Just initialized to -1
    public List<ResourceKey<ResearchPack>> researchPacks;
    /** Stacks whose pack is gone, waiting to be popped out. See {@link #remapSlotsToPacks()}. */
    private final List<ItemStack> orphanedStacks = new ArrayList<>();

    public ResearchLabControllerBE(BlockPos pos, BlockState blockState) {
        super(ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get(), pos, blockState);
        this.currentResearchDuration = -1;
        this.researchPackUsage = new HashMap<>();

        this.addItemHandler(HandlerUtils::newItemStackHandler, builder -> builder.onChange(slot -> {
                    if (level != null) {
                        this.updateData();
                    }
                })
                .validator(this::isItemValid));
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.researchPacks = ResearchHelperCommon.getResearchPackKeys(level);
        this.researchPacks.forEach(key -> {
            this.researchPackUsage.computeIfAbsent(key, $ -> 0f);
        });
        this.researchPackUsage.keySet().retainAll(this.researchPacks);

        this.remapSlotsToPacks();
    }

    private void remapSlotsToPacks() {
        ItemStackHandler itemHandler = (ItemStackHandler) this.getItemHandler();

        List<ItemStack> savedStacks = new ArrayList<>(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            savedStacks.add(itemHandler.getStackInSlot(i));
        }

        itemHandler.setSize(this.researchPacks.size());

        for (ItemStack stack : savedStacks) {
            if (stack.isEmpty()) continue;

            ResourceKey<ResearchPack> packKey = getPackKey(stack);
            int slot = packKey != null ? this.researchPacks.indexOf(packKey) : -1;

            if (slot >= 0 && itemHandler.getStackInSlot(slot).isEmpty()) {
                itemHandler.setStackInSlot(slot, stack);
                continue;
            }

            Researchd.error(
                    "Research Lab",
                    "Research pack %s stored at %s no longer has a slot, dropping it",
                    packKey != null ? packKey.location() : "<unknown>",
                    this.getBlockPos().toShortString());
            this.orphanedStacks.add(stack); // Dropped on the next tick, the chunk is still loading here
        }
    }

    private static @Nullable ResourceKey<ResearchPack> getPackKey(ItemStack stack) {
        ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK);
        return component != null ? component.researchPackKey().orElse(null) : null;
    }

    private boolean isItemValid(int slot, ItemStack stack) {
        ResourceKey<ResearchPack> itemPackKey = getPackKey(stack);
        if (itemPackKey == null || this.researchPacks == null || slot >= this.researchPacks.size()) return false;

        return this.researchPacks.get(slot).equals(itemPackKey);
    }

    @Contract(pure = true)
    public boolean containsNecessaryPacks(List<ResourceKey<ResearchPack>> packs) {
        List<ResourceKey<ResearchPack>> packsCopy = new ArrayList<>(packs);

        IItemHandler handler = getItemHandler();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ResearchPackItem)) continue;

            ResourceKey<ResearchPack> key = getPackKey(stack);
            if (key == null) continue; // Leftover pack item from a pack that no longer exists

            if (packsCopy.contains(key) || researchPackUsage.getOrDefault(key, 0f) > 0) {
                packsCopy.remove(key);
            }
        }

        return packsCopy.isEmpty();
    }

    public void decreaseNecessaryPackCount(List<ResourceKey<ResearchPack>> packs) {
        IItemHandler handler = getItemHandler();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ResearchPackItem)) continue;

            ResourceKey<ResearchPack> key = getPackKey(stack);
            if (key == null) continue;

            if (packs.contains(key)
                    && (researchPackUsage.getOrDefault(key, 0f)
                            == 0)) { // Only decrease if the pack is necessary and not already used
                stack.shrink(1);
                researchPackUsage.put(key, researchPackUsage.getOrDefault(key, 0f) + 1f);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.orphanedStacks.isEmpty()) {
            if (this.level != null && !this.level.isClientSide()) {
                this.orphanedStacks.forEach(stack -> Block.popResource(this.level, this.getBlockPos(), stack));
            }
            this.orphanedStacks.clear();
        }

        UUID teamId = ResearchdApi.getOrMigratePlacedByTeam(this, this.getLevel());
        ResearchTeam team = ResearchdApi.getTeamManager(this.getLevel()).getTeamById(teamId);
        if (team == null) return;

        ResourceKey<Research> current = team.getCurrentResearch();
        if (current == null) return;

        ResearchProgress progress = team.getResearchProgresses().get(current);
        if (progress == null) return;

        progress.checkProgress(current, this.level, new ResearchMethod.SimpleMethodContext(team, this));
    }

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag researchPackUsageTag = new CompoundTag();
        for (Map.Entry<ResourceKey<ResearchPack>, Float> entry : researchPackUsage.entrySet()) {
            researchPackUsageTag.putFloat(entry.getKey().location().toString(), entry.getValue());
        }
        tag.put("research_pack_usage", researchPackUsageTag);
        super.saveData(tag, registries);
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag researchPackUsageTag = tag.getCompound("research_pack_usage");
        for (String key : researchPackUsageTag.getAllKeys()) {
            this.researchPackUsage.put(
                    ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.parse(key)),
                    researchPackUsageTag.getFloat(key));
        }
        super.loadData(tag, registries);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Research Lab");
    }

    @Override
    protected PDLAbstractContainerMenu<?> createControllerMenu(int containerId, Inventory inventory, Player player) {
        return new ResearchLabMenu(containerId, inventory, this);
    }
}
