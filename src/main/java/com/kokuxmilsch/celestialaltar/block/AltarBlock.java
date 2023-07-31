package com.kokuxmilsch.celestialaltar.block;

import com.kokuxmilsch.celestialaltar.item.ModItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class AltarBlock extends Block {

    protected static final VoxelShape SHAPE = Shapes.join(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(1.0D, 8.0D, 1.0D, 15.0D, 16.0D, 15.0D), BooleanOp.OR);

    public AltarBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide) {
            ItemStack useItem = pPlayer.getUseItem();
            if (true) { //this if statement doesn't work
                pLevel.playSound(pPlayer, pPos, SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 1, 2); //This mf doesn't work
                pPlayer.sendSystemMessage(Component.literal("USED ENDER EYE!"));
                useItem.shrink(1); //This mf doesn't work
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}
