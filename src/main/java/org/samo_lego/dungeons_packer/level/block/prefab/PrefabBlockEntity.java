package org.samo_lego.dungeons_packer.level.block.prefab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_packer.level.block.ConverterBlockEntites;

public class PrefabBlockEntity extends BlockEntity implements Nameable {
    private PrefabData prefabData;

    public PrefabBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ConverterBlockEntites.PREFAB_BLOCK_ENTITY, worldPosition, blockState);
        this.prefabData = PrefabData.getDefault();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        this.prefabData.save(output);
        super.saveAdditional(output);
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.prefabData = PrefabData.load(input);
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    public String getBPClassName() {
        return this.prefabData.BP_Class();
    }

    public void setRotation(Vec3i rotation) {
        this.setPrefabData(this.prefabData.withRotation(rotation));
    }

    public Vec3i getRotation() {
        return this.prefabData.rotation();
    }

    public Vec3 getStructureScale() {
        return this.prefabData.scale();
    }

    public Vec3 getRelativePos() {
        return this.prefabData.relativePos();
    }

    @Override
    public Component getName() {
        return this.getDisplayName();
    }

    @Override
    public Component getDisplayName() {
        if (this.prefabData.BP_Class().isEmpty()) {
            return Component.translatable("translate.block.prefab");
        } else {
            return Component.literal(this.prefabData.BP_Class());
        }
    }

    public PrefabData getPrefabData() {
        return this.prefabData;
    }

    public void setRelativePos(Vec3 vec3) {
        this.setPrefabData(this.prefabData.withRelativePos(vec3));
    }

    public void setStructureScale(Vec3 vec3) {
        this.setPrefabData( this.prefabData.withScale(vec3));
    }

    public void setBPClassName(String value) {
        this.setPrefabData(this.prefabData.withBPClass(value));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setPrefabData(PrefabData prefabData) {
        this.prefabData = prefabData;
        this.setChanged();
    }
}
