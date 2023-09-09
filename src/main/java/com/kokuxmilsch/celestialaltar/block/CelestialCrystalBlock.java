package com.kokuxmilsch.celestialaltar.block;

import com.kokuxmilsch.celestialaltar.block.entity.CelestialCrystalBlockEntity;
import com.kokuxmilsch.celestialaltar.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PART, ACTIVATED, SPLIT);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        String part = pState.getValue(PART).getSerializedName();
        if(part.matches("middle")) {
            return !pLevel.getBlockState(pPos.below(1)).getValue(PART).getSerializedName().matches("bottom") || !pLevel.getBlockState(pPos.above(1)).getValue(PART).getSerializedName().matches("top");
        } else if(part.matches("top")) {
            return !pLevel.getBlockState(pPos.below(1)).getValue(PART).getSerializedName().matches("middle");
        } else if(part.matches("bottom")) {
            return !pLevel.getBlockState(pPos.above(1)).getValue(PART).getSerializedName().matches("middle");

        }
        return super.canSurvive(pState, pLevel, pPos);
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
        if(pState.getValue(PART).getSerializedName().matches("middle")) {
            return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModBlockEntities.CELESTIAL_CRYSTAL_BLOCK_ENTITY.get(), CelestialCrystalBlockEntity::tick);
        }
        return null;
    }


}
