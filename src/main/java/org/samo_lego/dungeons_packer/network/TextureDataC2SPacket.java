package org.samo_lego.dungeons_packer.network;

import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


public record TextureDataC2SPacket(
    int stateId,
    Map<Direction, Identifier> sideMappings,
    Map<Identifier, byte[]> uniqueTextures
)  implements CustomPacketPayload {
    public static final Identifier TEXTURE_DATA_PAYLOAD_ID = Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "texture_data");
    public static final CustomPacketPayload.Type<TextureDataC2SPacket> ID = new CustomPacketPayload.Type<>(TEXTURE_DATA_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, TextureDataC2SPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            TextureDataC2SPacket::stateId,
            ByteBufCodecs.map((_) -> new EnumMap<>(Direction.class), Direction.STREAM_CODEC, Identifier.STREAM_CODEC),
            TextureDataC2SPacket::sideMappings,
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.BYTE_ARRAY),
            TextureDataC2SPacket::uniqueTextures,
            TextureDataC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
