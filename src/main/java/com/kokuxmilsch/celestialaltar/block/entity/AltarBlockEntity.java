package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.block.AltarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.kokuxmilsch.celestialaltar.block.AltarBlock.ACTIVATED;

public class AltarBlockEntity extends BlockEntity implements MenuProvider {

    public boolean active = false;
    private boolean structure_complete = false;

    private final ItemStackHandler itemStackHandler = new ItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public AltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu." + CelestialAltar.MODID + ".altar");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putBoolean("active", active);
        nbt.putBoolean("structure_complete", structure_complete);
        nbt.put("inventory", itemStackHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        active = nbt.getBoolean("active");
        structure_complete = nbt.getBoolean("structure_complete");
        itemStackHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void dropItems() {
        SimpleContainer temp = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            temp.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, temp);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, AltarBlockEntity pBlockEntity) {


        pBlockEntity.validateMultiblock();


        if(pBlockEntity.validMultiblock()) {
            //Process Events
        } else {
            pBlockEntity.destroyMultiblock(blockState);
        }
    }

    public void activateAltar(BlockState pState) {
        active = true;
        this.level.playSound(null, this.worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.MASTER, 1f, 1.4f);
        this.level.setBlock(this.worldPosition, pState.setValue(ACTIVATED, true), 3);
    }

    public void validateMultiblock() {
        if (this.level.getBlockState(this.worldPosition.below(1)).is(Blocks.BEACON)) {
            this.structure_complete = true;
            return;
        }
        this.structure_complete = false;
    }

    public boolean validMultiblock() {
        return this.structure_complete;
    }

    public void destroyMultiblock(BlockState pBlockState) {
        if(!active) {
            return;
        }
        active = false;

        if(this.level instanceof ServerLevel serverLevel) {

            serverLevel.playSound(null, this.worldPosition, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.MASTER, 1, 1.2f);
            serverLevel.playSound(null, this.worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.MASTER, 1, 1.2f);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 2.5, this.worldPosition.getZ() + 0.5, 1, 0, 0, 0, 0);

        }
        this.level.setBlock(this.worldPosition, pBlockState.setValue(ACTIVATED, false), 3);
    }
}
