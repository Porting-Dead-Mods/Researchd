package com.portingdeadmods.researchd.pdl.config;

import net.neoforged.fml.config.IConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class PDLConfigManager {
    public static final Map<IConfigSpec, PDLConfig> CONFIGS = new HashMap<>();
    public static final Map<Class<?>, PDLConfig> CONFIGS_BY_CLASS = new HashMap<>();
}
