package com.kokuxmilsch.celestialaltar.block;

import com.kokuxmilsch.celestialaltar.block.entity.CelestialCrystalBlockEntity;
import com.kokuxmilsch.celestialaltar.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CelestialCrystalBlock extends BaseEntityBlock {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final BooleanProperty SPLIT = BooleanProperty.create("split");
    public static final EnumProperty<CrystalParts> PART = EnumProperty.create("part", CrystalParts.class);
    protected static final VoxelShape TOP_SHAPE = Shapes.or(
            Block.box(2.0D, -1.0D, 2.0D, 14.0D, 7.0D, 14.0D), //top
            Block.box(5.0D, 7.0D, 5.0D, 11.0D, 13.0D, 11.0D), //top
            Block.box(0.0D, 1.0D -16D, 0.0D, 16.0D, 15.0D -16D, 16.0D), //middle
            Block.box(2.0D, 9.0D -32D, 2.0D, 14.0D, 17.0D -32D, 14.0D), //
            Block.box(5.0D, 3.0D -32D, 5.0D, 11.0D, 9.0D -32D, 11.0D)   //bottom
    );
    protected static final VoxelShape MIDDLE_SHAPE = Shapes.or(
            Block.box(2.0D, -1.0D +16D, 2.0D, 14.0D, 7.0D +16D, 14.0D), //top
            Block.box(5.0D, 7.0D +16D, 5.0D, 11.0D, 13.0D +16D, 11.0D), //
            Block.box(0.0D, 1.0D, 0.0D, 16.0D, 15.0D, 16.0D), //middle
            Block.box(2.0D, 9.0D -16D, 2.0D, 14.0D, 17.0D -16D, 14.0D), //
            Block.box(5.0D, 3.0D -16D, 5.0D, 11.0D, 9.0D -16D, 11.0D)   //bottom
    );
    protected static final VoxelShape BOTTOM_SHAPE = Shapes.or(
            Block.box(2.0D, -1.0D +32D, 2.0D, 14.0D, 7.0D +32D, 14.0D), //top
            Block.box(5.0D, 7.0D +32D, 5.0D, 11.0D, 13.0D +32D, 11.0D), //
            Block.box(0.0D, 1.0D +16D, 0.0D, 16.0D, 15.0D +16D, 16.0D), //middle
            Block.box(2.0D, 9.0D, 2.0D, 14.0D, 17.0D, 14.0D), //
            Block.box(5.0D, 3.0D, 5.0D, 11.0D, 9.0D, 11.0D)   //bottom
    );

    public CelestialCrystalBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVATED, false).setValue(SPLIT, false).setValue(PART, CrystalParts.MIDDLE));
    }

    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            preventCreativeDropFromMiddlePart(pLevel, pPos, pState, pPlayer);
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    protected static void preventCreativeDropFromMiddlePart(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        CrystalParts part = pState.getValue(PART);
        if (part == CrystalParts.TOP) {
            BlockPos blockpos = pPos.below();
            BlockState blockStateBelow = pLevel.getBlockState(blockpos);
            if (blockStateBelow.is(pState.getBlock()) && blockStateBelow.getValue(PART) == CrystalParts.MIDDLE) {
                pLevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(Blocks.AIR.defaultBlockState()));
            }
        } else if(part == CrystalParts.BOTTOM) {
            BlockPos blockPos = pPos.above();
            BlockState blockStateAbove = pLevel.getBlockState(blockPos);
            if (blockStateAbove.is(pState.getBlock()) && blockStateAbove.getValue(PART) == CrystalParts.MIDDLE) {
                pLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
                pLevel.levelEvent(pPlayer, 2001, blockPos, Block.getId(Blocks.AIR.defaultBlockState()));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PART, ACTIVATED, SPLIT);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if(pDirection.getAxis() == Direction.Axis.Y) {
            if(pNeighborState.is(this)) {
                if (pNeighborState.getValue(SPLIT) && !pState.getValue(SPLIT)) {
                    return pState.setValue(SPLIT, true);
                }
                else if (!pNeighborState.getValue(SPLIT) && pState.getValue(SPLIT)) {
                    return pState.setValue(SPLIT, false);
                }
            }
            return switch (pState.getValue(PART)) {
                case BOTTOM -> (pDirection == Direction.UP) ? ((pNeighborState.is(this) && pNeighborState.getValue(PART) == CrystalParts.MIDDLE) ? pState : Blocks.AIR.defaultBlockState()) : pState;

                case MIDDLE -> (pNeighborState.is(this) && ((pNeighborState.getValue(PART) == CrystalParts.BOTTOM) || (pNeighborState.getValue(PART) == CrystalParts.TOP))) ? pState : Blocks.AIR.defaultBlockState();

                case TOP -> (pDirection == Direction.DOWN) ? ((pNeighborState.is(this) && pNeighborState.getValue(PART) == CrystalParts.MIDDLE) ? pState : Blocks.AIR.defaultBlockState()) : pState;
            };
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        CrystalParts part = pState.getValue(PART);

        if(part == CrystalParts.MIDDLE) {
            return pLevel.getBlockState(pPos.below(1)).is(Blocks.AIR) && pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR);
        } else if(part == CrystalParts.TOP) {
            return pLevel.getBlockState(pPos.below(1)).is(Blocks.AIR) && pLevel.getBlockState(pPos.below(2)).is(Blocks.AIR);
        } else if(part == CrystalParts.BOTTOM) {
            return pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) && pLevel.getBlockState(pPos.above(2)).is(Blocks.AIR);

        }
        return super.canSurvive(pState, pLevel, pPos);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        switch (pState.getValue(PART)) {
            case BOTTOM -> {
                pLevel.setBlock(pPos.above(), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, CrystalParts.MIDDLE), 3);
                pLevel.setBlock(pPos.above(2), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, CrystalParts.TOP), 3);
            }
            case MIDDLE -> {
                pLevel.setBlock(pPos.below(), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, CrystalParts.BOTTOM), 3);
                pLevel.setBlock(pPos.above(), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, CrystalParts.TOP), 3);
            }
            case TOP -> {
                pLevel.setBlock(pPos.below(), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, CrystalParts.MIDDLE), 3);
                pLevel.setBlock(pPos.below(2), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, CrystalParts.BOTTOM), 3);
            }
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction clickedFace = pContext.getClickedFace();
        if(clickedFace.getAxis().isVertical()) {
            if(clickedFace == Direction.UP) {
                return this.defaultBlockState().setValue(PART, CrystalParts.BOTTOM);
            } else {
                return this.defaultBlockState().setValue(PART, CrystalParts.TOP);
            }
        }
        return super.getStateForPlacement(pContext);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(PART)) {
            case TOP -> TOP_SHAPE;
            case MIDDLE -> MIDDLE_SHAPE;
            case BOTTOM -> BOTTOM_SHAPE;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CelestialCrystalBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pState.getValue(PART) == CrystalParts.MIDDLE) {
            return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModBlockEntities.CELESTIAL_CRYSTAL_BLOCK_ENTITY.get(), CelestialCrystalBlockEntity::tick);
        }
        return null;
    }


}
