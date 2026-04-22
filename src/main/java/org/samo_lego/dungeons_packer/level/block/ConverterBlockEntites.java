package org.samo_lego.dungeons_packer.level.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.corner.TileCornerBlockEntity;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabBlockEntity;

public class ConverterBlockEntites {
    public static final BlockEntityType<TileCornerBlockEntity> TILE_CORNER_BLOCK_ENTITY =
            register("tile_corner", TileCornerBlockEntity::new, ConverterBlocks.TILE_CORNER_BLOCK);

    public static final BlockEntityType<PrefabBlockEntity> PREFAB_BLOCK_ENTITY =
            register("prefab", PrefabBlockEntity::new, ConverterBlocks.PREFAB_BLOCK);

    public static final BlockEntityType<PrefabBlockEntity> MOBS_AREA_BLOCK_ENTITY =
            register("mobs_area", PrefabBlockEntity::new, ConverterBlocks.MOBS_AREA_BLOCK);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() { }
}
