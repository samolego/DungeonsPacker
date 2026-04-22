package org.samo_lego.dungeons_packer.lovika.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Type;

public class BlockSerializer implements JsonSerializer<Block>, JsonDeserializer<Block> {
    @Override
    public Block deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        // Find the block by its registry name
        var blockId = jsonElement.getAsString();
        return BuiltInRegistries.BLOCK.get(Identifier.tryParse(blockId))
                .map(Reference::value)
                .orElseThrow(() -> new IllegalArgumentException("Unknown block ID: " + blockId));
    }

    @Override
    public JsonElement serialize(Block block, Type type, JsonSerializationContext jsonSerializationContext) {
        // Serialize the block as its registry name
        var blockId = BuiltInRegistries.BLOCK.getKey(block);
        return jsonSerializationContext.serialize(blockId.toString());
    }
}
