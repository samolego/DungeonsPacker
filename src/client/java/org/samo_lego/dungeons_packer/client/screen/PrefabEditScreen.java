package org.samo_lego.dungeons_packer.client.screen;


import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabBlockEntity;
import org.samo_lego.dungeons_packer.level.block.prefab.PrefabData;
import org.samo_lego.dungeons_packer.network.UpdatePrefabC2SPacket;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PrefabEditScreen extends Screen {
    private static final Component NAME_LABEL = Component.translatable("structure_block.structure_name");
    private static final Component POSITION_LABEL = Component.translatable("structure_block.position");
    private static final Component SIZE_LABEL = Component.translatable("structure_block.size");
    private static final Component ROTATION_LABEL = Component.translatable("prefab_block.rotation");
    private final PrefabBlockEntity prefabBE;
    private final PrefabData defaultData;
    private EditBox nameEdit;
    private EditBox posXEdit;
    private EditBox posYEdit;
    private EditBox posZEdit;
    private EditBox scaleXEdit;
    private EditBox scaleYEdit;
    private EditBox scaleZEdit;

    private EditBox rotX;
    private EditBox rotY;
    private EditBox rotZ;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0###", DecimalFormatSymbols.getInstance(Locale.ROOT));

    public PrefabEditScreen(final PrefabBlockEntity structure) {
        super(Component.translatable(Blocks.STRUCTURE_BLOCK.getDescriptionId()));
        this.prefabBE = structure;
        this.defaultData = this.prefabBE.getPrefabData();
    }

    private void onDone() {
        if (this.sendToServer()) {
            this.minecraft.setScreen(null);
        }
    }

    private void onCancel() {
        this.prefabBE.setPrefabData(this.defaultData);
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onDone()).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onCancel()).bounds(this.width / 2 + 4, 210, 150, 20).build());

        int offsetX = 202;
        int width = 40;
        this.nameEdit = new EditBox(this.font, this.width / 2 - offsetX, 40, 3 * width, 20, Component.translatable("structure_block.structure_name"));

        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(this.prefabBE.getBPClassName());
        // Update blockEntity
        this.nameEdit.setResponder(this.prefabBE::setBPClassName);
        this.addWidget(this.nameEdit);
        var minPos = this.prefabBE.getRelativePos();
        this.posXEdit = new EditBox(this.font, this.width / 2 - offsetX, 80, width, 20, Component.translatable("structure_block.position.x"));
        this.posXEdit.setMaxLength(15);
        this.posXEdit.setValue(Double.toString(minPos.x()));
        this.posXEdit.setResponder(value -> {
            // Parse number, then update blockEntity
            var newX = this.parseDouble(value);
            this.prefabBE.setRelativePos(new Vec3(newX, this.prefabBE.getRelativePos().y(), this.prefabBE.getRelativePos().z()));
        });
        this.addWidget(this.posXEdit);
        this.posYEdit = new EditBox(this.font, this.width / 2 - offsetX + width, 80, width, 20, Component.translatable("structure_block.position.y"));
        this.posYEdit.setMaxLength(15);
        this.posYEdit.setValue(Double.toString(minPos.y()));
        this.posYEdit.setResponder(value -> {
            // Parse number, then update blockEntity
            var newY = this.parseDouble(value);
            this.prefabBE.setRelativePos(new Vec3(this.prefabBE.getRelativePos().x(), newY, this.prefabBE.getRelativePos().z()));
        });
        this.addWidget(this.posYEdit);
        this.posZEdit = new EditBox(this.font, this.width / 2 - offsetX + 2 * width, 80, width, 20, Component.translatable("structure_block.position.z"));
        this.posZEdit.setMaxLength(15);
        this.posZEdit.setValue(Double.toString(minPos.z()));
        this.posZEdit.setResponder(value -> {
            // Parse number, then update blockEntity
            var newZ = this.parseDouble(value);
            this.prefabBE.setRelativePos(new Vec3(this.prefabBE.getRelativePos().x(), this.prefabBE.getRelativePos().y(), newZ));
        });
        this.addWidget(this.posZEdit);
        var maxPos = this.prefabBE.getStructureScale();
        this.scaleXEdit = new EditBox(this.font, this.width / 2 - offsetX, 120, width, 20, Component.translatable("structure_block.size.x"));
        this.scaleXEdit.setMaxLength(15);
        this.scaleXEdit.setValue(Double.toString(maxPos.x()));
        this.scaleXEdit.setResponder(value -> {
            // Parse number, then update blockEntity
            var newX = this.parseDouble(value);
            this.prefabBE.setStructureScale(new Vec3(newX, this.prefabBE.getStructureScale().y(), this.prefabBE.getStructureScale().z()));
        });
        this.addWidget(this.scaleXEdit);
        this.scaleYEdit = new EditBox(this.font, this.width / 2 - offsetX + width, 120, width, 20, Component.translatable("structure_block.size.y"));
        this.scaleYEdit.setMaxLength(15);
        this.scaleYEdit.setValue(Double.toString(maxPos.y()));
        this.scaleYEdit.setResponder(value -> {
            // Parse number, then update blockEntity
            var newY = this.parseDouble(value);
            this.prefabBE.setStructureScale(new Vec3(this.prefabBE.getStructureScale().x(), newY, this.prefabBE.getStructureScale().z()));
        });
        this.addWidget(this.scaleYEdit);
        this.scaleZEdit = new EditBox(this.font, this.width / 2 - offsetX + 2 * width, 120, width, 20, Component.translatable("structure_block.size.z"));
        this.scaleZEdit.setMaxLength(15);
        this.scaleZEdit.setValue(Double.toString(maxPos.z()));
        this.scaleZEdit.setResponder(value -> {
            // Parse number, then update blockEntity
            var newZ = this.parseDouble(value);
            this.prefabBE.setStructureScale(new Vec3(this.prefabBE.getStructureScale().x(), this.prefabBE.getStructureScale().y(), newZ));
        });
        this.addWidget(this.scaleZEdit);


        this.rotX = new EditBox(this.font, this.width / 2 - offsetX, 160, width, 20, Component.translatable("prefab_block.rotation.x"));
        this.rotX.setMaxLength(15);
        this.rotX.setValue(Double.toString(this.prefabBE.getRotation().x()));
        this.rotX.setResponder(value -> {
            var newX = this.parseDouble(value);
            this.prefabBE.setRotation(new Vec3(newX, this.prefabBE.getRotation().y(), this.prefabBE.getRotation().z()));
        });
        this.addWidget(this.rotX);
        this.rotY = new EditBox(this.font, this.width / 2 - offsetX + width, 160, width, 20, Component.translatable("prefab_block.rotation.y"));
        this.rotY.setMaxLength(15);
        this.rotY.setValue(Double.toString(this.prefabBE.getRotation().y()));
        this.rotY.setResponder(value -> {
            var newY = this.parseDouble(value);
            this.prefabBE.setRotation(new Vec3(this.prefabBE.getRotation().x(), newY, this.prefabBE.getRotation().z()));
        });
        this.addWidget(this.rotY);
        this.rotZ = new EditBox(this.font, this.width / 2 - offsetX + 2 * width, 160, width, 20, Component.translatable("prefab_block.rotation.z"));
        this.rotZ.setMaxLength(15);
        this.rotZ.setValue(Double.toString(this.prefabBE.getRotation().z()));
        this.rotZ.setResponder(value -> {
            var newZ = this.parseDouble(value);
            this.prefabBE.setRotation(new Vec3(this.prefabBE.getRotation().x(), this.prefabBE.getRotation().y(), newZ));
        });
        this.addWidget(this.rotZ);
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameEdit);
    }

    @Override
    public void resize(final int width, final int height) {
        String oldNameEdit = this.nameEdit.getValue();
        String oldPosXEdit = this.posXEdit.getValue();
        String oldPosYEdit = this.posYEdit.getValue();
        String oldPosZEdit = this.posZEdit.getValue();
        String oldSizeXEdit = this.scaleXEdit.getValue();
        String oldSizeYEdit = this.scaleYEdit.getValue();
        String oldSizeZEdit = this.scaleZEdit.getValue();
        String oldRotXEdit = this.rotX.getValue();
        String oldRotYEdit = this.rotY.getValue();
        String oldRotZEdit = this.rotZ.getValue();
        this.init(width, height);
        this.nameEdit.setValue(oldNameEdit);
        this.posXEdit.setValue(oldPosXEdit);
        this.posYEdit.setValue(oldPosYEdit);
        this.posZEdit.setValue(oldPosZEdit);
        this.scaleXEdit.setValue(oldSizeXEdit);
        this.scaleYEdit.setValue(oldSizeYEdit);
        this.scaleZEdit.setValue(oldSizeZEdit);
        this.rotX.setValue(oldRotXEdit);
        this.rotY.setValue(oldRotYEdit);
        this.rotZ.setValue(oldRotZEdit);
    }

    private boolean sendToServer() {
        ClientPlayNetworking.send(new UpdatePrefabC2SPacket(this.prefabBE.getBlockPos(), this.prefabBE.getPrefabData()));
        return true;
    }

    private double parseDouble(final String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException var3) {
            return 0;
        }
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        } else if (event.isConfirmation()) {
            this.onDone();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
        super.render(graphics, mouseX, mouseY, a);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, -1);

        int offsetX = 200;
        graphics.drawString(this.font, NAME_LABEL, this.width / 2 - offsetX, 30, -6250336);
        this.nameEdit.render(graphics, mouseX, mouseY, a);

        graphics.drawString(this.font, POSITION_LABEL, this.width / 2 - offsetX, 70, -6250336);
        this.posXEdit.render(graphics, mouseX, mouseY, a);
        this.posYEdit.render(graphics, mouseX, mouseY, a);
        this.posZEdit.render(graphics, mouseX, mouseY, a);

        graphics.drawString(this.font, SIZE_LABEL, this.width / 2 - offsetX, 110, -6250336);
        this.scaleXEdit.render(graphics, mouseX, mouseY, a);
        this.scaleYEdit.render(graphics, mouseX, mouseY, a);
        this.scaleZEdit.render(graphics, mouseX, mouseY, a);

        graphics.drawString(this.font, ROTATION_LABEL, this.width / 2 - offsetX, 150, -6250336);
        this.rotX.render(graphics, mouseX, mouseY, a);
        this.rotY.render(graphics, mouseX, mouseY, a);
        this.rotZ.render(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) { }
}

