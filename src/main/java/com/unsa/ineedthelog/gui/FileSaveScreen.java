package com.unsa.ineedthelog.gui;

import com.unsa.ineedthelog.config.ModConfig;
import com.unsa.ineedthelog.util.LogExporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSaveScreen extends Screen {
    private final Screen parent;
    private EditBox pathField;
    private String feedbackMessage = null;
    private int feedbackTimer = 0;

    public FileSaveScreen(Screen parent) {
        super(Component.literal("保存日志文件"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        String defaultPath = ModConfig.COMMON.exportPath.get();
        this.pathField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20,
                Component.literal("文件路径（目录或完整文件路径）"));
        this.pathField.setMaxLength(256);
        this.pathField.setValue(defaultPath);
        this.addRenderableWidget(this.pathField);

        Button saveButton = Button.builder(
                Component.literal("保存"),
                button -> {
                    String userPath = this.pathField.getValue().trim();
                    if (userPath.isEmpty()) {
                        feedbackMessage = "路径不能为空！";
                        feedbackTimer = 80;
                        return;
                    }

                    // 确定最终导出路径
                    String finalPath = userPath;
                    // 如果用户输入不是以 .log 或 .txt 结尾，视为目录，自动生成时间戳文件名
                    if (!userPath.endsWith(".log") && !userPath.endsWith(".txt")) {
                        // 生成时间戳文件名: YYYY.MM.DD_HH:mm-ERROR-LOG.txt
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH:mm"));
                        String fileName = timestamp + "-ERROR-LOG.txt";
                        // 确保路径以分隔符结尾
                        Path dir = Paths.get(userPath);
                        if (!userPath.endsWith("/") && !userPath.endsWith("\\")) {
                            // 简单处理，确保目录分隔符正确，这里不做复杂处理，交给 Paths
                            dir = dir.resolve(fileName);
                        } else {
                            dir = dir.resolve(fileName);
                        }
                        finalPath = dir.toString();
                    }

                    boolean success = LogExporter.exportLogToFile(finalPath);
                    if (success) {
                        feedbackMessage = "日志已保存至 " + finalPath;
                    } else {
                        feedbackMessage = "保存失败，请检查路径";
                    }
                    feedbackTimer = 80;

                    // 保存用户输入的原始路径到配置（下次默认显示）
                    ModConfig.COMMON.exportPath.set(userPath);
                    ModConfig.COMMON_SPEC.save();
                })
                .bounds(this.width / 2 - 50, this.height / 2 + 20, 100, 20)
                .build();
        this.addRenderableWidget(saveButton);

        Button backButton = Button.builder(
                Component.literal("返回"),
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
        guiGraphics.drawCenteredString(this.font, "请输入文件路径（绝对或相对游戏目录）",
                this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.pathField.render(guiGraphics, mouseX, mouseY, partialTick);

        if (feedbackMessage != null && feedbackTimer > 0) {
            int color = feedbackMessage.startsWith("日志已保存") ? 0x00FF00 : 0xFF5555;
            guiGraphics.drawCenteredString(this.font, feedbackMessage,
                    this.width / 2, this.height / 2 + 80, color);
            feedbackTimer--;
            if (feedbackTimer <= 0) feedbackMessage = null;
        }
    }
}
