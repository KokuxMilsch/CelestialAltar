package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.block.CelestialAltarBlock;
import com.kokuxmilsch.celestialaltar.block.GlowStoneEvaporatorBlock;
import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import com.kokuxmilsch.celestialaltar.item.ModItems;
import com.kokuxmilsch.celestialaltar.misc.RitualType;
import com.kokuxmilsch.celestialaltar.multiblock.Multiblock;
import com.kokuxmilsch.celestialaltar.recipe.CelestialAltarRecipe;
import com.kokuxmilsch.celestialaltar.recipe.ModRecipes;
import com.kokuxmilsch.celestialaltar.screen.CelestialAltarMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
    private int ritualInInt = 0;
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
        if(recipe.isPresent()) {
            this.ritualInInt = recipe.get().getRitualType().ordinal();
            return (this.glowStoneCharge >= recipe.get().getGlowstone() || this.glowStoneChargeCrafting >= recipe.get().getGlowstone());
        }
        return false;
    }

    public static void resumeProgress(ServerLevel pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        //
        pBlockEntity.progress++;
        if(pBlockEntity.progress == preRitualProgressTime) {
            submitItems(pLevel, pPos, pBlockEntity);
        }
        if(pBlockEntity.progress >= maxProgress) {
            finishRitual(pBlockEntity);
        }
        if(pBlockEntity.progress == 320) {
            changeWeatherOrTime(pLevel, pPos, pBlockEntity);
        }
        ritualAnimation(pLevel, pPos, pBlockEntity.progress);
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
        pBlockEntity.glowStoneCharge = pBlockEntity.glowStoneCharge-recipe.get().getGlowstone();
        pLevel.playSound(null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1, 1);
        pLevel.sendParticles(ParticleTypes.FLAME, pPos.getX()+0.5, pPos.getY()+1.5, pPos.getZ()+0.5, 20, 0, 0, 0, 0);
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

    public static void ritualAnimation(ServerLevel pLevel, BlockPos pPos, int progress) {
        //max Progress = 400t
        if(progress == 1) {
            playSound(pLevel, SoundEvents.BEACON_POWER_SELECT, pPos, 0.8f, 2.5f);
        }
        if(progress == 30) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if(progress == 40) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if(progress == 50) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if(progress == 60) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if(progress > 60 && progress < 110) {
            //beam animation
            double increment = (double) Math.round((((double) (progress-60))*0.03333D)*100)/100;
            BlockPos particlePos = pPos.offset(2, 4, 2);
            pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX()+0.5-increment, particlePos.getY()+(increment/2), particlePos.getZ()+0.5-increment, 1, 0, 0, 0, 0);

            particlePos = pPos.offset(2, 4, -2);
            pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX()+0.5-increment, particlePos.getY()+(increment/2), particlePos.getZ()+0.5+increment, 1, 0, 0, 0, 0);

            particlePos = pPos.offset(-2, 4, 2);
            pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX()+0.5+increment, particlePos.getY()+(increment/2), particlePos.getZ()+0.5-increment, 1, 0, 0, 0, 0);

            particlePos = pPos.offset(-2, 4, -2);
            pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX()+0.5+increment, particlePos.getY()+(increment/2), particlePos.getZ()+0.5+increment, 1, 0, 0, 0, 0);

            playSound(pLevel, SoundEvents.BEACON_POWER_SELECT, pPos, 2f, 1f);
        }
        if(progress == 120) {
            playSound(pLevel, SoundEvents.BEACON_ACTIVATE, pPos, 1.2f, 3f);
            pLevel.sendParticles(ParticleTypes.EXPLOSION, pPos.getX()+0.5, pPos.getY()+4.5, pPos.getZ()+0.5, 4, 0, 0, 0, 1);

        }
        if(progress >= 120 && progress <= 124) {
            pLevel.sendParticles(ParticleTypes.FLASH, pPos.getX()+0.5, pPos.getY()+4.5, pPos.getZ()+0.5, 1, 0, 0, 0, 0);
        }
        if(progress == 125) {
            playSound(pLevel, SoundEvents.BEACON_POWER_SELECT, pPos, 1f, 2.5f);
            for (int i = 0; i < 10; i++) {
                playSound(pLevel, SoundEvents.PORTAL_AMBIENT, pPos, 0.5f, 2f);
            }
        }
        if(progress == 300) {
            playSound(pLevel, SoundEvents.ENDER_DRAGON_GROWL, pPos, 0.5f, 1f);
            playSound(pLevel, SoundEvents.BEACON_DEACTIVATE, pPos, 0.5f, 1f);
        }
        if(progress >= 300 && progress < 320 && progress % 2 == 0) {
            pLevel.sendParticles(ParticleTypes.SONIC_BOOM, pPos.getX()+0.5, pPos.getY()+0.5+(double)((progress-300)/2), pPos.getZ()+0.5, 2, 0, 0, 0, 1);
        }
        if(progress == 280) {
            for (int i = 0; i < 4; i++) {
                playSound(pLevel, SoundEvents.BELL_RESONATE, pPos, 0.5f, 4);
            }
        }
        if(progress == 399) {
            playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, pPos, 2f, 1f);
            playSound(pLevel, SoundEvents.BEACON_DEACTIVATE, pPos, 0.9f, 2f);
            playSound(pLevel, SoundEvents.LIGHTNING_BOLT_THUNDER, pPos, 0.5f, 3f);
            playSound(pLevel, SoundEvents.END_PORTAL_SPAWN, pPos, 0.5f, 3f);
            pLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pPos.getX()+0.5, pPos.getY()+4.5, pPos.getZ()+0.5, 1, 0, 0, 0, 1);

            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
        }
    }

    public static void playSound(ServerLevel pLevel, SoundEvent soundEvent, BlockPos pPos, float pPitch, float pVolume) {
        pLevel.playSound(null, pPos,soundEvent, SoundSource.AMBIENT, pVolume, pPitch);
    }

    public static void changeWeatherOrTime(ServerLevel pLevel, BlockPos pPos, CelestialAltarBlockEntity pBlockEntity) {
        Optional<CelestialAltarRecipe> recipe = pBlockEntity.getRecipe();
        //pLevel.addFreshEntity(new ItemEntity(pLevel, pPos.getX(), pPos.getY()+1, pPos.getZ(), recipe.get().getResultItem(RegistryAccess.EMPTY)));
        switch (recipe.get().getRitualType()) {
            case DAY -> pLevel.players().get(0).sendSystemMessage(Component.literal("DAYTIME"));
            case NIGHT -> pLevel.players().get(0).sendSystemMessage(Component.literal("NIGHT"));
            case SUNNY -> pLevel.setWeatherParameters(1000, 0, false, false);
            case RAIN -> pLevel.setWeatherParameters(0, 1000, true, false);
            case THUNDER -> pLevel.setWeatherParameters(0, 1000, true, true);
            case EMPTY -> pLevel.players().get(0).sendSystemMessage(Component.literal("Ritual does: ...........nothing."));
        }
    }

    public static void finishRitual(CelestialAltarBlockEntity pBlockEntity) {
        pBlockEntity.glowStoneChargeCrafting = 0;
        pBlockEntity.resetProgress();
    }

    public void resetProgress() {
        this.progress = 0;
    }

    public void chargeGlowStone() {
        ItemStack glowStone = this.itemStackHandler.getStackInSlot(0);
        if(!glowStone.isEmpty() && this.glowStoneCharge < maxGlowStoneCharge) {
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
        this.level.setBlock(this.worldPosition.offset(2, 2, 2), this.level.getBlockState(this.worldPosition.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
        this.level.setBlock(this.worldPosition.offset(2, 2, -2), this.level.getBlockState(this.worldPosition.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
        this.level.setBlock(this.worldPosition.offset(-2, 2, 2), this.level.getBlockState(this.worldPosition.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
        this.level.setBlock(this.worldPosition.offset(-2, 2, -2), this.level.getBlockState(this.worldPosition.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
        this.level.setBlock(this.worldPosition, pBlockState.setValue(CelestialAltarBlock.ACTIVATED, false), 3);
    }
}
