package com.kokuxmilsch.celestialaltar.block;

import com.kokuxmilsch.celestialaltar.block.entity.AltarBlockEntity;
import com.kokuxmilsch.celestialaltar.block.entity.ModBlockEntities;
import com.kokuxmilsch.celestialaltar.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;


public class AltarBlock extends BaseEntityBlock {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape VISUAL_SHAPE = Shapes.join(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(1.0D, 8.0D, 1.0D, 15.0D, 16.0D, 15.0D), BooleanOp.OR);



    public AltarBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ACTIVATED);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pHand.equals(InteractionHand.OFF_HAND) && !pState.getValue(ACTIVATED)) {
            ItemStack useItem = pPlayer.getItemInHand(pHand);
            if(useItem.is(ModItems.ENCHANTED_EYE_OF_ENDER.get())) {
                if(!pLevel.isClientSide) {
                    BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                    if (blockEntity instanceof AltarBlockEntity) {
                        if (!((AltarBlockEntity) blockEntity).validMultiblock()) {
                            return InteractionResult.FAIL;
                        }
                    } else {
                        return InteractionResult.FAIL;
                    }
                    ((AltarBlockEntity) blockEntity).activateAltar(pState);
                    useItem.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }


        }

        if(!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof AltarBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer)pPlayer),(AltarBlockEntity) blockEntity, pPos);
            } else {
                throw new IllegalStateException("Container provider is missing!");
            }
        }



        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return VISUAL_SHAPE;
    }

    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AltarBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntity::tick);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        //Ambient Animation
        if(pState.getValue(ACTIVATED)) {
            for(int i = 0; i < 10; ++i) {
                double d1 = pRandom.nextGaussian() * 0.5D; //delta 1
                double d3 = pRandom.nextGaussian() * 0.5D; //delta 2
                double d5 = pRandom.nextGaussian() * 0.5D; //delta 3
                double d6 = pRandom.nextGaussian() * 1;
                double d7 = pRandom.nextGaussian() * 1;
                double d8 = pRandom.nextGaussian() * 1;

                pLevel.addParticle(ParticleTypes.ENCHANT, pPos.getX() + d1, pPos.getY() + d3, pPos.getZ() + d5, d6, d7, d8);

            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(pState != pNewState) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof AltarBlockEntity) {
                ((AltarBlockEntity) blockEntity).dropItems();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
