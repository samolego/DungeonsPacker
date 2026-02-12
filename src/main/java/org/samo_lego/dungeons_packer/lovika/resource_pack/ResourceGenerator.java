package org.samo_lego.dungeons_packer.lovika.resource_pack;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.network.RequestTexturesS2CPacket;

import java.util.Map;

public class ResourceGenerator {
    public static final Map<BlockState, TextureEntry> HARDCODED = Map.of(
            Blocks.AIR.defaultBlockState(), new TextureEntry((short) 0, (byte) 0,"air"),
            Blocks.CAVE_AIR.defaultBlockState(), new TextureEntry((short) 0, (byte) 0,"air"),
            Blocks.VOID_AIR.defaultBlockState(), new TextureEntry((short) 0, (byte) 0,"air")
    );

    private final Block2IdGenerator blockGen;
    private final Map<BlockState, TextureEntry> cache;

    public ResourceGenerator() {
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


    public void fetchClientTextures(ServerPlayer player) {
        var packet = new RequestTexturesS2CPacket(this.cache.keySet().stream().mapToLong(Block::getId).toArray());
        ServerPlayNetworking.send(player, packet);
    }

}
