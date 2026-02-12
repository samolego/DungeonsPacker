package org.samo_lego.dungeons_packer.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;

public record TextureDataC2SPacket(int id, Identifier textureId, byte[] bytes) implements CustomPacketPayload {
    public static final Identifier SUMMON_LIGHTNING_PAYLOAD_ID = Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "texture_data");
    public static final CustomPacketPayload.Type<TextureDataC2SPacket> ID = new CustomPacketPayload.Type<>(SUMMON_LIGHTNING_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, TextureDataC2SPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            TextureDataC2SPacket::id,
            Identifier.STREAM_CODEC,
            TextureDataC2SPacket::textureId,
            ByteBufCodecs.BYTE_ARRAY,
            TextureDataC2SPacket::bytes,
            TextureDataC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
