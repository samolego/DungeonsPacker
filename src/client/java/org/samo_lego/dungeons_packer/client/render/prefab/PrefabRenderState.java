package org.samo_lego.dungeons_packer.client.render.prefab;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;
import org.samo_lego.dungeons_packer.DungeonsPacker;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabRegistry;

import java.io.IOException;

public class PrefabRenderState extends BlockEntityRenderState {
    private ObjModel objModel;
    private PrefabData prefabData;
    private Identifier baseTexture;

    public ObjModel getBPObjModel() {
        return this.objModel;
    }

    public Identifier getBaseTexture() {
        return this.baseTexture;
    }

    public void setPrefabData(PrefabData prefabData) {
        if (this.prefabData != null && this.prefabData.BP_Class().equals(prefabData.BP_Class())) {
            // No change in prefab class, skip reloading model and texture
            this.prefabData = prefabData;
            return;
        }
        this.prefabData = prefabData;

        PrefabRegistry.getObjModel(this.prefabData.BP_Class()).ifPresent(is -> {
            try {
                this.objModel = new ObjModel(is);
                this.baseTexture = PrefabRegistry.getBaseTexture(this.prefabData.BP_Class());
            } catch (IOException e) {
                DungeonsPacker.LOGGER.error("Failed to load OBJ model for prefab class: {}", this.prefabData.BP_Class(), e);
            }
        });
    }

    public PrefabData getPrefabData() {
        return this.prefabData;
    }
}
