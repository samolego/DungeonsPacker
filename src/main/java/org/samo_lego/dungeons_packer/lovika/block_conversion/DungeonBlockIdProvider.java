package org.samo_lego.dungeons_packer.lovika.block_conversion;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureEntry;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DungeonBlockIdProvider {
    public static final Map<BlockState, TextureEntry> HARDCODED = new TreeMap<>(Comparator.comparing(Block::getId));

    private final Block2IdGenerator blockGen;
    private final Map<BlockState, TextureEntry> cache;

    public DungeonBlockIdProvider() {
        this.blockGen = new Block2IdGenerator();
        this.cache = new Reference2ObjectOpenHashMap<>();
    }

    public short requestId(BlockState state) {
        var hardcoded = HARDCODED.get(state);
        if (hardcoded != null) {
            return hardcoded.getFullId();
        }

        var cached = this.cache.get(state);
        if (cached != null) {
            return cached.getFullId();
        }

        // We'll need to request new ID
        if (this.blockGen.hasNext()) {
            var entry = this.blockGen.next();
            this.cache.put(state, entry);

            return entry.getFullId();
        }

        return -1;
    }

    public Map<BlockState, TextureEntry> getUsedTextures() {
        return this.cache;
    }


    public int fetchClientTextures(ServerPlayer player, Set<BlockState> textureCache) {
        var missingStates = new ArrayList<>(this.cache.keySet().stream().filter(state -> !textureCache.contains(state)).toList());
        missingStates.addAll(HARDCODED.keySet().stream().filter(state -> !textureCache.contains(state)).toList());

        var ids = missingStates.stream().mapToLong(Block::getId).toArray();
        if (ids.length != 0) {
            var packet = new RequestTexturesS2CPacket(ids);
            ServerPlayNetworking.send(player, packet);
        }

        return ids.length;
    }

    private static void register(BlockState state, TextureEntry entry) {
        HARDCODED.put(state, entry);
    }


    static {
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 1), new TextureEntry((short) 0x0008, (byte) 0b0000, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 2), new TextureEntry((short) 0x0008, (byte) 0b0001, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 3), new TextureEntry((short) 0x0008, (byte) 0b0010, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 4), new TextureEntry((short) 0x0008, (byte) 0b0011, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 5), new TextureEntry((short) 0x0008, (byte) 0b0100, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 6), new TextureEntry((short) 0x0008, (byte) 0b0101, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 7), new TextureEntry((short) 0x0008, (byte) 0b0110, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 8), new TextureEntry((short) 0x0008, (byte) 0b0111, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 9), new TextureEntry((short) 0x0008, (byte) 0b1000, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 10), new TextureEntry((short) 0x0008, (byte) 0b1001, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 11), new TextureEntry((short) 0x0008, (byte) 0b1010, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 12), new TextureEntry((short) 0x0008, (byte) 0b1011, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 13), new TextureEntry((short) 0x0008, (byte) 0b1100, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 14), new TextureEntry((short) 0x0008, (byte) 0b1101, "flowing_water"));
        register(Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 15), new TextureEntry((short) 0x0008, (byte) 0b1110, "water"));


        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 1), new TextureEntry((short) 0x000a, (byte) 0b0000, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 2), new TextureEntry((short) 0x000a, (byte) 0b0001, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 3), new TextureEntry((short) 0x000a, (byte) 0b0010, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 4), new TextureEntry((short) 0x000a, (byte) 0b0011, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 5), new TextureEntry((short) 0x000a, (byte) 0b0100, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 6), new TextureEntry((short) 0x000a, (byte) 0b0101, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 7), new TextureEntry((short) 0x000a, (byte) 0b0110, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 8), new TextureEntry((short) 0x000a, (byte) 0b0111, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 9), new TextureEntry((short) 0x000a, (byte) 0b1000, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 10), new TextureEntry((short) 0x000a, (byte) 0b1001, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 11), new TextureEntry((short) 0x000a, (byte) 0b1010, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 12), new TextureEntry((short) 0x000a, (byte) 0b1011, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 13), new TextureEntry((short) 0x000a, (byte) 0b1100, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 14), new TextureEntry((short) 0x000a, (byte) 0b1101, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 15), new TextureEntry((short) 0x000a, (byte) 0b1110, "flowing_lava"));
        register(Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 0), new TextureEntry((short) 0x000b, (byte) 0b0000, "lava"));
    }
}
