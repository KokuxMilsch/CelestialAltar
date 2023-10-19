package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.block.CelestialAltarBlock;
import com.kokuxmilsch.celestialaltar.block.CelestialCrystalBlock;
import com.kokuxmilsch.celestialaltar.block.GlowStoneEvaporatorBlock;
import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import com.kokuxmilsch.celestialaltar.client.helper.ClientHelper;
import com.kokuxmilsch.celestialaltar.misc.RitualAnimation;
import com.kokuxmilsch.celestialaltar.misc.RitualType;
import com.kokuxmilsch.celestialaltar.multiblock.Multiblock;
import com.kokuxmilsch.celestialaltar.recipe.CelestialAltarRecipe;
import com.kokuxmilsch.celestialaltar.client.screen.CelestialAltarMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.phys.AABB;
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
    private int tickCount = 0;

    private int clientProgress;
    private int progress = 0;
    private int ritualInInt = RitualType.EMPTY.ordinal();
    public static final int maxProgress = 400;
    private int hasSkyAccess = 1;
    private int glowStoneCharge = 0;
    public static final int maxGlowStoneCharge = 16;

    private SimpleContainer craftingInventory;

    public static final int preRitualProgressTime = 120;


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
    private int glowStoneChargeCrafting = 0;

    public CelestialAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CelestialAltarBlockEntity.this.progress;
                    case 1 -> CelestialAltarBlockEntity.this.ritualInInt;
                    case 2 -> CelestialAltarBlockEntity.this.hasSkyAccess;
                    case 3 -> CelestialAltarBlockEntity.this.glowStoneCharge;
                    case 4 -> maxGlowStoneCharge;
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
                }
                ;
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

        if (pBlockEntity.validMultiblock()) {
            pBlockEntity.tickCount++;
            if (pBlockEntity.tickCount >= 40) { //only check every 2 seconds for skylight
                pBlockEntity.checkForClearSky(blockPos);
                pBlockEntity.tickCount = 0;
            }

            if (pBlockEntity.hasSkyAccess == 1) {
                //Process Events
                if (pBlockEntity.hasRecipe() && pBlockEntity.progress <= maxProgress) {
                    resumeProgress((ServerLevel) level, blockPos, pBlockEntity);
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
        if (recipe.isPresent()) {
            this.ritualInInt = recipe.get().getRitualType().ordinal();
            return (this.glowStoneCharge >= recipe.get().getGlowstone() || this.glowStoneChargeCrafting >= recipe.get().getGlowstone());
        } else {
            this.ritualInInt = RitualType.EMPTY.ordinal();
        }
        return false;
    }

    public static void resumeProgress(ServerLevel pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        //update the blockState so the client can render the beam
        pLevel.setBlock(pPos, pLevel.getBlockState(pPos).setValue(CelestialAltarBlock.CRAFTING, true), 3);

        pBlockEntity.progress++;
        if (pBlockEntity.progress == preRitualProgressTime) {
            submitItems(pLevel, pPos, pBlockEntity);
        }
        if (pBlockEntity.progress >= maxProgress) {
            finishRitual(pBlockEntity);
        }
        if (pBlockEntity.progress == 320) {
            changeWeatherOrTime(pLevel, pPos, pBlockEntity);
        }
        RitualAnimation.animateServer(pLevel, pPos, pBlockEntity.progress);
    }

    public static void submitItems(ServerLevel pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        pBlockEntity.craftingInventory = new SimpleContainer(pBlockEntity.itemStackHandler.getSlots());
        for (int i = 0; i < pBlockEntity.itemStackHandler.getSlots(); i++) {
            pBlockEntity.craftingInventory.setItem(i, pBlockEntity.itemStackHandler.getStackInSlot(i).copy());
        }
        pBlockEntity.glowStoneChargeCrafting = pBlockEntity.glowStoneCharge;
        Optional<CelestialAltarRecipe> recipe = pBlockEntity.getRecipe();
        pBlockEntity.itemStackHandler.getStackInSlot(2).shrink(1);
        pBlockEntity.itemStackHandler.getStackInSlot(1).shrink(1);
        pBlockEntity.glowStoneCharge = pBlockEntity.glowStoneCharge - recipe.get().getGlowstone();
        pLevel.playSound(null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 0.8f, 1);
        for (int i = 0; i < 40; i++) {
            pLevel.sendParticles(ParticleTypes.FLAME, pPos.getX() + 0.5, pPos.getY() + 1.5, pPos.getZ() + 0.5, 1, 0.3, 0.3, 0.3, 0);
        }
    }

    public Optional<CelestialAltarRecipe> getRecipe() {
        SimpleContainer inventory;
        if (this.progress < preRitualProgressTime) {
            inventory = new SimpleContainer(this.itemStackHandler.getSlots());
            for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
                inventory.setItem(i, this.itemStackHandler.getStackInSlot(i));
            }
        } else {
            if (this.craftingInventory != null) {
                inventory = this.craftingInventory;
            } else {
                resetProgress();
                return Optional.empty();
            }
        }
        return this.level.getRecipeManager().getRecipeFor(CelestialAltarRecipe.Type.INSTANCE, inventory, this.level);
    }

    public static void playSound(ServerLevel pLevel, SoundEvent soundEvent, BlockPos pPos, float pPitch, float pVolume) {
        pLevel.playSound(null, pPos, soundEvent, SoundSource.AMBIENT, pVolume, pPitch);
    }

    public static void changeWeatherOrTime(ServerLevel pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        Optional<CelestialAltarRecipe> recipe = pBlockEntity.getRecipe();
        //pLevel.addFreshEntity(new ItemEntity(pLevel, pPos.getX(), pPos.getY()+1, pPos.getZ(), recipe.get().getResultItem(RegistryAccess.EMPTY)));
        switch (recipe.get().getRitualType()) {
            case DAY -> pLevel.setDayTime(1000);
            case NIGHT -> pLevel.setDayTime(14000);
            case SUNNY -> pLevel.setWeatherParameters(1500, 0, false, false);
            case RAIN -> pLevel.setWeatherParameters(0, 1500, true, false);
            case THUNDER -> pLevel.setWeatherParameters(0, 1500, true, true);
            case EMPTY -> pLevel.players().get(0).sendSystemMessage(Component.literal("Ritual does: ...........nothing."));
        }
    }

    public static void finishRitual(CelestialAltarBlockEntity pBlockEntity) {
        pBlockEntity.glowStoneChargeCrafting = 0;
        pBlockEntity.resetProgress();
    }

    public void resetProgress() {
        this.progress = 0;
        this.ritualInInt = RitualType.EMPTY.ordinal();
        this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(CelestialAltarBlock.CRAFTING, false), 3);

    }

    public void chargeGlowStone() {
        ItemStack glowStone = this.itemStackHandler.getStackInSlot(0);
        if (!glowStone.isEmpty() && this.glowStoneCharge < maxGlowStoneCharge) {
            if (glowStone.is(Items.GLOWSTONE_DUST)) {
                this.glowStoneCharge++;
                glowStone.shrink(1);
            } else if (glowStone.is(Items.GLOWSTONE)) {
                this.glowStoneCharge = this.glowStoneCharge + 4;
                glowStone.shrink(1);
            }
        }
    }

    public void activateAltar(BlockState pState) {
        active = true;
        this.level.playSound(null, this.worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.MASTER, 1f, 1.4f);
        this.level.setBlock(this.worldPosition, pState.setValue(CelestialAltarBlock.ACTIVE, true), 3);
        this.level.setBlock(this.worldPosition.above(4), this.level.getBlockState(this.worldPosition.above(4)).trySetValue(CelestialCrystalBlock.ACTIVATED, true), 3);

        this.level.setBlock(this.worldPosition.offset(2, 2, 2), this.level.getBlockState(this.worldPosition.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, false), 3);
        this.level.setBlock(this.worldPosition.offset(-2, 2, 2), this.level.getBlockState(this.worldPosition.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, false), 3);
        this.level.setBlock(this.worldPosition.offset(2, 2, -2), this.level.getBlockState(this.worldPosition.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, false), 3);
        this.level.setBlock(this.worldPosition.offset(-2, 2, -2), this.level.getBlockState(this.worldPosition.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, false), 3);
    }

    public void validateMultiblock(Level pLevel, BlockPos pBlockPos) {
        if (Multiblock.scanMultiblock(Multiblock.CELESTIAL_ALTAR_MULTIBLOCK, pBlockPos, pLevel)) {
            this.structure_complete = true;
            if (!this.active) {
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
        BlockPos pos = pPos;
        for (int i = 0; i < (this.level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) - pPos.getY()); i++) {
            pos = pos.above();
            if (!this.level.getBlockState(pos).is(Blocks.AIR) && !this.level.getBlockState(pos).is(ModBlocks.CELESTIAL_CRYSTAL.get())) {
                this.hasSkyAccess = 0;
                return false;
            }
        }
        this.hasSkyAccess = 1;
        return true;
    }

    public void destroyMultiblock(BlockState pBlockState) {
        if (!active) {
            return;
        }
        active = false;
        this.resetProgress();

        if (this.level instanceof ServerLevel serverLevel) {

            serverLevel.playSound(null, this.worldPosition, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.MASTER, 1, 1.2f);
            serverLevel.playSound(null, this.worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.MASTER, 1, 1.2f);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 2.5, this.worldPosition.getZ() + 0.5, 1, 0, 0, 0, 0);

        }
        this.level.setBlock(this.worldPosition.offset(2, 2, 2), this.level.getBlockState(this.worldPosition.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, true), 3);
        this.level.setBlock(this.worldPosition.offset(2, 2, -2), this.level.getBlockState(this.worldPosition.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, true), 3);
        this.level.setBlock(this.worldPosition.offset(-2, 2, 2), this.level.getBlockState(this.worldPosition.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, true), 3);
        this.level.setBlock(this.worldPosition.offset(-2, 2, -2), this.level.getBlockState(this.worldPosition.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0).trySetValue(GlowStoneEvaporatorBlock.STANDALONE, true), 3);

        this.level.setBlock(this.worldPosition.above(4), this.level.getBlockState(this.worldPosition.above(4)).trySetValue(CelestialCrystalBlock.ACTIVATED, false).trySetValue(CelestialCrystalBlock.SPLIT, false), 2);
        this.level.setBlock(this.worldPosition, pBlockState.setValue(CelestialAltarBlock.ACTIVE, false).setValue(CelestialAltarBlock.CRAFTING, false), 3);

    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, CelestialAltarBlockEntity pBlockEntity) {
        if (blockState.getValue(CelestialAltarBlock.CRAFTING)) {
            pBlockEntity.clientProgress++;
            if (level instanceof ClientLevel) {
                RitualAnimation.animateClient((ClientLevel) level, blockPos, pBlockEntity.clientProgress);
            }
        } else {
            pBlockEntity.clientProgress = 0;
        }
    }


    public int getClientProgress() {
        return this.clientProgress;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
