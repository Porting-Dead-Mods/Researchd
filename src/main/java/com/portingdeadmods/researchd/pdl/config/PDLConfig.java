package com.portingdeadmods.researchd.pdl.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PDLConfig {
    private final Map<String, Value<?>> configValues;
    private final Map<String, ModConfigSpec.ConfigValue<?>> specConfigValues;
    private final ModConfig.Type type;
    private final Class<?> configClass;

    public PDLConfig(Class<?> configClass, ModConfig.Type type) {
        this.configClass = configClass;
        this.configValues = new HashMap<>();
        this.specConfigValues = new HashMap<>();
        this.type = type;
    }

    public void addConfigValue(String path, Value<?> value) {
        this.configValues.put(path, value);
    }

    public Value<?> getValue(String path) {
        return this.configValues.get(path);
    }

    public ModConfigSpec.ConfigValue<?> getSpecValue(String path) {
        return this.specConfigValues.get(path);
    }

    public ModConfig.Type getType() {
        return type;
    }

    public Set<String> getConfigPaths() {
        return this.configValues.keySet();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void register(ModContainer modContainer) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        for (String path : this.getConfigPaths()) {
            Value<?> value = this.getValue(path);
            if (value.type() != null) {
                builder.comment(value.comment());

                this.specConfigValues.put(path, switch (value.type()) {
                    case BYTE, SHORT, INTEGER -> builder.defineInRange(path, (int) value.defaultValue(), (int) value.min(), (int) value.max());
                    case LONG -> builder.defineInRange(path, (long) value.defaultValue(), (long) value.min(), (long) value.max());
                    case FLOAT, DOUBLE -> builder.defineInRange(path, (double) value.defaultValue(), value.min(), value.max());
                    case STRING -> builder.define(path, value.defaultValue());
                    case BOOLEAN -> builder.define(path, (boolean) value.defaultValue());
                    case ENUM -> builder.defineEnum(path, (Enum) value.defaultValue());
                    case LIST -> builder.defineList(path, (List<String>) value.defaultValue(), String::new, s -> true);
                    case MAP -> throw new RuntimeException("Map NYI");
                });
            }
        }
        ModConfigSpec spec = builder.build();
        modContainer.registerConfig(this.type, spec);
        PDLConfigManager.CONFIGS.put(spec, this);
        PDLConfigManager.CONFIGS_BY_CLASS.put(this.configClass, this);
    }

    public record Value<T>(Field field, @Nullable Type type, T defaultValue, String key, String category, String name, String comment, double min, double max) {
        public enum Type {
            BYTE(true),
            SHORT(true),
            INTEGER(true),
            LONG(true),
            FLOAT(true),
            DOUBLE(true),
            STRING(false),
            BOOLEAN(false),
            ENUM(false),
            LIST(false),
            MAP(false);

            private final boolean number;

            Type(boolean number) {
                this.number = number;
            }

            public boolean isNumber() {
                return number;
            }
        }
    }

}
