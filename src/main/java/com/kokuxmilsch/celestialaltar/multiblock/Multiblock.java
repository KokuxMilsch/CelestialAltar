package com.kokuxmilsch.celestialaltar.multiblock;

import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;


public class Multiblock {

    public final String name;
    public final Block centerBlock;
    public final BlockEntry[] blocks;

    public static final Multiblock CELESTIAL_ALTAR_MULTIBLOCK = new Multiblock("celestial_altar", ModBlocks.ALTAR.get(),
            new BlockEntry(1, -1, 1, Blocks.CRYING_OBSIDIAN),
            new BlockEntry(1, -1, 0, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(1, -1, -1, Blocks.CRYING_OBSIDIAN),
            new BlockEntry(0, -1, 1, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(0, -1, 0, Blocks.SCULK_CATALYST),
            new BlockEntry(0, -1, -1, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(-1,-1, 1, Blocks.CRYING_OBSIDIAN),
            new BlockEntry(-1,-1, 0, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(-1,-1, -1, Blocks.CRYING_OBSIDIAN),


            new BlockEntry(0, -2, 0, Blocks.DIAMOND_BLOCK),
            new BlockEntry(1, -2, 0, Blocks.EMERALD_BLOCK),
            new BlockEntry(0, -2, -1, Blocks.EMERALD_BLOCK),
            new BlockEntry(0, -2, 1, Blocks.EMERALD_BLOCK),
            new BlockEntry(-1,-2, 0, Blocks.EMERALD_BLOCK),
            new BlockEntry(1, -2, -1, Blocks.GOLD_BLOCK),
            new BlockEntry(-1,-2, -1, Blocks.GOLD_BLOCK),
            new BlockEntry(1 ,-2, 1, Blocks.GOLD_BLOCK),
            new BlockEntry(-1,-2, 1, Blocks.GOLD_BLOCK),

            new BlockEntry(2, -2, 2, Blocks.CRYING_OBSIDIAN),
            new BlockEntry(2, -2, 1, Blocks.SCULK),
            new BlockEntry(2, -2, 0, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(2, -2, -1, Blocks.SCULK),
            new BlockEntry(2, -2, -2, Blocks.CRYING_OBSIDIAN),
            new BlockEntry(1, -2, 2, Blocks.SCULK),
            new BlockEntry(1, -2, -2, Blocks.SCULK),
            new BlockEntry(0, -2, 2, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(0, -2, -2, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(-1,-2, 2, Blocks.SCULK),
            new BlockEntry(-1,-2, -2, Blocks.SCULK),
            new BlockEntry(-2,-2, 2, Blocks.CRYING_OBSIDIAN),
            new BlockEntry(-2,-2, 1, Blocks.SCULK),
            new BlockEntry(-2,-2, 0, Blocks.SMOOTH_QUARTZ_STAIRS),
            new BlockEntry(-2,-2, -1, Blocks.SCULK),
            new BlockEntry(-2,-2, -2, Blocks.CRYING_OBSIDIAN),


            new BlockEntry(2, -1, 2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(2, -1, -2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(-2,-1, 2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(-2,-1, -2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(2, 0, 2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(2, 0, -2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(-2,0, 2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(-2,0, -2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(2, 1, 2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(2, 1, -2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(-2,1, 2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(-2,1, -2, Blocks.DEEPSLATE_BRICK_WALL),
            new BlockEntry(2, 2, 2, ModBlocks.GLOW_STONE_EVAPORATOR.get()),
            new BlockEntry(2, 2, -2, ModBlocks.GLOW_STONE_EVAPORATOR.get()),
            new BlockEntry(-2,2, 2, ModBlocks.GLOW_STONE_EVAPORATOR.get()),
            new BlockEntry(-2,2, -2, ModBlocks.GLOW_STONE_EVAPORATOR.get()),
            new BlockEntry(2, 3, 2, Blocks.END_ROD),
            new BlockEntry(2, 3, -2, Blocks.END_ROD),
            new BlockEntry(-2,3, 2, Blocks.END_ROD),
            new BlockEntry(-2,3, -2, Blocks.END_ROD),

            new BlockEntry(0,3, 0, ModBlocks.CELESTIAL_CRYSTAL.get()),
            new BlockEntry(0,4, 0, ModBlocks.CELESTIAL_CRYSTAL.get()),
            new BlockEntry(0,5, 0, ModBlocks.CELESTIAL_CRYSTAL.get())
    );



    public Multiblock(String name, Block centerBlock, BlockEntry... blocks) {
        this.name = name;
        this.centerBlock = centerBlock;
        this.blocks = blocks;
    }

    public static boolean scanMultiblock(Multiblock multiblock, BlockPos altarPos, Level pLevel) {
        for (int i = 0; i < multiblock.blocks.length; i++) {
            BlockPos currentOffset = multiblock.blocks[i].getOffset();
            if(!multiblock.blocks[i].block.defaultBlockState().is(pLevel.getBlockState(altarPos.offset(currentOffset)).getBlock())) {
                return false;
            }
        }
        return true;
    }




    private static class BlockEntry {

        public final int x;
        public final int y;
        public final int z;
        public final Block block;

        //Position relative to Center Block
        public BlockEntry(int xOfst, int yOfst, int zOfst, Block block) {
            this.x = xOfst;
            this.y = yOfst;
            this.z = zOfst;
            this.block = block;
        }

        public BlockPos getOffset() {
            return new BlockPos(x,y,z);
        }
    }
}
