package org.samo_lego.dungeons_packer.lovika.resource_pack;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record TextureBytes(Map<Direction, Identifier> sideMappings, Map<Identifier, byte[]> bytes) { }
