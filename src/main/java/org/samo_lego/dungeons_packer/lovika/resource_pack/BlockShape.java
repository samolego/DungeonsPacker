package org.samo_lego.dungeons_packer.lovika.resource_pack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.lovika.Utils;
import org.samo_lego.dungeons_packer.mixin.block.SlabBlockAccessor;


public enum BlockShape {
    INVISIBLE,
    CROSS_TEXTURE,
    WATER,
    TREE,
    LEAVES,
    BED,
    RAIL,
    PISTON,
    BLOCK_HALF,
    TORCH,
    STAIRS,
    CHEST,
    RED_DUST,
    ROWS,  // crops
    DOOR,
    LADDER,
    LEVER,
    CACTUS,
    FENCE,
    REPEATER,
    IRON_FENCE,  // Iron bars
    STEM,
    VINE,
    FENCE_GATE,
    LILYPAD,
    BREWING_STAND,
    CAULDRON,
    PORTAL_FRAME,
    COCOA,
    TRIPWIRE_HOOK,
    TRIPWIRE,
    WALL,
    FLOWER_POT,
    ANVIL,
    COMPARATOR,
    HOPPER,
    SLIME_BLOCK,
    DOUBLE_PLANT_POLY,
    FIRE;

    /**
     * Mostly inspired by <a href="https://github.com/TheEpicBlock/PolyMc/blob/a3eaae6a56522a830b6e9a244e2bade0431a8c59/src/main/java/io/github/theepicblock/polymc/impl/generator/BlockPolyGenerator.java#L64">PolyMc</a>
     * @param state The blockstate to get the shape of.
     * @param level The level where the blockstate is located.
     * @return The block shape of the blockstate or null if it's a normal block.
     */
    @Nullable
    public static BlockShape fromBlockState(BlockState state, ServerLevel level) {
        var block = state.getBlock();

        //=== FLUIDS ===
        if (!state.getFluidState().isEmpty()) {
            return BlockShape.WATER;
        }

        switch (block) {
            //=== FENCE GATES ===
            case FenceGateBlock _ -> {
                return BlockShape.FENCE_GATE;
            }
            case CactusBlock _ -> {
                return BlockShape.CACTUS;
            }
            case FireBlock _ -> {
                return BlockShape.FIRE;
            }
            case AnvilBlock _ -> {
                return BlockShape.ANVIL;
            }
            case WallBlock _ -> {
                return BlockShape.WALL;
            }
            case BedBlock _ -> {
                return BlockShape.BED;
            }
            case BaseRailBlock _ -> {
                return BlockShape.RAIL;
            }
            case PistonBaseBlock _ -> {
                return BlockShape.PISTON;
            }
            case ChestBlock _ -> {
                return BlockShape.CHEST;
            }
            case CropBlock _ -> {
                return BlockShape.ROWS;
            }
            case BaseTorchBlock _ -> {
                return BlockShape.TORCH;
            }
            case LeverBlock _ -> {
                return BlockShape.LEVER;
            }
            case RedStoneWireBlock _ -> {
                return BlockShape.RED_DUST;
            }
            case LilyPadBlock _ -> {
                return BlockShape.LILYPAD;
            }
            case RepeaterBlock _ -> {
                return BlockShape.REPEATER;
            }
            case ComparatorBlock _ -> {
                return BlockShape.COMPARATOR;
            }
            case BrewingStandBlock _ -> {
                return BlockShape.BREWING_STAND;
            }
            case AbstractCauldronBlock _ -> {
                return BlockShape.CAULDRON;
            }
            case NetherPortalBlock _ -> {
                return BlockShape.PORTAL_FRAME;
            }
            case HopperBlock _ -> {
                return BlockShape.HOPPER;
            }
            case CocoaBlock _ -> {
                return BlockShape.COCOA;
            }
            case FlowerPotBlock _ -> {
                return BlockShape.FLOWER_POT;
            }
            case DoublePlantBlock _ -> {
                return BlockShape.DOUBLE_PLANT_POLY;
            }
            case WebBlock _, VegetationBlock _, SugarCaneBlock _ -> {
                return BlockShape.CROSS_TEXTURE;
            }
            case TripWireBlock _ -> {
                return BlockShape.TRIPWIRE;
            }
            case TripWireHookBlock _ -> {
                return BlockShape.TRIPWIRE_HOOK;
            }
            case RotatedPillarBlock _ -> {
                return BlockShape.TREE;
            }
            //=== (TRAP)DOORS ===
            case DoorBlock _ -> {
                return BlockShape.DOOR;
            }
            case TrapDoorBlock _ -> {
                return BlockShape.BLOCK_HALF;
            }
            //=== STAIRS ===
            case StairBlock _ -> {
                return BlockShape.STAIRS;
            }
            case IronBarsBlock _ -> {
                return BlockShape.IRON_FENCE;
            }
            case FenceBlock _ -> {
                return BlockShape.FENCE;
            }
            case HalfTransparentBlock _ -> {
                return BlockShape.SLIME_BLOCK;
            }
            default -> {
            }
        }


        //=== LEAVES ===
        if (block instanceof LeavesBlock || state.is(BlockTags.LEAVES)) {
            return BlockShape.LEAVES;
        }


        var saved = level.getBlockState(BlockPos.ZERO);
        level.setBlock(BlockPos.ZERO, state, 3, 0);

        // Get the state's collision shape.
        VoxelShape collisionShape;
        try {
            collisionShape = state.getCollisionShape(level, BlockPos.ZERO);
        } catch (Exception e) {
            DungeonsPacker.LOGGER.warn("Failed to get collision shape for {}: {}", state, e);
            collisionShape = Shapes.INFINITY;
        }
        level.setBlock(BlockPos.ZERO, saved, 3, 0);


        //=== INVISIBLE BLOCKS ===
        if (state.getRenderShape() == RenderShape.INVISIBLE) {
            if (Block.isShapeFullBlock(collisionShape)) {
                // Invisible bedrock maybe - is this it?
            }
            return BlockShape.INVISIBLE;
        }

        //=== SLABS ===
        boolean slabLike = Utils.areEqual(collisionShape, SlabBlockAccessor.SHAPE_BOTTOM());
        if (block instanceof SlabBlock || slabLike) {
            return BlockShape.BLOCK_HALF;
        }

        if (state.isAir()) return INVISIBLE;

        //=== FULL BLOCKS ===
        // Blocks that have a full top face and at least something on the bottom are considered full blocks. This works better for some blocks
        if (Block.isFaceFull(collisionShape, Direction.UP) && collisionShape.min(Direction.Axis.Y) <= 0) {
            return null;
        }

        //=== NO COLLISION BLOCKS ===
        if (collisionShape.isEmpty()) {
            if (state.is(BlockTags.CLIMBABLE)) {
                if (block instanceof VineBlock) {
                    return BlockShape.VINE;
                }
                return BlockShape.LADDER;
            }
            return null;
        }



        //=== DEFAULT ===
        return null;
    }
}
