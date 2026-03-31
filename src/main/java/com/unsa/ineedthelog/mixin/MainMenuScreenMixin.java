package com.unsa.ineedthelog.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MainMenuScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(MainMenuScreen.class)
public abstract class MainMenuScreenMixin extends Screen {
    protected MainMenuScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addTestButton(CallbackInfo ci) {
        int centerX = this.width / 2;
        int buttonWidth = 150;
        int buttonHeight = 20;
        int y = this.height / 2 + 50;

        Button testBtn = Button.builder(
                Component.literal("Test Button"),
                button -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.displayClientMessage(
                                Component.literal("Test button clicked!"), false);
                    }
                })
                .bounds(centerX - buttonWidth / 2, y, buttonWidth, buttonHeight)
                .build();
        this.addRenderableWidget(testBtn);
    }
}
