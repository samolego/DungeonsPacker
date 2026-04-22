# Dungeons Packer

Minecraft Fabric mod to `pak` your Minecraft Java world to Minecraft Dungeons!
Huge thanks to Dokucraft team, especially their [Level Docs](https://github.com/Dokucraft/Dungeons-Level-Format) and [Mod Kit](https://github.com/Dokucraft/Dungeons-Mod-Kit).


## Converting worlds

**First make sure to prepare your Minecraft Dungeons for [modding](https://stash.dokucraft.co.uk/?help=modding-dungeons).**

Build a world you want to import to dungeons. The world should be made of *tiles* (areas that mcd stitches together). Once you're happy with a tile, mark it with **Tile Corner** block in the 2 opposite corners, like so:

<img width="854" height="480" alt="2026-04-22_10 43 35" src="https://github.com/user-attachments/assets/710a3dac-5853-43cb-89d0-d69ff2f06b09" />
<img width="854" height="480" alt="2026-04-22_10 43 39" src="https://github.com/user-attachments/assets/0f372481-71d2-4835-b90d-43cd3dc3c65b" />

(Your tiles will probably be bigger :P)

### Connecting tiles

Once you're happy with your tiles, you must connect them using *doors*. There's a **Tile Door** block you can use for that. Make sure that:
1. Tile doors are on the edge of the tile
2. Door shape from tile A should be identical to the one in tile B <!--(todo: test)-->
<img width="854" height="480" alt="2026-04-22_10 44 15" src="https://github.com/user-attachments/assets/984bba94-860a-4936-9580-02c9003623c6" />

### Mission ending

For the player to successfully complete a level, they must get to the end of the mission. You can mark the ending with *End Mission* block.
<img width="854" height="480" alt="2026-04-22_11 13 48" src="https://github.com/user-attachments/assets/4a14eca7-cca8-4320-83af-1142ffdd4124" />


### Chests

This mods supports placing chests too. You can search for them in creative menu. Note: not all chests were tested, but wooden, gold and obsidian ("deluxe") chests work.


### Exporting / dumping the world

Once you're happy with the setup, use the following command to export your world to dungeons:
```
/dungeons export
```
Mod will convert your tiles to dungeons, add a resource pack for the blocks and create a `pak` file for it. You must put the pak file in the `~mods` folder in dungeons.

If you're curios, you can also use `/dungeons dump` to dump the content on the disk. It will create a `Dungeons/` dir in your world folder. You can then use `/pak pack` to create a pak file back.


## Other info

The level replaces `archhaven` mission. There's ways to replace others too, but you'll have to dig through source code of the mod and cook the assets with Unreal Engine 4 for that.


<!--
## Resource pack test
* working:
  * existing resourcepack
  * you can also assign custom side textures on blocks
  * https://minecraft.fandom.com/wiki/Minecraft_Dungeons:Mission_Select#Unused
  * we can leave in all the blocks.json and nothing crashes -> we can have it as a template
  * we can use flowers for full sized blocks too!
* crashing:
  * custom resource pack name except "test"
* any block not present in the blocks.json of resourcepack gets the unknown ("Update") texture assigned
* geysermc mappings: https://raw.githubusercontent.com/GeyserMC/mappings/522967d6ee76972994ad05a992dc9d7bb4e889ba/blocks.json

## Crashes:
* < 2 tiles
* not all the blocks in blocks.json are present in the resource pack - NOT SURE
* custom resource pack name (except "test")
-->
