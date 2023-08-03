package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.block.CelestialAltarBlock;
import com.kokuxmilsch.celestialaltar.item.ModItems;
import com.kokuxmilsch.celestialaltar.multiblock.Multiblock;
import com.kokuxmilsch.celestialaltar.recipe.CelestialAltarRecipe;
import com.kokuxmilsch.celestialaltar.recipe.ModRecipes;
import com.kokuxmilsch.celestialaltar.screen.CelestialAltarMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class CelestialAltarBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOTS = 3;

    public boolean active = false;
    public int active_int = 0;
    private boolean structure_complete = false;

    private int progress = 0;
    private final int maxProgress = 400;
    private int hasSkyAccess = 1;
    private int glowStoneCharge = 0;
    private final int maxGlowStoneCharge = 16;

    private SimpleContainer craftingInventory;

    public static final int preRitualProgressTime = 100;


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

    public CelestialAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CelestialAltarBlockEntity.this.progress;
                    case 1 -> CelestialAltarBlockEntity.this.maxProgress;
                    case 2 -> CelestialAltarBlockEntity.this.hasSkyAccess;
                    case 3 -> CelestialAltarBlockEntity.this.glowStoneCharge;
                    case 4 -> CelestialAltarBlockEntity.this.maxGlowStoneCharge;
                    case 5 -> CelestialAltarBlockEntity.this.active_int;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CelestialAltarBlockEntity.this.progress = pValue;
                    case 2 -> CelestialAltarBlockEntity.this.hasSkyAccess = pValue;
                    case 3 -> CelestialAltarBlockEntity.this.glowStoneCharge = pValue;
                };
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu." + CelestialAltar.MODID + ".celestial_altar");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CelestialAltarMenu(pContainerId, pPlayerInventory, this, this.data);
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
        nbt.putBoolean("active", active);
        nbt.putBoolean("structure_complete", structure_complete);
        nbt.putInt("glowstone_charge", glowStoneCharge);
        nbt.putInt("progress", progress);
        nbt.put("inventory", itemStackHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        active = nbt.getBoolean("active");
        structure_complete = nbt.getBoolean("structure_complete");
        glowStoneCharge = nbt.getInt("glowstone_charge");
        progress = nbt.getInt("progress");
        itemStackHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void dropItems() {
        SimpleContainer temp = new SimpleContainer(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            temp.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, temp);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, CelestialAltarBlockEntity pBlockEntity) {
        pBlockEntity.active_int = pBlockEntity.active ? 1 : 0;

        pBlockEntity.validateMultiblock(level, blockPos);

        if(pBlockEntity.validMultiblock()) {
            if(pBlockEntity.checkForClearSky(blockPos)) {
                //Process Events
                if (pBlockEntity.hasRecipe() && pBlockEntity.progress <= pBlockEntity.maxProgress) {
                    resumeProgress(level, blockPos, pBlockEntity);
                } else {
                    pBlockEntity.resetProgress();
                }
            }
            pBlockEntity.chargeGlowStone();

        } else {
            pBlockEntity.destroyMultiblock(blockState);
        }
    }

    public boolean hasRecipe() {
        Optional<CelestialAltarRecipe> recipe = this.getRecipe();
        return recipe.isPresent() && this.glowStoneCharge >= recipe.get().getGlowstone();
    }

    public static void resumeProgress(Level pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        //
        pBlockEntity.progress++;
        if(pBlockEntity.progress == preRitualProgressTime) {
            submitItems((ServerLevel) pLevel, pPos, pBlockEntity);
        }
        if(pBlockEntity.progress >= pBlockEntity.maxProgress) {
            finishRitual(pLevel, pPos, pBlockEntity);
        }
    }

    public static void submitItems(ServerLevel pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        pBlockEntity.craftingInventory = new SimpleContainer(pBlockEntity.itemStackHandler.getSlots());
        for (int i = 0; i < pBlockEntity.itemStackHandler.getSlots(); i++) {
            pBlockEntity.craftingInventory.setItem(i, pBlockEntity.itemStackHandler.getStackInSlot(i).copy());
        }
        Optional<CelestialAltarRecipe> recipe = pBlockEntity.getRecipe();
        pBlockEntity.itemStackHandler.getStackInSlot(2).shrink(1);
        pBlockEntity.itemStackHandler.getStackInSlot(1).shrink(1);
        pBlockEntity.glowStoneCharge = pBlockEntity.glowStoneCharge-recipe.get().getGlowstone();
        pLevel.playSound(null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1, 1);
        pLevel.sendParticles(ParticleTypes.FLAME, pPos.getX(), pPos.getY()+1, pPos.getZ(), 10, 0.2, 0.2, 0.2, 0);
    }

    public Optional<CelestialAltarRecipe> getRecipe() {
        SimpleContainer inventory;
        if(this.progress < preRitualProgressTime) {
            inventory = new SimpleContainer(this.itemStackHandler.getSlots());
            for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
                inventory.setItem(i, this.itemStackHandler.getStackInSlot(i));
            }
        } else {
            inventory = this.craftingInventory;
        }
        return this.level.getRecipeManager().getRecipeFor(CelestialAltarRecipe.Type.INSTANCE, inventory, this.level);
    }

    public static void finishRitual(Level pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        Optional<CelestialAltarRecipe> recipe = pBlockEntity.getRecipe();
        pLevel.addFreshEntity(new ItemEntity(pLevel, pPos.getX(), pPos.getY()+1, pPos.getZ(), recipe.get().getResultItem(RegistryAccess.EMPTY)));
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("finished Ritual!!!"));
        pBlockEntity.resetProgress();
    }

    public void resetProgress() {
        this.progress = 0;
    }

    public void chargeGlowStone() {
        ItemStack glowStone = this.itemStackHandler.getStackInSlot(0);
        if(!glowStone.isEmpty() && this.glowStoneCharge < this.maxGlowStoneCharge) {
            if(glowStone.is(Items.GLOWSTONE_DUST)) {
                this.glowStoneCharge++;
                glowStone.shrink(1);
            } else if(glowStone.is(Items.GLOWSTONE)){
                this.glowStoneCharge = this.glowStoneCharge + 4;
                glowStone.shrink(1);
            }
        }
    }

    public void activateAltar(BlockState pState) {
        active = true;
        this.level.playSound(null, this.worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.MASTER, 1f, 1.4f);
        this.level.setBlock(this.worldPosition, pState.setValue(CelestialAltarBlock.ACTIVATED, true), 3);
    }

    public void validateMultiblock(Level pLevel, BlockPos pBlockPos) {
        if (Multiblock.scanMultiblock(Multiblock.CELESTIAL_ALTAR_MULTIBLOCK, pBlockPos, pLevel)) {
            this.structure_complete = true;
            if(!this.active) {
                activateAltar(pLevel.getBlockState(pBlockPos));
            }
            return;
        }
        this.structure_complete = false;
    }

    public boolean validMultiblock() {
        return this.structure_complete;
    }

    public boolean checkForClearSky(BlockPos pPos) {
        BlockPos pos = pPos.above(1); //start above the celestial crystal
        for (int i = 0; i < (this.level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ())-pos.getY())-1; i++) {
            if(!this.level.getBlockState(pos.above(i)).is(Blocks.AIR)) {
                this.hasSkyAccess = 0;
                return false;
            }
        }
        this.hasSkyAccess = 1;
        return true;
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
        this.level.setBlock(this.worldPosition, pBlockState.setValue(CelestialAltarBlock.ACTIVATED, false), 3);
    }
}
