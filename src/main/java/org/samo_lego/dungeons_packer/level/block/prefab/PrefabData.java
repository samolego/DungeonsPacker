package org.samo_lego.dungeons_packer.level.block.prefab;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record PrefabData(
    String BP_Class,
    Vec3 relativePos,
    Vec3 scale,
    Vec3i rotation
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, PrefabData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PrefabData::BP_Class,
            Vec3.STREAM_CODEC, PrefabData::relativePos,
            Vec3.STREAM_CODEC, PrefabData::scale,
            Vec3i.STREAM_CODEC, PrefabData::rotation,
            PrefabData::new
    );

    public static final Codec<PrefabData> CODEC = RecordCodecBuilder.create(
		i -> i.group(
				Codec.sizeLimitedString(64).fieldOf("BP_Class").forGetter(PrefabData::BP_Class),
                Vec3.CODEC.fieldOf("relativePos").forGetter(PrefabData::relativePos),
                Vec3.CODEC.fieldOf("scale").forGetter(PrefabData::scale),
                Vec3i.CODEC.fieldOf("rotation").forGetter(PrefabData::rotation)
			)
			.apply(i, PrefabData::new)
	);

    private static final PrefabData EMPTY = new PrefabData("", Vec3.ZERO, new Vec3(1, 1, 1), Vec3i.ZERO);


    public static PrefabData getDefault() {
        return EMPTY;
    }

    public PrefabData withRotation(Vec3i rotation) {
        return new PrefabData(this.BP_Class, this.relativePos, this.scale, rotation);
    }

    public PrefabData withRelativePos(Vec3 relativePos) {
        return new PrefabData(this.BP_Class, relativePos, this.scale, this.rotation);
    }

    public PrefabData withScale(Vec3 size) {
        return new PrefabData(this.BP_Class, this.relativePos, size, this.rotation);
    }

    public PrefabData withBPClass(String bpClass) {
        return new PrefabData(bpClass, this.relativePos, this.scale, this.rotation);
    }

    public void save(ValueOutput output) {
        var tag = output.child("PrefabData");
        tag.putString("BP_Class", this.BP_Class);
        var relativePosTag = tag.child("RelativePos");
        relativePosTag.putDouble("X", this.relativePos.x);
        relativePosTag.putDouble("Y", this.relativePos.y);
        relativePosTag.putDouble("Z", this.relativePos.z);
        var scaleTag = tag.child("Scale");
        scaleTag.putDouble("X", this.scale.x);
        scaleTag.putDouble("Y", this.scale.y);
        scaleTag.putDouble("Z", this.scale.z);
        tag.putIntArray("Rotation", new int[]{this.rotation.getX(), this.rotation.getY(), this.rotation.getZ()});
    }

    public static PrefabData load(ValueInput input) {
        return input.child("PrefabData").map(tag -> {
            var bpClass = tag.getString("BP_Class").orElse("");
            var relativePosTag = tag.child("RelativePos").orElseThrow();
            var relativePos = new Vec3(
                relativePosTag.getDoubleOr("X", 0.5),
                relativePosTag.getDoubleOr("Y", 0.0),
                relativePosTag.getDoubleOr("Z", 0.5)
            );
            var scaleTag = tag.child("Scale").orElseThrow();
            var scale = new Vec3(
                scaleTag.getDoubleOr("X", 1.0),
                scaleTag.getDoubleOr("Y", 1.0),
                scaleTag.getDoubleOr("Z", 1.0)
            );

            var rotationArr = tag.getIntArray("Rotation").orElse(new int[]{0, 0, 0});
            var rotation = new Vec3i(
                    rotationArr[0],
                    rotationArr[1],
                    rotationArr[2]
            );

            return new PrefabData(bpClass, relativePos, scale, rotation);
        }).orElseGet(PrefabData::getDefault);
    }

    /**
     * Encodes prefab data into 4 pixels (represented as ARGB integers).
     */
    public Optional<int[]> encodeToPixels(BlockPos offsetPos) {
        int[] pixels = new int[4];

        // Metadata & World Position (Pixels 0 & 1)
        int index = PrefabRegistry.PREFAB_NAME_TO_INDEX.getOrDefault(this.BP_Class, -1);
        if (index == -1) {
            return Optional.empty();
        }

        // Clamp positions to 16-bit range (0-65535)
        int wz = Math.max(0, Math.min(65535, offsetPos.getZ()));
        int wx = Math.max(0, Math.min(65535, offsetPos.getX()));
        int wy = Math.max(0, Math.min(65535, offsetPos.getY()));

        // Pixel 0: index, y (height)
        pixels[0] = packRGBA((index >> 8), index, (wy >> 8), wy);

        // Pixel 1: x, z
        pixels[1] = packRGBA((wx >> 8), wx, (wz >> 8), wz);

        // Rotations (Pixel 2) - Packed as 10-bit triplet
        pixels[2] = pack10BitTriplet(
                (this.rotation.getX() % 360 + 360) % 360,
                (this.rotation.getZ() % 360 + 360 ) % 360,
                (this.rotation.getY() % 360 + 360 ) % 360
        );

        // Scale (Pixel 3) - Multiplied by 100 and packed as 10-bit triplet
        pixels[3] = pack10BitTriplet(
                (int) Math.round(this.scale.x * 100),
                (int) Math.round(this.scale.z * 100),
                (int) Math.round(this.scale.y * 100)
        );

        return Optional.of(pixels);
    }

    /**
     * Packs three 10-bit numbers into one 32-bit ARGB integer.
     * Matches Python pack_10bit_triplet logic.
     */
    private int pack10BitTriplet(int v1, int v2, int v3) {
        // Ensure values are in 10-bit range (0-1023)
        v1 = Math.max(0, Math.min(1023, v1));
        v2 = Math.max(0, Math.min(1023, v2));
        v3 = Math.max(0, Math.min(1023, v3));

        // Byte 1 (R): Top 8 bits of V1
        int r = (v1 >> 2) & 0xFF;

        // Byte 2 (G): Bottom 2 bits of V1 | Top 6 bits of V2
        int g = ((v1 & 0x03) << 6) | ((v2 >> 4) & 0x3F);

        // Byte 3 (B): Bottom 4 bits of V2 | Top 4 bits of V3
        int b = ((v2 & 0x0F) << 4) | ((v3 >> 6) & 0x0F);

        // Byte 4 (A): Bottom 6 bits of V3 | 2 Unused bits
        int a = (v3 & 0x3F) << 2;

        return packRGBA(r, g, b, a);
    }

    /**
     * Packs R, G, B, A components into a single Java ARGB int (0xAARRGGBB).
     */
    private int packRGBA(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
