package com.unsa.ineedthelog.gui;

import com.unsa.ineedthelog.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FirstRunSetupScreen extends Screen {
    private EditBox pathField;
    private String message = "";
    private int messageTimer = 0;

    public FirstRunSetupScreen() {
        super(Component.literal("I need the log! - First Run Setup"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.pathField = new EditBox(this.font, centerX - 150, centerY - 40, 300, 20,
                Component.literal("Export path (relative to game dir or absolute)"));
        this.pathField.setMaxLength(256);
        this.pathField.setValue(ModConfig.COMMON.exportPath.get());
        this.addRenderableWidget(this.pathField);

        Button saveButton = Button.builder(
                Component.literal("Save and continue"),
                button -> {
                    String path = this.pathField.getValue().trim();
                    if (path.isEmpty()) {
                        message = "Path cannot be empty!";
                        messageTimer = 60;
                        return;
                    }
                    // 保存配置
                    ModConfig.COMMON.exportPath.set(path);
                    ModConfig.COMMON.firstRun.set(false);
                    ModConfig.COMMON_SPEC.save();
                    // 关闭界面，进入游戏主菜单
                    Minecraft.getInstance().setScreen(null);
                })
                .bounds(centerX - 160, centerY + 20, 150, 20)
                .build();
        this.addRenderableWidget(saveButton);

        Button exitButton = Button.builder(
                Component.literal("Exit game"),
                button -> Minecraft.getInstance().stop())
                .bounds(centerX + 10, centerY + 20, 150, 20)
                .build();
        this.addRenderableWidget(exitButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, "Welcome! Choose where to save exported logs",
                centerX, this.height / 2 - 80, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "Path will be saved and can be changed later in mod settings",
                centerX, this.height / 2 - 60, 0xAAAAAA);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (messageTimer > 0) {
            guiGraphics.drawCenteredString(this.font, message, centerX, this.height / 2 + 60, 0xFF5555);
            messageTimer--;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
