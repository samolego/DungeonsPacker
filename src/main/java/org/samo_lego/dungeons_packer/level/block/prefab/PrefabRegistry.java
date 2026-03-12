package org.samo_lego.dungeons_packer.level.block.prefab;

import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PrefabRegistry {
    public static final Map<String, Integer> PREFAB_NAME_TO_INDEX;

    public static Optional<InputStream> getObjModel(String name) {
        return Optional.ofNullable(PrefabRegistry.class.getResourceAsStream("/assets/" + DungeonsPacker.MOD_ID_GENERATED + "/obj/" + name +  ".obj"));
    }

    public static Identifier getBaseTexture(String name) {
        try {
            return Identifier.fromNamespaceAndPath(DungeonsPacker.MOD_ID_GENERATED, "textures/model/" + name.toLowerCase() + ".png");
        } catch (IdentifierException _) {
        }
        return Identifier.parse("minecraft:missingno");
    }

    static {
        var map = new HashMap<String, Integer>();
        try (var stream = PrefabRegistry.class.getResourceAsStream("/assets/" + DungeonsPacker.MOD_ID_GENERATED + "/prefabs.csv");
             var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        ) {
            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                // Split by comma, limit to 2 parts in case the path contains a comma (unlikely but safe)
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        int index = Integer.parseInt(parts[0].trim());
                        var stringPaths = parts[1].split("/");
                        var BP_Classname = stringPaths[stringPaths.length - 1].trim();
                        map.put(BP_Classname, index);
                    } catch (NumberFormatException e) {
                        DungeonsPacker.LOGGER.warn("Skipping invalid line in prefabs.csv (invalid index): " + line);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prefabs from CSV", e);
        }

        PREFAB_NAME_TO_INDEX = Map.copyOf(map);
    }
}
