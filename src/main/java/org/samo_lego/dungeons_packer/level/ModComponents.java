package org.samo_lego.dungeons_packer.level;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;

public class ModComponents {
    public static final DataComponentType<PrefabData> PREFAB_DATA = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "prefab_data"),
            DataComponentType.<PrefabData>builder().persistent(PrefabData.CODEC).build()
    );

    public static void initialize() {}
}
