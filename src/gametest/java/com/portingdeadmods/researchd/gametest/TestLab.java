package com.portingdeadmods.researchd.gametest;

import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import com.portingdeadmods.researchd.registries.ResearchdItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * A Research Lab seen from outside: the item and energy handlers of one of its Lab Parts, the way a pipe or a
 * hopper would reach them.
 * <p>
 * The Lab Controller sets up its pack slots on its first tick, so the Lab takes no packs before tick 1.
 */
public record TestLab(ResourceHandler<ItemResource> items, EnergyHandler energy) {
    /** Places a Research Lab as {@code player}, so it belongs to the player's team at the time. */
    public static TestLab place(GameTestHelper helper, Player player) {
        ServerLevel level = helper.getLevel();
        ItemStack stack = ResearchdItems.RESEARCH_LAB.toStack();
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos target = helper.absolutePos(new BlockPos(3, 1, 3));
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(target), Direction.UP, target, false);
        InteractionResult placed = ResearchdItems.RESEARCH_LAB
                .get()
                .place(new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack, hit));
        helper.assertTrue(placed.consumesAction(), "Research Lab placement returned " + placed);

        // The Lab spans 3x3x3 around the clicked spot. Any Lab Part exposing both handlers will do
        boolean hasController = false;
        for (BlockPos pos : BlockPos.betweenClosed(target.offset(-3, -3, -3), target.offset(3, 3, 3))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ResearchLabControllerBE) hasController = true;
            if (!(be instanceof ResearchLabPartBE)) continue;

            ResourceHandler<ItemResource> items = level.getCapability(Capabilities.Item.BLOCK, pos, Direction.UP);
            EnergyHandler energy = level.getCapability(Capabilities.Energy.BLOCK, pos, Direction.UP);
            if (items != null && energy != null) return new TestLab(items, energy);
        }
        helper.assertTrue(hasController, "Research Lab was not placed");
        throw helper.assertionException(Component.literal("No Lab Part exposes items and energy"));
    }

    /** Inserts all of {@code stack} or fails the test. */
    public void insert(GameTestHelper helper, ItemStack stack) {
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = this.items.insert(ItemResource.of(stack), stack.getCount(), transaction);
            helper.assertValueEqual(stack.getCount(), inserted, "items accepted");
            transaction.commit();
        }
    }

    /** The number of items in the Lab. */
    public int itemCount() {
        int count = 0;
        for (int i = 0; i < this.items.size(); i++) {
            count += this.items.getAmountAsInt(i);
        }
        return count;
    }

    /** Inserts up to {@code amount} energy and returns how much was accepted. */
    public int insertEnergy(int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = this.energy.insert(amount, transaction);
            transaction.commit();
            return inserted;
        }
    }

    /** Inserts up to {@code amount} energy, returns how much was accepted, then aborts the transaction. */
    public int insertEnergyAndAbort(int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            return this.energy.insert(amount, transaction);
        }
    }

    /** Extracts up to {@code amount} energy and returns how much came out. */
    public int extractEnergy(int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = this.energy.extract(amount, transaction);
            transaction.commit();
            return extracted;
        }
    }

    public int energyStored() {
        return this.energy.getAmountAsInt();
    }
}
