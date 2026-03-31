package com.unsa.ineedthelog.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.repository.PackRepository;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class LogExporter {
    public static boolean exportLogToFile(String destination) {
        try {
            // 1. 收集所有日志文件
            Path logsDir = Minecraft.getInstance().gameDirectory.toPath().resolve("logs");
            if (!Files.exists(logsDir)) return false;

            List<Path> logFiles = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, entry -> {
                String name = entry.getFileName().toString();
                return name.endsWith(".log") || name.endsWith(".log.gz");
            })) {
                for (Path p : stream) logFiles.add(p);
            }
            logFiles.sort(Comparator.comparing(LogExporter::extractOrder));

            // 2. 构建完整日志内容（按时间顺序）
            StringBuilder fullLog = new StringBuilder();
            for (Path file : logFiles) {
                fullLog.append("===== ").append(file.getFileName()).append(" =====\n");
                if (file.toString().endsWith(".gz")) {
                    fullLog.append(readGzipped(file));
                } else {
                    fullLog.append(Files.readString(file));
                }
                fullLog.append("\n\n");
            }

            // 3. 获取设备信息
            String deviceInfo = getDeviceInfo();

            // 4. 将设备信息放在最前面
            String finalContent = deviceInfo + "\n" + fullLog.toString();

            // 5. 写入目标文件
            Path targetPath = Paths.get(destination);
            if (!targetPath.isAbsolute()) {
                targetPath = Minecraft.getInstance().gameDirectory.toPath().resolve(destination);
            }
            Files.createDirectories(targetPath.getParent());
            Files.writeString(targetPath, finalContent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getDeviceInfo() {
        Minecraft mc = Minecraft.getInstance();
        GameConfig config = mc.gameDirectory != null ? mc.gameConfig : null;
        StringBuilder info = new StringBuilder();
        info.append("===================================\n");
        info.append("Device Information Detail\n");
        info.append("===================================\n");
        info.append("Java version: ").append(System.getProperty("java.version")).append("\n");
        info.append("Java Publisher: ").append(System.getProperty("java.vendor")).append("\n");
        info.append("Minecraft version: ").append(mc.getLaunchedVersion()).append("\n");
        info.append("Module driver: NeoForge ").append(net.neoforged.fml.loading.FMLEnvironment.loaderVersion).append("\n");
        info.append("Number of modules: ").append(net.neoforged.fml.ModList.get().size()).append("\n");
        info.append("Minecraft Launcher: ").append(System.getProperty("minecraft.launcher.brand", "Unknown")).append(" ").append(System.getProperty("minecraft.launcher.version", "")).append("\n");
        info.append("Storage allocated to Minecraft: ").append(Runtime.getRuntime().maxMemory() / 1024 / 1024).append(" MB\n");
        info.append("Equipment system: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append(" (").append(System.getProperty("os.arch")).append(")\n");
        info.append("Rendering method: ").append(mc.getWindow().getRendererType()).append("\n");
        // 登录方式：通过获取用户类型判断
        String userType = mc.getUser().getType() != null ? mc.getUser().getType().toString() : "Unknown";
        info.append("Login method: ").append(userType).append("\n");
        // CPU 信息（简单获取）
        info.append("CPU information: ").append(System.getenv("PROCESSOR_IDENTIFIER") != null ? System.getenv("PROCESSOR_IDENTIFIER") : "Unknown").append("\n");
        info.append("GPU UNK0：").append(mc.getWindow().getPrimaryMonitor().getManufacturer() != null ? mc.getWindow().getPrimaryMonitor().getManufacturer() : "Unknown").append("\n");
        // 崩溃区域：此处由调用者提供，但我们无法在导出时知道崩溃场景，留空
        info.append("Crash Zone: <not specified>\n");
        // 崩溃码：无法获取，留空
        info.append("Minecraft Crash Code: N/A\n");
        // 平均性能指数：简单估算
        info.append("Average performance index: ").append("N/A\n");
        info.append("===================================\n");
        return info.toString();
    }

    private static long extractOrder(Path p) {
        String name = p.getFileName().toString();
        int lastDash = name.lastIndexOf('-');
        int dot = name.indexOf('.');
        if (lastDash > 0 && dot > lastDash) {
            try {
                return Long.parseLong(name.substring(lastDash + 1, dot));
            } catch (NumberFormatException ignored) {}
        }
        try {
            return Files.getLastModifiedTime(p).toMillis();
        } catch (IOException e) {
            return 0;
        }
    }

    private static String readGzipped(Path path) throws IOException {
        try (InputStream is = new GZIPInputStream(Files.newInputStream(path));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toString();
        }
    }
}
