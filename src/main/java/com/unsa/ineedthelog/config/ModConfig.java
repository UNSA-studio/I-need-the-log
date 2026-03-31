package com.unsa.ineedthelog.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static class Common {
        public final ModConfigSpec.ConfigValue<String> exportPath;
        public final ModConfigSpec.ConfigValue<Boolean> firstRun;

        Common(ModConfigSpec.Builder builder) {
            builder.comment("General settings").push("general");
            exportPath = builder
                    .comment("Directory or file path where logs will be exported (relative to game dir or absolute)")
                    .define("exportPath", "ERROR-LOG/error.txt");
            firstRun = builder
                    .comment("Whether it's the first run (shows setup screen)")
                    .define("firstRun", true);
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static void register(ModContainer container) {
        container.registerConfig(Type.COMMON, COMMON_SPEC);
    }
}
