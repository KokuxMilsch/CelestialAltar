package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.menu.CelestialAltarMenu;
import com.kokuxmilsch.celestialaltar.menu.GlowStoneEvaporatorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class GlowStoneEvaporatorBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOTS = 5; //coal, glowstone, base item, catalyst (eg. lightning rod for lightning stell)

    private int progress = 0;
    private final int maxProgress = 100;
    private int burnTime = 0;

    private BlockPos altarPos;

    private final ItemStackHandler itemStackHandler = new ItemStackHandler(SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public void setSize(int size) {
            super.setSize(SLOTS);
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler;

    protected final ContainerData data;

    public GlowStoneEvaporatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GLOWSTONE_EVAPORATOR_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> burnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> progress = pValue;
                    case 2 -> burnTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu." + CelestialAltar.MODID + ".glowstone_evaporator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new GlowStoneEvaporatorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);

    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemStackHandler.serializeNBT());
        nbt.putInt("progress", this.progress);
        nbt.putInt("burnTime", this.burnTime);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemStackHandler.deserializeNBT(nbt.getCompound("inventory"));
        this.progress = nbt.getInt("progress");
        this.burnTime = nbt.getInt("burnTime");
    }

    public void dropItems() {
        SimpleContainer temp = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            temp.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, temp);
    }

    public void setAltar(@Nullable BlockPos altarBlockPos) {
        this.altarPos = altarBlockPos;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, GlowStoneEvaporatorBlockEntity pBlockEntity) {
        if(pBlockEntity.hasRecipe()) {
            pBlockEntity.progress++;
            if(pBlockEntity.progress > pBlockEntity.maxProgress) {
                craftItem(pBlockEntity, level);
            }
        } else {
            pBlockEntity.resetProgress();
        }
    }

    public boolean hasRecipe() {
        return this.itemStackHandler.getStackInSlot(1).is(Items.GLOWSTONE_DUST) && this.itemStackHandler.getStackInSlot(2).is(Items.IRON_INGOT);
    }

    public void resetProgress() {
        this.progress = 0;
    }

    public static void craftItem(GlowStoneEvaporatorBlockEntity pBlockEntity, Level pLevel) {
        pBlockEntity.progress = 0;
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("CRAFTED ITEM!!"));
    }

    public @Nullable BlockPos getAltarPos() {
        return this.altarPos;
    }
}
