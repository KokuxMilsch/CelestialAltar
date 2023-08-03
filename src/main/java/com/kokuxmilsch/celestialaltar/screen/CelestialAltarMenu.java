package com.kokuxmilsch.celestialaltar.screen;

import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import com.kokuxmilsch.celestialaltar.block.entity.CelestialAltarBlockEntity;
import com.kokuxmilsch.celestialaltar.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CelestialAltarMenu extends AbstractContainerMenu {

    public final CelestialAltarBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public CelestialAltarMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
    }

    public CelestialAltarMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.CELESTIAL_ALTAR_MENU.get(), id);
        checkContainerSize(inv, CelestialAltarBlockEntity.SLOTS);
        this.blockEntity = (CelestialAltarBlockEntity) blockEntity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);


        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new GlowStoneSlot(handler, 0, 8, 51));
            this.addSlot(new SkyCrystalSlot(handler, 1, 152, 51));
            this.addSlot(new SlotItemHandler(handler, 2, 80, 51));
        });


        addDataSlots(data);
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = CelestialAltarBlockEntity.SLOTS;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    public boolean isCrafting() {
        return data.get(0) > CelestialAltarBlockEntity.preRitualProgressTime;
    }

    public boolean isPreCrafting() {
        return data.get(0) > 0 && data.get(0) <= CelestialAltarBlockEntity.preRitualProgressTime;
    }

    public boolean isMultiblockActive() {
        return this.data.get(5) == 1;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0)-CelestialAltarBlockEntity.preRitualProgressTime;
        int maxProgress = this.data.get(1)-CelestialAltarBlockEntity.preRitualProgressTime;  // Max Progress
        int progressBarSize = 22; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressBarSize / maxProgress : 0;
    }

    public int getScaledPreRitualProgress() {
        int progress = this.data.get(0);
        int maxProgress = CelestialAltarBlockEntity.preRitualProgressTime;  // Max Progress
        int progressBarSize = 49; // This is the width in pixels of your arrow

        return progress != 0 ? progress * progressBarSize / maxProgress : 0;
    }

    public int getScaledGlowStoneCharge() {
        int glowStoneCharge = this.data.get(3);
        int maxGlowStoneCharge = this.data.get(4);  // Max Progress
        int progressBarSize = 32; // This is the height in pixels of your arrow

        return maxGlowStoneCharge != 0 && glowStoneCharge != 0 ? glowStoneCharge * progressBarSize / maxGlowStoneCharge : 0;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.ALTAR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public boolean hasSkyAccess() {
        return this.data.get(2) == 1;
    }

    private static class GlowStoneSlot extends SlotItemHandler {

        public GlowStoneSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(Items.GLOWSTONE_DUST) || stack.is(Items.GLOWSTONE);
        }
    }

    private static class SkyCrystalSlot extends SlotItemHandler {

        public SkyCrystalSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(ModItems.SKY_CRYSTAL_SHARD.get())  || stack.is(ModItems.NIGHT_SKY_CRYSTAL_SHARD.get());
        }
    }
}
