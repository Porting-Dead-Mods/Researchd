package com.portingdeadmods.researchd.pdl.config;

import com.portingdeadmods.portingdeadlibs.PortingDeadLibs;
import com.portingdeadmods.researchd.Researchd;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@EventBusSubscriber(modid = Researchd.MODID)
public class PDLConfigHelper {
    public static PDLConfig registerConfig(Class<?> configClass, ModConfig.Type configType) {
        PDLConfig config = new PDLConfig(configClass, configType);

        for (Field field : configClass.getFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    PortingDeadLibs.LOGGER.error("Non-static field ({}) in class {} annotated with @ConfigValue", field.getName(), configClass.getName());
                    continue;
                }

                if (Modifier.isFinal(field.getModifiers())) {
                    PortingDeadLibs.LOGGER.error("Field ({}) in class {} annotated with @ConfigValue is final", field.getName(), configClass.getName());
                    continue;
                }

                ConfigValue annotation = field.getAnnotation(ConfigValue.class);
                String category = annotation.category();
                if (!category.isEmpty()) {
                    category += ".";
                }
                String key = annotation.key();
                if (key.isEmpty()) {
                    key = camelToSnake(field.getName());
                }

                String path = category + key;

                PDLConfig.Value.Type type = classToConfigType(field.getType());

                if (type == null) {
                    PortingDeadLibs.LOGGER.error("Field ({}) in class {}, annotated with @ConfigValue has invalid type for config. Supported types are: byte, short, int, long, float, double, String, boolean, List, Map - received type {} instead", field.getName(), configClass.getName(), field.getType().getName());
                    continue;
                }

                Object defaultValue;
                try {
                    defaultValue = field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                double min = 0;
                double max = 0;
                if (type.isNumber()) {
                    if (annotation.range().length > 2) {
                        PortingDeadLibs.LOGGER.error("Field ({}) in class {}, annotated with @ConfigValue is a number but has an invalid range. Range array can't be bigger than 2 elements.", field.getName(), configClass.getName());
                        continue;
                    } else if (annotation.range().length == 1) {
                        max = annotation.range()[0];
                    } else if (annotation.range().length == 2) {
                        min = annotation.range()[0];
                        max = annotation.range()[1];
                    }
                }

                PDLConfig.Value<?> value = new PDLConfig.Value<>(field, type, defaultValue, key, annotation.category(), annotation.name(), annotation.comment(), min, max);

                config.addConfigValue(path, value);
            }
        }

        return config;
    }

    public static void generateConfigNames(Class<?> configClass, String modid, BiConsumer<String, String> addTranslationFunction) {
        PDLConfig config = PDLConfigManager.CONFIGS_BY_CLASS.get(configClass);

        Set<String> categories = new HashSet<>();

        for (String path : config.getConfigPaths()) {
            PDLConfig.Value<?> value = config.getValue(path);

            String category = value.category();
            if (!category.isEmpty() && !categories.contains(category)) {
                String categoryName = Character.toUpperCase(category.charAt(0)) + category.substring(1);
                addTranslationFunction.accept(modid + ".configuration." + category, categoryName);
                categories.add(category);
            }

            addTranslationFunction.accept(modid + ".configuration." + value.key(), value.name());
        }
    }

    private static <T> PDLConfig.Value.@Nullable Type classToConfigType(Class<T> type) {
        if (type == byte.class) {
            return PDLConfig.Value.Type.BYTE;
        } else if (type == short.class) {
            return PDLConfig.Value.Type.SHORT;
        } else if (type == int.class) {
            return PDLConfig.Value.Type.INTEGER;
        } else if (type == long.class) {
            return PDLConfig.Value.Type.LONG;
        } else if (type == float.class) {
            return PDLConfig.Value.Type.FLOAT;
        } else if (type == double.class) {
            return PDLConfig.Value.Type.DOUBLE;
        } else if (type == String.class) {
            return PDLConfig.Value.Type.STRING;
        } else if (type == boolean.class) {
            return PDLConfig.Value.Type.BOOLEAN;
        } else if (List.class.isAssignableFrom(type)) {
            return PDLConfig.Value.Type.LIST;
        } else if (Map.class.isAssignableFrom(type)) {
            return PDLConfig.Value.Type.MAP;
        } else if (Enum.class.isAssignableFrom(type)) {
            return PDLConfig.Value.Type.ENUM;
        }
        return null;
    }

    @SubscribeEvent
    static void onConfigReloaded(ModConfigEvent event) {
        PDLConfig config = PDLConfigManager.CONFIGS.get(event.getConfig().getSpec());
        for (String path : config.getConfigPaths()) {
            PDLConfig.Value<?> value = config.getValue(path);
            Field field = value.field();
            setFieldValue(field, config.getSpecValue(path).get());
        }
    }

    public static String camelToSnake(String str) {
        StringBuilder result = new StringBuilder();

        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));

        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    private static void setFieldValue(Field field, Object value) {
        try {
            Class<?> type = field.getType();

            if (type.isPrimitive()) {
                if (type == int.class) field.setInt(null, ((Number) value).intValue());
                else if (type == boolean.class) field.setBoolean(null, (Boolean) value);
                else if (type == long.class) field.setLong(null, ((Number) value).longValue());
                else if (type == double.class) field.setDouble(null, ((Number) value).doubleValue());
                else if (type == float.class) field.setFloat(null, ((Number) value).floatValue());
                else if (type == short.class) field.setShort(null, ((Number) value).shortValue());
                else if (type == byte.class) field.setByte(null, ((Number) value).byteValue());
                else if (type == char.class) field.setChar(null, (Character) value);
                else throw new IllegalArgumentException("Unsupported primitive type: " + type);
            } else {
                field.set(null, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
