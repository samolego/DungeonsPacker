package org.samo_lego.dungeons_packer.lovika.serialization;

import com.google.gson.*;
import net.minecraft.core.Vec3i;
import java.lang.reflect.Type;

public class Vec3iSerializer implements JsonSerializer<Vec3i> {
    @Override
    public JsonElement serialize(Vec3i src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(src.getX());
        array.add(src.getY());
        array.add(src.getZ());
        return array;
    }
}