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
