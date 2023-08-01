package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
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

import static com.kokuxmilsch.celestialaltar.block.AltarBlock.ACTIVATED;

public class AltarBlockEntity extends BlockEntity implements MenuProvider {

    public boolean active = false;
    private boolean structure_complete = false;
    private boolean multiblock_destroyed_client = true;
    private boolean multiblock_destroyed_server = true;

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

        validateMultiblock();
        if(validMultiblock()) {
            this.multiblock_destroyed_server = false;
            this.multiblock_destroyed_client = false;
        }

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

        if(!level.isClientSide) {
            pBlockEntity.validateMultiblock();
        }

        if(pBlockEntity.validMultiblock()) {
        } else if(!pBlockEntity.isMultiblockDestroyed(level)){
            pBlockEntity.destroyMultiblock(level, blockState);
        }


        if(level.isClientSide && pBlockEntity.active) {
            for (int i = 0; i < 10; i++) {
                level.addParticle(ParticleTypes.ENCHANT,  blockPos.getX()+((float)i/10), blockPos.getY()+((float)i/10), blockPos.getZ()+((float)i/10), 0,0, 0);
            }
        }


    }

    public void activateAltar(BlockState pState) {
        active = true;
        this.level.setBlock(this.worldPosition, pState.setValue(ACTIVATED, true), 0);
        this.multiblock_destroyed_server = false;
        this.multiblock_destroyed_client = false;
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

    public void destroyMultiblock(Level pLevel, BlockState pBlockState) {
        if(pLevel.isClientSide) {
            System.out.println("destroyed Multiblock clientside");
            pLevel.playLocalSound(this.worldPosition.getX(), this.worldPosition.getX(), this.worldPosition.getX(), SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.MASTER, 1, 2f, false);
            pLevel.playLocalSound(this.worldPosition.getX(), this.worldPosition.getX(), this.worldPosition.getX(), SoundEvents.BEACON_DEACTIVATE, SoundSource.MASTER, 1, 1.5f, false);
            pLevel.addParticle(ParticleTypes.EXPLOSION_EMITTER, false, this.worldPosition.getX() + 0.5, this.worldPosition.getX() + 0.5, this.worldPosition.getX() + 0.5, 0, 0, 0);
            this.multiblock_destroyed_client = true;
        } else {
            System.out.println("destroyed Multiblock serverside");
            active = false;
            pLevel.setBlock(this.worldPosition, pBlockState.setValue(ACTIVATED, false), 2);
            this.multiblock_destroyed_server = true;
        }
    }

    private boolean isMultiblockDestroyed(Level pLevel) {
        return pLevel.isClientSide ? multiblock_destroyed_client : multiblock_destroyed_server;
    }
}
