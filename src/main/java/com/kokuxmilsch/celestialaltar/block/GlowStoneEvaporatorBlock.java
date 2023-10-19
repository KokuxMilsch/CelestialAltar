package com.kokuxmilsch.celestialaltar.block;

import com.kokuxmilsch.celestialaltar.block.entity.CelestialAltarBlockEntity;
import com.kokuxmilsch.celestialaltar.block.entity.GlowStoneEvaporatorBlockEntity;
import com.kokuxmilsch.celestialaltar.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class GlowStoneEvaporatorBlock extends BaseEntityBlock {

    public static final BooleanProperty STANDALONE = BooleanProperty.create("standalone");
    public static final EnumProperty<GSE_Part> PART = EnumProperty.create("part", GSE_Part.class);
    public static final IntegerProperty CHARGE = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;

    protected static final VoxelShape TOP_SHAPE = Shapes.or(
            Block.box(0.0D, -16.0D, 0.0D, 16.0D, 0.0D, 16.0D), //Bottom

            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 14.0D, 10.0D) //Top
    );
    protected static final VoxelShape BOTTOM_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), //Bottom

            Block.box(6.0D, 16.0D, 6.0D, 10.0D, 30.0D, 10.0D) //Top
    );

    public GlowStoneEvaporatorBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(STANDALONE, true).setValue(PART, GSE_Part.LOWER).setValue(CHARGE, 0));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide) {
            BlockPos newPos = pPos;
            if(pState.getValue(PART) == GSE_Part.UPPER) {
                if(pLevel.getBlockState(pPos.below()).is(this) && pLevel.getBlockState(pPos.below()).getValue(PART) == GSE_Part.LOWER) {
                    newPos = pPos.below();
                }
            }
            BlockEntity blockEntity = pLevel.getBlockEntity(newPos);
            if(pState.getValue(STANDALONE)) {
                if (blockEntity instanceof GlowStoneEvaporatorBlockEntity) {
                    NetworkHooks.openScreen(((ServerPlayer) pPlayer), (GlowStoneEvaporatorBlockEntity) blockEntity, newPos);
                } else {
                    throw new IllegalStateException("Container provider is missing!");
                }
            } else {
                if (blockEntity instanceof GlowStoneEvaporatorBlockEntity) {
                    CelestialAltarBlockEntity altarBlockEntity = ((GlowStoneEvaporatorBlockEntity) blockEntity).getAltar();
                    if(altarBlockEntity != null) {
                        NetworkHooks.openScreen(((ServerPlayer) pPlayer), altarBlockEntity, newPos);
                    }
                } else {
                    throw new IllegalStateException("Container provider is missing!");
                }
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PART, CHARGE, STANDALONE);
    }

    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            preventCreativeDropFromMiddlePart(pLevel, pPos, pState, pPlayer);
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    protected static void preventCreativeDropFromMiddlePart(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        GSE_Part part = pState.getValue(PART);
        if (part == GSE_Part.UPPER) {
            BlockPos blockpos = pPos.below();
            BlockState blockStateBelow = pLevel.getBlockState(blockpos);
            if (blockStateBelow.is(pState.getBlock()) && blockStateBelow.getValue(PART) == GSE_Part.LOWER) {
                pLevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(Blocks.AIR.defaultBlockState()));
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if(pDirection.getAxis() == Direction.Axis.Y) {
            if(pNeighborState.is(this)) {
                if (pNeighborState.getValue(STANDALONE) && !pState.getValue(STANDALONE)) {
                    return pState.setValue(STANDALONE, true);
                }
                else if (!pNeighborState.getValue(STANDALONE) && pState.getValue(STANDALONE)) {
                    return pState.setValue(STANDALONE, false);
                }
            }
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
            case LOWER -> pLevel.setBlock(pPos.above(), this.defaultBlockState().setValue(PART, GSE_Part.UPPER), 3);
            case UPPER -> pLevel.setBlock(pPos.below(), this.defaultBlockState().setValue(PART, GSE_Part.LOWER), 3);
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction clickedFace = pContext.getClickedFace();
        if(clickedFace.getAxis().isVertical()) {
            if(clickedFace == Direction.UP) {
                return this.defaultBlockState().setValue(PART, GSE_Part.LOWER);
            } else {
                return this.defaultBlockState().setValue(PART, GSE_Part.UPPER);
            }
        }
        return super.getStateForPlacement(pContext);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(PART)) {
            case UPPER -> TOP_SHAPE;
            case LOWER -> BOTTOM_SHAPE;
        };
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getValue(PART) == GSE_Part.UPPER ? null : new GlowStoneEvaporatorBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModBlockEntities.GLOWSTONE_EVAPORATOR_BLOCK_ENTITY.get(), GlowStoneEvaporatorBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(!pLevel.isClientSide) {
            if (!pState.is(pNewState.getBlock())) {
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                if (blockEntity instanceof GlowStoneEvaporatorBlockEntity) {
                    ((GlowStoneEvaporatorBlockEntity) blockEntity).dropItems();
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


    private enum GSE_Part implements StringRepresentable{
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
