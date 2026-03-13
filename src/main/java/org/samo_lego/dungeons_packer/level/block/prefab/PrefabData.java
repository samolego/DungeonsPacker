package org.samo_lego.dungeons_packer.level.block.prefab;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.samo_lego.dungeons_packer.DungeonsPacker;

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
     * Scale is multiplied by 100 to preserve 2 decimal places in 1 byte (0.00 to 2.55).
     * Coordinates are cast to short (0-65535 range).
     */
    public Optional<int[]> encodeToPixels(BlockPos worldPos) {
        int[] pixels = new int[9];

        // 1. Metadata & World Position (Pixel 0 & 1)
        int index = PrefabRegistry.PREFAB_NAME_TO_INDEX.getOrDefault(this.BP_Class, -1);
        if (index == -1) {
            return Optional.empty();
        }

        int wx = worldPos.getX();
        int wy = worldPos.getY();
        int wz = worldPos.getZ();

        // Pixel 0: [R:IdxH, G:IdxL] [B:WzH, A:WzL]
        pixels[0] = packUnrealRGBA((index >> 8), index, (wz >> 8), wz);
        // Pixel 1: [R:WxH, G:WxL] [B:WyH, A:WyL]
        pixels[1] = packUnrealRGBA((wx >> 8), wx, (wy >> 8), wy);

        // Relative Position (Pixel 2)
        // Range 0-100, stored as (val * 100) in 1 byte each
        int rx = (int) Math.round(this.relativePos.x * 100);
        int ry = (int) Math.round(this.relativePos.y * 100);
        int rz = (int) Math.round(this.relativePos.z * 100);
        pixels[2] = packUnrealRGBA(rx, ry, rz, 0);

        // 3. Scale (Pixels 3, 4, 5) - Full 32-bit Floats
        pixels[3] = packFloatToPixel((float)this.scale.x);
        pixels[4] = packFloatToPixel((float)this.scale.y);
        pixels[5] = packFloatToPixel((float)this.scale.z);

        // 4. Rotation (Pixels 6, 7, 8) - Full 32-bit Floats
        pixels[6] = packFloatToPixel((float)this.rotation.getX());
        pixels[7] = packFloatToPixel((float)this.rotation.getY());
        pixels[8] = packFloatToPixel((float)this.rotation.getZ());

        return Optional.of(pixels);
    }

    private int packFloatToPixel(float val) {
        int bits = Float.floatToRawIntBits(val);
        // Split 32 bits into R, G, B, A (8 bits each)
        return packUnrealRGBA(
                (bits >> 24) & 0xFF,  // R
                (bits >> 16) & 0xFF,  // G
                (bits >> 8) & 0xFF,  // B
                bits & 0xFF  // A
        );
    }

    private int packUnrealRGBA(int r, int g, int b, int a) {
        // Java Int format: 0xAARRGGBB
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

}
