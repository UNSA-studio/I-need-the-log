package com.unsa.ineedthelog.mixin;

import com.unsa.ineedthelog.gui.FileSaveScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.screens.CrashReportScreen")
public abstract class CrashReportScreenMixin extends Screen {
    protected CrashReportScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addExportButton(CallbackInfo ci) {
        int centerX = this.width / 2;
        int buttonWidth = 150;
        int buttonHeight = 20;
        int cancelY = this.height - 30;
        int exportY = cancelY - buttonHeight - 5;

        Button exportBtn = Button.builder(
                Component.literal("Export ERROR.txt"),
                button -> Minecraft.getInstance().setScreen(new FileSaveScreen(this)))
                .bounds(centerX - buttonWidth / 2, exportY, buttonWidth, buttonHeight)
                .build();
        this.addRenderableWidget(exportBtn);
    }
}
