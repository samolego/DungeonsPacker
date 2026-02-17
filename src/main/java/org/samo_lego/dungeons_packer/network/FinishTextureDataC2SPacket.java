package org.samo_lego.dungeons_packer.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;

public record FinishTextureDataC2SPacket() implements CustomPacketPayload {
    public static final Identifier TEXTURE_DATA_FINISH_PAYLOAD_ID = Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "finish_texture_data");
    public static final CustomPacketPayload.Type<FinishTextureDataC2SPacket> ID = new CustomPacketPayload.Type<>(TEXTURE_DATA_FINISH_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, FinishTextureDataC2SPacket> CODEC = StreamCodec.unit(new FinishTextureDataC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
