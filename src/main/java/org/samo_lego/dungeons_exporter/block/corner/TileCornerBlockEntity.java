package org.samo_lego.dungeons_exporter.block.corner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.dungeons_exporter.DungeonsExporter;
import org.samo_lego.dungeons_exporter.block.ConverterBlockEntites;
import org.samo_lego.dungeons_exporter.lovika.tiles.ITileListener;
import org.samo_lego.dungeons_exporter.lovika.tiles.Tile;

import java.util.Optional;

public class TileCornerBlockEntity extends BlockEntity implements BoundingBoxRenderable {
    @Nullable
    private BlockPos otherCorner;

    public TileCornerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ConverterBlockEntites.TILE_CORNER_BLOCK_ENTITY, worldPosition, blockState);
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        super.preRemoveSideEffects(pos, state);
        // Notify tile listener
        var tileListener = ((ITileListener) this.level).dungeons_exporter$getTileListener();
        tileListener.onCornerRemoved(this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        int[] coordinates = new int[0];
        if (this.otherCorner != null) {
            coordinates = new int[3];
            coordinates[0] = this.otherCorner.getX();
            coordinates[1] = this.otherCorner.getY();
            coordinates[2] = this.otherCorner.getZ();
        }

        output.putIntArray("OtherCorner", coordinates);
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.getIntArray("OtherCorner").ifPresent(coordinates -> {
            if (coordinates.length == 3) {
                this.otherCorner = new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
            } else {
                this.otherCorner = null;
            }
        });
    }

    public void setMatchingCorner(@Nullable BlockPos pos) {
        this.otherCorner = pos;
        this.setChanged();

        // Update the client
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public Optional<Tile> toDungeonTile() {
        if (this.otherCorner == null) {
            DungeonsExporter.LOGGER.warn("Cannot convert TileCornerBlockEntity to Tile without matching corner set.");
            return Optional.empty();
        }

        BlockPos min = new BlockPos(
                Math.min(this.getBlockPos().getX(), this.otherCorner.getX()),
                Math.min(this.getBlockPos().getY(), this.otherCorner.getY()),
                Math.min(this.getBlockPos().getZ(), this.otherCorner.getZ())
        );

        BlockPos max = new BlockPos(
                Math.max(this.getBlockPos().getX(), this.otherCorner.getX()),
                Math.max(this.getBlockPos().getY(), this.otherCorner.getY()),
                Math.max(this.getBlockPos().getZ(), this.otherCorner.getZ())
        );

        return Optional.of(new Tile(
                "",
                min,
                max.subtract(min).offset(1, 1, 1),
                "",
                "",
                "",
                "",
                null,
                null
        ));
    }

    public Optional<TileCornerBlockEntity> getMatchingCorner() {
        if (this.otherCorner == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.level.getBlockEntity(this.otherCorner) instanceof TileCornerBlockEntity be ? be : null);
    }

    @Override
    public Mode renderMode() {
        if (this.otherCorner == null || this.otherCorner.compareTo(this.getBlockPos()) >= 0) {
            return Mode.NONE;
        }
        return Mode.BOX;
    }

    @Override
    public RenderableBox getRenderableBox() {
        if (this.otherCorner == null) {
            //DungeonsExporter.LOGGER.error("Cannot get renderable box when other corner is not set.");
            return new RenderableBox(this.getBlockPos(), this.getBlockPos());
        }

        Vec3i size = this.getBlockPos().subtract(this.otherCorner);
        size = absolute(size).offset(1, 1, 1);

        var minimal = new BlockPos(
                Math.min(this.getBlockPos().getX(), this.otherCorner.getX()),
                Math.min(this.getBlockPos().getY(), this.otherCorner.getY()),
                Math.min(this.getBlockPos().getZ(), this.otherCorner.getZ())
        );
        var relative = minimal.subtract(this.getBlockPos());
        return new RenderableBox(relative, size);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);

        int[] coordinates = new int[0];
        if (this.otherCorner != null) {
            coordinates = new int[3];
            coordinates[0] = this.otherCorner.getX();
            coordinates[1] = this.otherCorner.getY();
            coordinates[2] = this.otherCorner.getZ();
        }

        tag.putIntArray("OtherCorner", coordinates);

        return tag;
    }

    private static Vec3i absolute(Vec3i pos) {
        return new Vec3i(
                Math.abs(pos.getX()),
                Math.abs(pos.getY()),
                Math.abs(pos.getZ())
        );
    }
}
