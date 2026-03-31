package com.unsa.ineedthelog.gui;

import com.unsa.ineedthelog.config.ModConfig;
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
    private String feedbackMessage = null;
    private int feedbackTimer = 0;

    public FileSaveScreen(Screen parent) {
        super(Component.literal("Save Log File"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        String defaultPath = ModConfig.COMMON.exportPath.get();
        this.pathField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20,
                Component.literal("File Path"));
        this.pathField.setMaxLength(256);
        this.pathField.setValue(defaultPath);
        this.addRenderableWidget(this.pathField);

        boolean isChinese = System.getProperty("user.language", "en").startsWith("zh");
        String saveText = isChinese ? "保存" : "Save";
        String backText = isChinese ? "返回" : "Back";

        Button saveButton = Button.builder(
                Component.literal(saveText),
                button -> {
                    String path = this.pathField.getValue();
                    boolean success = LogExporter.exportLogToFile(path);
                    if (success) {
                        feedbackMessage = isChinese ? "日志已保存至 " + path : "Log saved to " + path;
                    } else {
                        feedbackMessage = isChinese ? "保存失败，请检查路径" : "Failed to save, check path";
                    }
                    feedbackTimer = 80;
                })
                .bounds(this.width / 2 - 50, this.height / 2 + 20, 100, 20)
                .build();
        this.addRenderableWidget(saveButton);

        Button backButton = Button.builder(
                Component.literal(backText),
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
        boolean isChinese = System.getProperty("user.language", "en").startsWith("zh");
        String instruction = isChinese ? "请输入文件路径（绝对或相对游戏目录）" :
                "Enter file path (absolute or relative to game dir)";
        guiGraphics.drawCenteredString(this.font, instruction,
                this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.pathField.render(guiGraphics, mouseX, mouseY, partialTick);

        if (feedbackMessage != null && feedbackTimer > 0) {
            int color = feedbackMessage.startsWith(isChinese ? "日志已保存" : "Log saved") ? 0x00FF00 : 0xFF5555;
            guiGraphics.drawCenteredString(this.font, feedbackMessage,
                    this.width / 2, this.height / 2 + 80, color);
            feedbackTimer--;
            if (feedbackTimer <= 0) feedbackMessage = null;
        }
    }
}
