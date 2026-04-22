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

                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        // Extract name and index from e.g.
                        // "31","/Game/Decor/Prefabs/_Plants/AloeVera/BP_AloeVera.BP_AloeVera_C"
                        int index = Integer.parseInt(parts[0].trim().replace("\"", ""));
                        var stringPaths = parts[1].split("\\.");
                        // get last part without the _C" suffix
                        var BP_Classname = stringPaths[stringPaths.length - 1].replace("_C\"", "");
                        map.put(BP_Classname, index);
                    } catch (NumberFormatException e) {
                        DungeonsPacker.LOGGER.warn("Skipping invalid line in prefabs.csv (invalid index): {}", line);
                    }
                }
            }
        } catch (Exception e) {
            DungeonsPacker.LOGGER.error("Could not parse prefabs from prefabs.csv: {}", e.getLocalizedMessage());
        }

        PREFAB_NAME_TO_INDEX = Map.copyOf(map);
    }
}
