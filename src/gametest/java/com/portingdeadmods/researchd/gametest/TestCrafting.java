package com.portingdeadmods.researchd.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A crafting table's menu as a player has it open. Filling the grid makes the server work out the output slot, the
 * way it does when a player places items by hand.
 * <p>
 * The server sends the output to the player's connection, so the player must be a {@link ServerPlayer} in the
 * level, see {@link TestPlayers}.
 */
public record TestCrafting(CraftingMenu menu) {
    /** Where the crafting table would stand, relative to the test structure. Nothing is placed there. */
    private static final BlockPos TABLE = new BlockPos(3, 1, 3);

    private static final int RESULT_SLOT = 0;
    private static final int FIRST_GRID_SLOT = 1;
    private static final int GRID_SLOTS = 9;

    public static TestCrafting open(GameTestHelper helper, ServerPlayer player) {
        ContainerLevelAccess access = ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(TABLE));
        return new TestCrafting(new CraftingMenu(1, player.getInventory(), access));
    }

    /**
     * Fills the grid row by row, left to right, from {@code stacks}; slots past the last stack are emptied.
     * {@link ItemStack#EMPTY} leaves a gap.
     */
    public void fill(ItemStack... stacks) {
        for (int i = 0; i < GRID_SLOTS; i++) {
            ItemStack stack = i < stacks.length ? stacks[i].copy() : ItemStack.EMPTY;
            this.menu.getSlot(FIRST_GRID_SLOT + i).set(stack);
        }
    }

    /** Takes the whole output as {@code player}, which uses up the grid and leaves the recipe's remainders. */
    public ItemStack take(ServerPlayer player) {
        Slot result = this.menu.getSlot(RESULT_SLOT);
        ItemStack taken = result.remove(result.getItem().getCount());
        result.onTake(player, taken);
        return taken;
    }

    public void assertGridEmpty(GameTestHelper helper, String message) {
        for (int i = 0; i < GRID_SLOTS; i++) {
            ItemStack left = this.menu.getSlot(FIRST_GRID_SLOT + i).getItem();
            helper.assertTrue(left.isEmpty(), message + ": grid slot " + i + " holds " + describe(left));
        }
    }

    public ItemStack output() {
        return this.menu.getSlot(RESULT_SLOT).getItem();
    }

    public void assertOutput(GameTestHelper helper, ItemStack expected, String message) {
        ItemStack output = this.output();
        helper.assertTrue(
                ItemStack.matches(expected, output),
                message + ": expected " + describe(expected) + ", got " + describe(output));
    }

    public void assertNoOutput(GameTestHelper helper, String message) {
        this.assertOutput(helper, ItemStack.EMPTY, message);
    }

    private static String describe(ItemStack stack) {
        return stack.isEmpty() ? "nothing" : stack.getCount() + " " + stack.typeHolder().getRegisteredName();
    }
}
