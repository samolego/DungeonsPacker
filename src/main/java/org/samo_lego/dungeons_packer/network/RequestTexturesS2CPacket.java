package org.samo_lego.dungeons_packer.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;

public record RequestTexturesS2CPacket(long[] blockStates) implements CustomPacketPayload {
    public static final Identifier REQUEST_TEXTURES_PACKET_ID = Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID, "request_blockstates");
    public static final CustomPacketPayload.Type<RequestTexturesS2CPacket> ID = new CustomPacketPayload.Type<>(REQUEST_TEXTURES_PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestTexturesS2CPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG_ARRAY,
            RequestTexturesS2CPacket::blockStates,
            RequestTexturesS2CPacket::new
    );



    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;

    }
}
