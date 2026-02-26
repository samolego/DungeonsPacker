package org.samo_lego.dungeons_packer.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.samo_lego.dungeons_packer.DungeonsPacker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .registerTypeAdapter(Block.class, new BlockSetAdapter())
            .setStrictness(Strictness.LENIENT)
            .create();
    private static final ModConfig INSTANCE;

    @JsonAdapter(BlockSetAdapter.class)
    public Set<Block> whitelisted_blocks = new TreeSet<>(Comparator.comparing(b -> BuiltInRegistries.BLOCK.wrapAsHolder(b).getRegisteredName()));

    // Full blocks that will actually be converted to
    // the intended variant
    // This is done since some blocks cause trouble
    // and get converted into more blocks than they should be
    @JsonAdapter(BlockSetAdapter.class)
    public Set<Block> forced_blocks = new TreeSet<>(Comparator.comparing(b -> BuiltInRegistries.BLOCK.wrapAsHolder(b).getRegisteredName()));

    public static ModConfig getInstance() {
        return ModConfig.INSTANCE;
    }


    public void writeToFile(File file) {
        try (var writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            DungeonsPacker.LOGGER.error("Problem occurred when saving config: {}", e.getMessage());
        }
    }

    static {
        INSTANCE = reload();
    }

    public static ModConfig reload() {
        ModConfig instance = null;
        var file = FabricLoader.getInstance().getConfigDir().resolve("dungeons_packer.json").toFile();

        if (file.exists()) {
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                instance = GSON.fromJson(fileReader, ModConfig.class);
            } catch (JsonIOException | JsonSyntaxException | IOException e) {
                DungeonsPacker.LOGGER.error("Problem occurred when trying to load config: {}", e.getMessage());
            }
        }

        if (instance == null) {
            instance = new ModConfig();
            instance.forced_blocks.addAll(Set.of(
                    Blocks.FURNACE,
                    Blocks.DROPPER,
                    Blocks.END_PORTAL_FRAME,
                    Blocks.ENCHANTING_TABLE,
                    Blocks.OAK_WALL_SIGN,
                    Blocks.OAK_SIGN,
                    Blocks.STONE_PRESSURE_PLATE,
                    Blocks.SKELETON_SKULL,
                    Blocks.SKELETON_WALL_SKULL,
                    Blocks.DISPENSER
            ));
        }
        instance.writeToFile(file);

        return instance;
    }

    /**
     * Adapts {@link Block} between it and the identifier.
     *
     * @author Ampflower
     */
    private static final class BlockSetAdapter extends TypeAdapter<Set<Block>> {

        @Override
        public void write(JsonWriter out, Set<Block> value) throws IOException {
            out.beginArray();
            var reg = BuiltInRegistries.BLOCK;
            for (var block : value) {
                out.value(reg.getKey(block).toString());
            }
            out.endArray();
        }

        @Override
        public Set<Block> read(JsonReader in) throws IOException {
            in.beginArray();
            var reg = BuiltInRegistries.BLOCK;
            var set = new TreeSet<Block>(Comparator.comparing(b -> BuiltInRegistries.BLOCK.wrapAsHolder(b).getRegisteredName()));
            while (in.hasNext()) {
                var id = Identifier.tryParse(in.nextString());
                if (id == null) {
                    DungeonsPacker.LOGGER.warn("Invalid block identifier in config: {}", id);
                    continue;
                }
                reg.get(id).map(Reference::value).ifPresent(set::add);
            }
            in.endArray();
            return set;
        }
    }
}
