package org.samo_lego.dungeons_packer.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;

public record UpdatePrefabC2SPacket(BlockPos pos, PrefabData prefabData) implements CustomPacketPayload {
    public static final Identifier UPDATE_PREFAB_PAYLOAD_ID = Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "update_prefab");
    public static final CustomPacketPayload.Type<UpdatePrefabC2SPacket> ID = new CustomPacketPayload.Type<>(UPDATE_PREFAB_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePrefabC2SPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            UpdatePrefabC2SPacket::pos,
            PrefabData.STREAM_CODEC,
            UpdatePrefabC2SPacket::prefabData,
            UpdatePrefabC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
