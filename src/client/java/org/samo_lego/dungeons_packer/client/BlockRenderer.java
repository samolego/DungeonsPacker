package org.samo_lego.dungeons_packer.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.dungeons_packer.client.mixin.SpriteContentsAccessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BlockRenderer {
    private static final BlockRenderer instance = new BlockRenderer();

    public static BlockRenderer getInstance() {
        return instance;
    }

    public byte[] getRawTextureBytes(Identifier textureId, Minecraft client) {
        Identifier resourcePath = textureId.withPrefix("textures/").withSuffix(".png");

        return client.getResourceManager().getResource(resourcePath).map(resource -> {
            try (var stream = resource.open()) {
                return stream.readAllBytes();
            } catch (IOException e) {
                return new byte[0];
            }
        }).orElse(new byte[0]);
    }


    public byte[] captureSideRaw(BlockState state, Direction side, Minecraft client) {
        BlockStateModel blockModel = client.getBlockRenderer().getBlockModel(state);
        var random = RandomSource.create(42L);
        List<BlockModelPart> parts = blockModel.collectParts(random);

        // 1. Collect all layers for this side
        List<BakedQuad> layers = new ArrayList<>();
        for (BlockModelPart part : parts) {
            List<BakedQuad> quads = part.getQuads(side);
            if (quads.isEmpty()) quads = part.getQuads(null);
            layers.addAll(quads);
        }

        if (layers.isEmpty()) return new byte[0];

        // 2. Create a canvas
        int size = layers.getFirst().spriteInfo().sprite().contents().width();
        try (NativeImage canvas = new NativeImage(size, size, true)) {

            for (BakedQuad quad : layers) {
                var contents = quad.spriteInfo().sprite().contents();

                // 3. Extract pixels from the Atlas (Core logic)
                try (NativeImage spritePixels = extractSprite(contents)) {
                    int tint = -1;
                    if (quad.isTinted()) {
                        // Get the biome color at player's location
                        tint = client.getBlockColors().getColor(state, client.level, client.player.blockPosition(), quad.tintIndex());
                    }

                    // 4. Paint this layer onto our canvas
                    paintLayer(canvas, spritePixels, tint);
                }
            }
            return encodeToPng(canvas);
        } catch (Exception e) {
            return new byte[0];
        }
    }


    private static NativeImage extractSprite(SpriteContents contents) {
        NativeImage fullAtlasStrip = ((SpriteContentsAccessor) contents).getOriginalImage();

        NativeImage sprite = new NativeImage(contents.width(), contents.height(), false);

        // copyRect(target, sourceX, sourceY, targetX, targetY, width, height, flipX, flipY)
        // We take Frame 0 (top of the strip).
        // If the sprite is animated, the strip contains all frames vertically.
        fullAtlasStrip.copyRect(sprite, 0, 0, 0, 0, contents.width(), contents.height(), false, false);

        return sprite;
    }

    private static void paintLayer(NativeImage canvas, NativeImage layer, int tint) {
        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                int pixel = layer.getPixel(x, y); // ARGB
                int alpha = (pixel >> 24) & 0xFF;
                if (alpha == 0) continue;

                // Apply Tint if necessary
                if (tint != -1) {
                    pixel = multiply(pixel, tint);
                }

                // Blend on top of canvas
                if (alpha == 255) {
                    canvas.setPixel(x, y, pixel);
                } else {
                    int background = canvas.getPixel(x, y);
                    canvas.setPixel(x, y, blend(background, pixel));
                }
            }
        }
    }

    // Helper: Simple color multiplication for tints (Grass, Leaves)
    private static int multiply(int color, int tint) {
        int a = (color >> 24) & 0xFF;
        int r = (((color >> 16) & 0xFF) * ((tint >> 16) & 0xFF)) / 255;
        int g = (((color >> 8) & 0xFF) * ((tint >> 8) & 0xFF)) / 255;
        int b = ((color & 0xFF) * (tint & 0xFF)) / 255;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // Helper: Alpha blending
    private static int blend(int bg, int fg) {
        float a = ((fg >> 24) & 0xFF) / 255f;
        int r = (int) (((fg >> 16) & 0xFF) * a + ((bg >> 16) & 0xFF) * (1 - a));
        int g = (int) (((fg >> 8) & 0xFF) * a + ((bg >> 8) & 0xFF) * (1 - a));
        int b = (int) ((fg & 0xFF) * a + (bg & 0xFF) * (1 - a));
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    private static byte[] encodeToPng(NativeImage img) throws IOException {
        Path temp = Files.createTempFile("dp" + img.hashCode(), ".png");
        img.writeToFile(temp);
        byte[] bytes = Files.readAllBytes(temp);
        //Files.deleteIfExists(temp);
        return bytes;
    }

}
