package com.unsa.ineedthelog.util;

import net.minecraft.client.Minecraft;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogExporter {
    public static boolean exportLogToFile(String destination) {
        try {
            Path logFile = Minecraft.getInstance().gameDirectory.toPath().resolve("logs/latest.log");
            if (!Files.exists(logFile)) return false;
            String logContent = Files.readString(logFile);
            Path targetPath = Paths.get(destination);
            if (!targetPath.isAbsolute()) {
                targetPath = Minecraft.getInstance().gameDirectory.toPath().resolve(destination);
            }
            Files.createDirectories(targetPath.getParent());
            Files.writeString(targetPath, logContent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
