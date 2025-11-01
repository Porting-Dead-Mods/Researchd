package com.portingdeadmods.researchd;

import com.portingdeadmods.researchd.pdl.config.ConfigValue;

public class TestConfig {
    @ConfigValue(name = "Test Name", comment = "Some random name lol idk")
    public static String testName = "Dev";

    @ConfigValue(name = "Test Int", comment = "Slaaaaaaaaaaay", range = {0, 99})
    public static int testInt = 0;

    @ConfigValue(name = "Test Type", comment = "Test typeeeee lol")
    public static Type testType = Type.NONE;

    public enum Type {
        NONE,
        SINGLE,
        MULTIPLE,
    }

}
