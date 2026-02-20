package org.samo_lego.dungeons_packer.lovika.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class CustomJsonSerializable implements JsonSerializer<ICustomJsonSerializable> {
    @Override
    public JsonElement serialize(ICustomJsonSerializable serializable, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(serializable.getSerializationObject());
    }
}
