package com.kokuxmilsch.celestialaltar.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GlowStoneEvaporatorBlock extends RespawnAnchorBlock {

    public static final BooleanProperty STANDALONE = BooleanProperty.create("standalone");
    public static final EnumProperty<GSE_Part> PART = EnumProperty.create("part", GSE_Part.class);

    public GlowStoneEvaporatorBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(STANDALONE, true).setValue(PART, GSE_Part.LOWER).setValue(CHARGE, 0));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PART);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if(pDirection.getAxis() == Direction.Axis.Y) {
            return switch (pState.getValue(PART)) {
                case LOWER -> (pDirection == Direction.UP) ? ((pNeighborState.is(this) && pNeighborState.getValue(PART) == GSE_Part.UPPER) ? pState : Blocks.AIR.defaultBlockState()) : pState;

                case UPPER -> (pDirection == Direction.DOWN) ? ((pNeighborState.is(this) && pNeighborState.getValue(PART) ==GSE_Part.LOWER) ? pState : Blocks.AIR.defaultBlockState()) : pState;
            };
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        GSE_Part part = pState.getValue(PART);

        if(part == GSE_Part.UPPER) {
            return pLevel.getBlockState(pPos.below(1)).is(Blocks.AIR);
        } else if(part == GSE_Part.LOWER) {
            return pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR);

        }
        return super.canSurvive(pState, pLevel, pPos);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        switch (pState.getValue(PART)) {
            case LOWER -> pLevel.setBlock(pPos.above(), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, GSE_Part.UPPER), 3);
            case UPPER -> pLevel.setBlock(pPos.below(), ModBlocks.CELESTIAL_CRYSTAL.get().defaultBlockState().setValue(PART, GSE_Part.LOWER), 3);
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction clickedFace = pContext.getClickedFace();
        if(clickedFace.getAxis().isVertical()) {
            if(clickedFace == Direction.UP) {
                return this.defaultBlockState().setValue(PART, GSE_Part.UPPER);
            } else {
                return this.defaultBlockState().setValue(PART, GSE_Part.LOWER);
            }
        }
        return super.getStateForPlacement(pContext);
    }

    public enum GSE_Part implements StringRepresentable{
        UPPER,
        LOWER;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return switch (this) {
                case UPPER -> "upper";
                case LOWER -> "lower";
            };
        }
    }
}
