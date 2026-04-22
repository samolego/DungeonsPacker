package org.samo_lego.dungeons_packer.level.item.mobs;

import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabRegistry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class DungeonMobRegistry {
    public static final Map<String, String> MOBS;

    static {
        var map = new TreeMap<String, String>();
        try (var stream = PrefabRegistry.class.getResourceAsStream("/assets/" + DungeonsPacker.MOD_ID_GENERATED + "/mobs.csv");
             var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        ) {
            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length >= 2) {
                    try {
                        var type = parts[0].trim();
                        var id = parts[1].trim().split(",")[0];
                        map.put(id, type);
                    } catch (NumberFormatException e) {
                        DungeonsPacker.LOGGER.warn("Skipping invalid line in prefabs.csv (invalid index): {}", line);
                    }
                }
            }
        } catch (Exception e) {
            DungeonsPacker.LOGGER.error("Could not parse prefabs from prefabs.csv: {}", e.getLocalizedMessage());
        }

        MOBS = Map.copyOf(map);
    }
}
