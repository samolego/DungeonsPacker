package org.samo_lego.dungeons_packer.lovika.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BooleanStringSerializer implements JsonSerializer<Boolean> {
    @Override
    public JsonElement serialize(Boolean bool, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(Boolean.toString(bool));
    }
}
