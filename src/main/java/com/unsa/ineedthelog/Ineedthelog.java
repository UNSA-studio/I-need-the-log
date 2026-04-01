package com.unsa.ineedthelog;

import com.unsa.ineedthelog.config.ModConfig;
import com.unsa.ineedthelog.gui.FirstRunSetupScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Ineedthelog.MOD_ID)
public class Ineedthelog {
    public static final String MOD_ID = "i_need_the_log";
    private static final Logger LOGGER = LoggerFactory.getLogger(Ineedthelog.class);

    public Ineedthelog(ModContainer container, IEventBus modBus) {
        ModConfig.register(container);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(this::clientSetup);
        }
        LOGGER.info("I need the log! mod loaded.");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Client setup: firstRun = {}", ModConfig.COMMON.firstRun.get());
            if (ModConfig.COMMON.firstRun.get()) {
                LOGGER.info("Showing first run setup screen (delayed)");
                // 延迟1秒，确保游戏窗口完全加载
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                    Minecraft.getInstance().execute(() -> {
                        Minecraft.getInstance().setScreen(new FirstRunSetupScreen());
                    });
                }).start();
            }
        });
    }
}
