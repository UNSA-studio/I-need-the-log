package com.unsa.ineedthelog;

import com.unsa.ineedthelog.config.ModConfig;
import com.unsa.ineedthelog.gui.FirstRunSetupScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Ineedthelog.MOD_ID)
public class Ineedthelog {
    public static final String MOD_ID = "i_need_the_log";

    public Ineedthelog(ModContainer container) {
        // 注册配置
        ModConfig.register(container);
        // 仅在客户端执行首次运行检查
        if (FMLEnvironment.dist == Dist.CLIENT) {
            container.addListener(this::clientSetup);
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 如果首次运行标志为 true，则显示设置界面
            if (ModConfig.COMMON.firstRun.get()) {
                Minecraft.getInstance().setScreen(new FirstRunSetupScreen());
            }
        });
    }
}
