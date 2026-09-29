package com.portingdeadmods.researchd.content.blockentities;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.api.data.transfer.PDLItemStacksHandler;
import com.portingdeadmods.portingdeadlibs.api.ghost.GhostMultiblockControllerBE;
import com.portingdeadmods.portingdeadlibs.api.ghost.SimpleGhostMultiblockPartBE;
import com.portingdeadmods.portingdeadlibs.api.gui.menus.PDLAbstractContainerMenu;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdConfig;
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
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchLabControllerBE extends GhostMultiblockControllerBE implements MenuProvider {
    private static final Codec<Map<ResourceKey<ResearchPack>, Float>> PACK_USAGE_CODEC =
            Codec.unboundedMap(ResourceKey.codec(ResearchdRegistries.RESEARCH_PACK_KEY), Codec.FLOAT);
    /** Mirrors PDL's package-private {@code GhostMultiblockControllerBE.HandlerExposure} codec. */
    private static final Codec<Pair<BlockPos, List<Identifier>>> HANDLER_EXPOSURE_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                            BlockPos.CODEC.fieldOf("pos").forGetter(Pair::getFirst),
                            Identifier.CODEC.listOf().fieldOf("handlers").forGetter(Pair::getSecond))
                    .apply(inst, Pair::of));

    public Map<ResourceKey<ResearchPack>, Float>
            researchPackUsage; // Usage is between 0 and 1. It decreases with 1/DURATION per tick.
    public int currentResearchDuration; // Just initialized to -1
    public List<ResourceKey<ResearchPack>> researchPacks;
    /** Stacks whose pack is gone, waiting to be popped out. See {@link #remapSlotsToPacks()}. */
    private final List<ItemStack> orphanedStacks = new ArrayList<>();

    private final LabItemHandler itemHandler;
    private final ResearchLabEnergyStorage energyHandler;

    private static final int ENERGY_SYNC_INTERVAL = 10;

    private int lastSyncedEnergy = -1;

    public ResearchLabControllerBE(BlockPos pos, BlockState blockState) {
        super(ResearchdBlockEntityTypes.RESEARCH_LAB_CONTROLLER.get(), pos, blockState);
        this.currentResearchDuration = -1;
        this.researchPackUsage = new HashMap<>();

        this.itemHandler = this.addHandler(Capabilities.Item.BLOCK, new LabItemHandler());
        this.itemHandler.setOnChangeFunction((slot, previous) -> {
            if (level != null) {
                this.updateData();
            }
        });
        this.itemHandler.setValidator(this::isItemValid);

        this.energyHandler = this.addHandler(Capabilities.Energy.BLOCK, new ResearchLabEnergyStorage());
        this.energyHandler.setOnChangeFunction(previous -> this.setChanged());
    }

    /** The per-tick draw. Zero means the feature is off. */
    public static int getEnergyUsage() {
        return ResearchdConfig.Server.getResearchLabEnergyUsage();
    }

    public PDLItemStacksHandler getItemHandler() {
        return this.itemHandler;
    }

    public ResearchLabEnergyStorage getEnergyHandler() {
        return this.energyHandler;
    }

    /**
     * Takes one tick's worth of energy, or nothing at all.
     *
     * @return true if the tick is paid for
     */
    public boolean tryConsumeEnergy() {
        int usage = getEnergyUsage();
        if (usage <= 0) return true;

        try (Transaction transaction = Transaction.openRoot()) {
            if (this.energyHandler.extract(usage, transaction) < usage) return false; // Rolled back on close

            transaction.commit();
            return true;
        }
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
        NonNullList<ItemStack> savedStacks = this.itemHandler.copyToList();

        this.itemHandler.clearAndResize(this.researchPacks.size());

        for (ItemStack stack : savedStacks) {
            if (stack.isEmpty()) continue;

            ResourceKey<ResearchPack> packKey = getPackKey(stack);
            int slot = packKey != null ? this.researchPacks.indexOf(packKey) : -1;

            if (slot >= 0 && this.itemHandler.getAmountAsInt(slot) == 0) {
                this.itemHandler.set(slot, ItemResource.of(stack), stack.getCount());
                continue;
            }

            Researchd.error(
                    "Research Lab",
                    "Research pack %s stored at %s no longer has a slot, dropping it",
                    packKey != null ? packKey.identifier() : "<unknown>",
                    this.getBlockPos().toShortString());
            this.orphanedStacks.add(stack); // Dropped on the next tick, the chunk is still loading here
        }
    }

    private static @Nullable ResourceKey<ResearchPack> getPackKey(DataComponentHolder stack) {
        ResearchPackComponent component = stack.get(ResearchdDataComponents.RESEARCH_PACK.get());
        return component != null ? component.researchPackKey().orElse(null) : null;
    }

    private boolean isItemValid(int slot, ItemResource resource) {
        ResourceKey<ResearchPack> itemPackKey = getPackKey(resource);
        if (itemPackKey == null || this.researchPacks == null || slot >= this.researchPacks.size()) return false;

        return this.researchPacks.get(slot).equals(itemPackKey);
    }

    @Contract(pure = true)
    public boolean containsNecessaryPacks(List<ResourceKey<ResearchPack>> packs) {
        List<ResourceKey<ResearchPack>> packsCopy = new ArrayList<>(packs);

        for (int i = 0; i < this.itemHandler.size(); i++) {
            ItemResource resource = this.itemHandler.getResource(i);
            if (resource.isEmpty() || !(resource.getItem() instanceof ResearchPackItem)) continue;

            ResourceKey<ResearchPack> key = getPackKey(resource);
            if (key == null) continue; // Leftover pack item from a pack that no longer exists

            if (packsCopy.contains(key) || researchPackUsage.getOrDefault(key, 0f) > 0) {
                packsCopy.remove(key);
            }
        }

        return packsCopy.isEmpty();
    }

    public void decreaseNecessaryPackCount(List<ResourceKey<ResearchPack>> packs) {
        for (int i = 0; i < this.itemHandler.size(); i++) {
            ItemResource resource = this.itemHandler.getResource(i);
            if (resource.isEmpty() || !(resource.getItem() instanceof ResearchPackItem)) continue;

            ResourceKey<ResearchPack> key = getPackKey(resource);
            if (key == null) continue;

            if (packs.contains(key)
                    && (researchPackUsage.getOrDefault(key, 0f)
                            == 0)) { // Only decrease if the pack is necessary and not already used
                int amount = this.itemHandler.getAmountAsInt(i) - 1;
                this.itemHandler.set(i, amount > 0 ? resource : ItemResource.EMPTY, amount);
                researchPackUsage.put(key, researchPackUsage.getOrDefault(key, 0f) + 1f);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.energyHandler.clampToCapacity();
        this.syncEnergyToClient();

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

    // TODO(26.1 port, PDL): PDL 1.1.15's PDLBlockEntity#dropItems does nothing, where PDL's 1.21.1 ContainerBlock
    // dropped the item handler's contents, so a broken Lab would lose its Research Packs
    @Override
    protected void dropItems() {
        if (this.level == null || this.level.isClientSide()) return;

        for (int i = 0; i < this.itemHandler.size(); i++) {
            ItemResource resource = this.itemHandler.getResource(i);
            if (resource.isEmpty()) continue;

            Block.popResource(this.level, this.getBlockPos(), resource.toStack(this.itemHandler.getAmountAsInt(i)));
        }
        this.orphanedStacks.forEach(stack -> Block.popResource(this.level, this.getBlockPos(), stack));
        this.orphanedStacks.clear();
    }

    // TODO(26.1 port, PDL): PDL 1.1.15's GhostMultiblockController removes the parts in affectNeighborsAfterRemoval,
    // which 26.1 calls after this block entity is gone, so it never finds them. They're removed here instead
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (this.level instanceof ServerLevel serverLevel) {
            for (BlockPos partPos : List.copyOf(this.getPartPositions())) {
                if (serverLevel.getBlockEntity(partPos) instanceof SimpleGhostMultiblockPartBE) {
                    serverLevel.removeBlock(partPos, false);
                }
            }
        }
    }

    private void syncEnergyToClient() {
        if (this.level == null || this.level.isClientSide() || getEnergyUsage() <= 0) return;

        int stored = this.energyHandler.getAmountAsInt();
        if (stored == this.lastSyncedEnergy) return;
        if (this.level.getGameTime() % ENERGY_SYNC_INTERVAL != 0) return;

        this.lastSyncedEnergy = stored;
        this.updateData();
    }

    private void updateData() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(
                    this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.store("research_pack_usage", PACK_USAGE_CODEC, this.researchPackUsage);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        input.read("research_pack_usage", PACK_USAGE_CODEC).ifPresent(this.researchPackUsage::putAll);
        super.loadAdditional(input);

        // TODO(26.1 port, PDL): PDL 1.1.15 saves the handler exposure as "handler_exposures" but loads
        // "handler_exposure", so without this the Lab Parts expose nothing after a reload. Remove once PDL reads the
        // key it writes.
        input.listOrEmpty("handler_exposures", HANDLER_EXPOSURE_CODEC)
                .forEach(exposure -> this.exposedHandlers.put(exposure.getFirst(), exposure.getSecond()));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Research Lab");
    }

    @Override
    protected PDLAbstractContainerMenu<?> createControllerMenu(int containerId, Inventory inventory, Player player) {
        return new ResearchLabMenu(containerId, inventory, this);
    }

    /** One slot per Research Pack; resized when the pack list changes. */
    private static final class LabItemHandler extends PDLItemStacksHandler {
        private LabItemHandler() {
            super(0);
        }

        private void clearAndResize(int size) {
            this.setStacks(NonNullList.withSize(size, ItemStack.EMPTY));
        }
    }
}
