package org.samo_lego.dungeons_packer.lovika.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class EnumLowerCaseSerializer<E extends Enum<E>>  implements JsonSerializer<E> {
    @Override
    public JsonElement serialize(E enumType, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(enumType.name().toLowerCase());
    }
}
