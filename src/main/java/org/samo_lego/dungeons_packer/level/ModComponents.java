package org.samo_lego.dungeons_packer.level;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;

public class ModComponents {
    public static final DataComponentType<PrefabData> PREFAB_DATA = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "prefab_data"),
            DataComponentType.<PrefabData>builder().persistent(PrefabData.CODEC).build()
    );

    public static final DataComponentType<String> MONSTER_DATA = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "monster_data"),
            DataComponentType.<String>builder().persistent(Codec.sizeLimitedString(32)).build()
    );


    public static void initialize() {}
}
