package com.unsa.ineedthelog.util;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class LogExporter {
    public static boolean exportLogToFile(String destination) {
        try {
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

            String deviceInfo = getDeviceInfo();
            String finalContent = deviceInfo + "\n" + fullLog.toString();

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
        StringBuilder info = new StringBuilder();
        info.append("===================================\n");
        info.append("Device Information Detail\n");
        info.append("===================================\n");
        info.append("Java version: ").append(System.getProperty("java.version")).append("\n");
        info.append("Java Publisher: ").append(System.getProperty("java.vendor")).append("\n");
        info.append("Minecraft version: ").append(mc.getLaunchedVersion()).append("\n");
        // 获取 NeoForge 版本
        String neoVersion = ModList.get().getModContainerById("neoforge")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("Unknown");
        info.append("Module driver: NeoForge ").append(neoVersion).append("\n");
        info.append("Number of modules: ").append(ModList.get().size()).append("\n");
        info.append("Minecraft Launcher: ").append(System.getProperty("minecraft.launcher.brand", "Unknown")).append(" ").append(System.getProperty("minecraft.launcher.version", "")).append("\n");
        info.append("Storage allocated to Minecraft: ").append(Runtime.getRuntime().maxMemory() / 1024 / 1024).append(" MB\n");
        info.append("Equipment system: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append(" (").append(System.getProperty("os.arch")).append(")\n");
        info.append("Rendering method: OpenGL ES 3.2 (Krypton)\n");
        info.append("Login method: ").append(mc.getUser().getType() != null ? mc.getUser().getType().toString() : "Unknown").append("\n");
        info.append("CPU information: ").append(System.getenv("PROCESSOR_IDENTIFIER") != null ? System.getenv("PROCESSOR_IDENTIFIER") : "Unknown").append("\n");
        info.append("GPU UNK0：Mali-G71 (Krypton wrapper)\n");
        info.append("Crash Zone: <not specified>\n");
        info.append("Minecraft Crash Code: N/A\n");
        info.append("Average performance index: N/A\n");
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
