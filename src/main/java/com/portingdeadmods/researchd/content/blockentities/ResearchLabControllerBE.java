package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.utils.IOAction;
import com.portingdeadmods.portingdeadlibs.utils.LazyFinal;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethod;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.content.items.ResearchPackItem;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.components.ResearchPackComponent;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import com.portingdeadmods.researchd.registries.ResearchdBlockEntityTypes;
import com.portingdeadmods.researchd.registries.ResearchdDataComponents;
import com.portingdeadmods.researchd.utils.researches.ResearchHelperCommon;
import com.portingdeadmods.researchd.utils.researches.ResearchTeamHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResearchLabControllerBE extends ContainerBlockEntity implements MenuProvider {
    public LazyFinal<List<BlockPos>> partPos;
    public Map<ResourceKey<ResearchPack>, Float> researchPackUsage; // Usage is between 0 and 1. It decreases with 1/DURATION per tick.
    public int currentResearchDuration; // Just initialized to -1
    public List<ResourceKey<ResearchPack>> researchPacks;

    public ResearchLabControllerBE(BlockPos pos, BlockState blockState) {
        super(ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get(), pos, blockState);
        this.partPos = LazyFinal.create();
        this.currentResearchDuration = -1;
        this.researchPackUsage = new HashMap<>();
        this.researchPacks = new ArrayList<>();

        addItemHandler(0, this::isItemValid);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        this.researchPacks = ResearchHelperCommon.getResearchPackKeys(level);
        this.addItemHandler(this.researchPacks.size(), this::isItemValid);
        if (this.researchPackUsage.isEmpty()) {
            this.researchPackUsage = ResearchHelperCommon.getResearchPacks(level).keySet().stream()
                    .collect(Collectors.toConcurrentMap(Function.identity(), $ -> 0f));
        } else {
            // TODO: add new packs to pack usage
        }
    }

    private boolean isItemValid(int slot, ItemStack stack) {
        if (stack.has(ResearchdDataComponents.RESEARCH_PACK.get())) {
            if (this.researchPacks.size() > slot) {
                ResourceKey<ResearchPack> packKey = this.researchPacks.get(slot);
                Optional<ResourceKey<ResearchPack>> itemPackKey = stack.get(ResearchdDataComponents.RESEARCH_PACK.get()).researchPackKey();
                if (itemPackKey.isPresent()) {
                    return packKey.compareTo(itemPackKey.get()) == 0;
                }
            }
        }
        return false;
    }

    // Param passed is not mutated.
    public boolean containsNecessaryPacks(List<ResourceKey<ResearchPack>> packs) {
        List<ResourceKey<ResearchPack>> packsCopy = new ArrayList<>(packs);

        IItemHandler handler = getItemHandler();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ResearchPackItem)) continue;

            ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK);
            ResourceKey<ResearchPack> key = component.researchPackKey().get(); // Placing item into a lab slot condition alr makes it such that key is present

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

            ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK);
            ResourceKey<ResearchPack> key = component.researchPackKey().get(); // Placing item into a lab slot condition alr makes it such that key is present

            if (packs.contains(key) && (researchPackUsage.getOrDefault(key, 0f) == 0)) { // Only decrease if the pack is necessary and not already used
                stack.shrink(1);
                researchPackUsage.put(key, researchPackUsage.getOrDefault(key, 0f) + 1f);
            }
        }
    }

    @Override
    public void commonTick() {
        super.commonTick();

        ResearchTeam team = ResearchTeamHelper.getTeamByMember(this.getLevel(), this.getData(ResearchdAttachments.PLACED_BY_UUID));

        ResourceKey<Research> current = team.getCurrentResearch();
        if (current == null) return;

        ResearchProgress progress = team.getResearchProgresses().get(current);
        if (progress == null) return;

        progress.checkProgress(current, this.level, new ResearchMethod.SimpleMethodContext(team, this));
    }

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        partPos.ifInitialized(pos -> {
            tag.putLongArray("part_positions", pos.stream().mapToLong(BlockPos::asLong).toArray());
        });

        CompoundTag researchPackUsageTag = new CompoundTag();
        for (Map.Entry<ResourceKey<ResearchPack>, Float> entry : researchPackUsage.entrySet()) {
            researchPackUsageTag.putFloat(entry.getKey().location().toString(), entry.getValue());
        }
        tag.put("research_pack_usage", researchPackUsageTag);
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("part_positions")) {
            long[] partPositions = tag.getLongArray("part_positions");
            List<BlockPos> positions = new UniqueArray<>();
            for (long posLong : partPositions) {
                BlockPos pos = BlockPos.of(posLong);
                positions.add(pos);
            }

            this.setPartPositions(positions);
        }

        CompoundTag researchPackUsageTag = tag.getCompound("research_pack_usage");
        for (String key : researchPackUsageTag.getAllKeys()) {
            this.researchPackUsage.put(ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, ResourceLocation.parse(key)), researchPackUsageTag.getFloat(key));
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

    public boolean shouldExposeHandler(ResearchLabPartBE part) {
        if (this.getBlockPos().relative(Direction.SOUTH).equals(part.getBlockPos())) return true;
        if (this.getBlockPos().relative(Direction.EAST).equals(part.getBlockPos())) return true;
        if (this.getBlockPos().relative(Direction.NORTH).equals(part.getBlockPos())) return true;
        if (this.getBlockPos().relative(Direction.WEST).equals(part.getBlockPos())) return true;

        return false;
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
