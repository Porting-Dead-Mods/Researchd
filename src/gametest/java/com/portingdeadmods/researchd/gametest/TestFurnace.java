package com.portingdeadmods.researchd.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;

/** A furnace placed by a player, as a machine that looks recipes up from its block-entity tick. */
public record TestFurnace(FurnaceBlockEntity furnace) {
    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;
    private static final int RESULT_SLOT = 2;

    /**
     * Places a furnace at {@code relativePos} and fires the block-place event for {@code placer}, which records the
     * placer's team on the furnace the way placing it by hand does.
     */
    public static TestFurnace place(GameTestHelper helper, Player placer, BlockPos relativePos) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(relativePos);
        level.setBlockAndUpdate(pos, Blocks.FURNACE.defaultBlockState());
        EventHooks.onBlockPlace(placer, BlockSnapshot.create(level.dimension(), level, pos), Direction.UP);
        return new TestFurnace((FurnaceBlockEntity) level.getBlockEntity(pos));
    }

    /** Loads {@code input} to smelt, with coal to burn. */
    public void load(ItemStack input) {
        this.furnace.setItem(INPUT_SLOT, input.copy());
        this.furnace.setItem(FUEL_SLOT, new ItemStack(Items.COAL));
    }

    public ItemStack result() {
        return this.furnace.getItem(RESULT_SLOT);
    }

    public void assertResult(GameTestHelper helper, ItemStack expected, String message) {
        ItemStack result = this.result();
        helper.assertTrue(
                ItemStack.matches(expected, result),
                message + ": expected " + describe(expected) + ", got " + describe(result));
    }

    public void assertNoResult(GameTestHelper helper, String message) {
        this.assertResult(helper, ItemStack.EMPTY, message);
    }

    private static String describe(ItemStack stack) {
        return stack.isEmpty() ? "nothing" : stack.getCount() + " " + stack.typeHolder().getRegisteredName();
    }
}
