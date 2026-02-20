package org.samo_lego.dungeons_packer.lovika.block_conversion;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.lovika.resource_pack.TextureEntry;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DungeonBlockIdProvider {

    private final BlockMap2 blockMap;
    private final Map<BlockState, TextureEntry> cache;

    public DungeonBlockIdProvider() {
        this.blockMap = new BlockMap2();
        this.cache = new Reference2ObjectOpenHashMap<>();
    }

    public short requestId(BlockState state, ServerPlayer player) {
        var cached = this.cache.get(state);
        if (cached != null) {
            return cached.getFullId();
        }

        short id = this.blockMap.toDungeonBlockId(state, player);
        if (id != -1) {
            var stringId = Block2IdGenerator.getResourceStringId(id >> 4);
            var entry = TextureEntry.fromFullId(id, stringId.orElse("unknown_mapping"));
            this.cache.put(state, entry);
            return entry.getFullId();
        }
        return id;
    }

    public Map<BlockState, TextureEntry> getUsedTextures() {
        return this.cache;
    }


    public int fetchClientTextures(ServerPlayer player, Set<BlockState> textureCache) {
        var missingStates = new ArrayList<>(this.cache.keySet().stream().filter(state -> !textureCache.contains(state)).toList());

        var ids = missingStates.stream().mapToLong(Block::getId).toArray();
        if (ids.length != 0) {
            var packet = new RequestTexturesS2CPacket(ids);
            ServerPlayNetworking.send(player, packet);
        }

        return ids.length;
    }
}
