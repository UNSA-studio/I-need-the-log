package com.unsa.ineedthelog.gui;

import com.unsa.ineedthelog.util.LogExporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FileSaveScreen extends Screen {
    private final Screen parent;
    private EditBox pathField;

    public FileSaveScreen(Screen parent) {
        super(Component.literal("Save Log File"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.pathField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20,
                Component.literal("File Path"));
        this.pathField.setMaxLength(256);
        this.pathField.setValue("logs/ERROR.txt");
        this.addRenderableWidget(this.pathField);

        Button saveButton = Button.builder(
                Component.literal("Save"),
                button -> {
                    String path = this.pathField.getValue();
                    boolean success = LogExporter.exportLogToFile(path);
                    if (success) {
                        Minecraft.getInstance().player.displayClientMessage(
                                Component.literal("Log saved to: " + path), false);
                    } else {
                        Minecraft.getInstance().player.displayClientMessage(
                                Component.literal("Failed to save, check path"), false);
                    }
                    this.onClose();
                })
                .bounds(this.width / 2 - 50, this.height / 2 + 20, 100, 20)
                .build();
        this.addRenderableWidget(saveButton);

        Button backButton = Button.builder(
                Component.literal("Back"),
                button -> this.onClose())
                .bounds(this.width / 2 - 50, this.height / 2 + 50, 100, 20)
                .build();
        this.addRenderableWidget(backButton);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, "Enter file path (absolute or relative to game dir)",
                this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.pathField.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
