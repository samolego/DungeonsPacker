package org.samo_lego.dungeons_packer.lovika.resource_pack;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ResourceGenerator {
    public static final Map<BlockState, TextureEntry> HARDCODED = Map.of(
            // Todo: bed, hopper, cauldron, etc. (blocks with special models)
    );

    private final Block2IdGenerator blockGen;
    private final Map<BlockState, TextureEntry> cache;
    private final Set<BlockState> textureCache;

    public ResourceGenerator(Set<BlockState> textureCache) {
        this.blockGen = new Block2IdGenerator();
        this.cache = new Reference2ObjectOpenHashMap<>();
        this.textureCache = textureCache;
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


    public void fetchClientTextures(ServerPlayer player) {
        var missingStates = new ArrayList<>(this.cache.keySet().stream().filter(state -> !this.textureCache.contains(state)).toList());
        missingStates.addAll(HARDCODED.keySet().stream().filter(state -> !this.textureCache.contains(state)).toList());

        var ids = missingStates.stream().mapToLong(Block::getId).toArray();
        if (ids.length == 0) {
            return;
        }

        var packet = new RequestTexturesS2CPacket(ids);
        ServerPlayNetworking.send(player, packet);
    }

}
