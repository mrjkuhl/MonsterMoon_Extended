package fi.nimbus.bukkit.plugin.monstermoon.properties;

import fi.nimbus.bukkit.plugin.monstermoon.MonsterMoon;

/**
 *  "day-order" property value
 */
public class DayOrder extends Property {
    public static final String NAME   = "day-order";
    public static final int LINEAR    = 0;
    public static final int UNIVERSAL = 1;
    public static final int RANDOM    = 2;

    private Integer value;

    /**
     *  Property value from Configuration Section
     */
    public DayOrder(final org.bukkit.configuration.ConfigurationSection cfg) {
        value = (cfg == null) ? null : parseValue(cfg.getString(NAME, ""));
    }

    /**
     *  Parse day order value: linear, universal, random
     *  @return LINEAR, UNIVERSAL, RANDOM, null for undefined or unrecognized values
     */
    @Override
    protected Integer parseValue(final String value) {
        switch (value.toLowerCase()) {
            case "":  case "undef":     return null;
            case "l": case "linear":    return LINEAR;
            case "u": case "universal": return UNIVERSAL;
            case "r": case "random":    return RANDOM;
        }

        syntaxError(value);
        return null;
    }

    /**
     *  @return Name of this property
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     *  @return Value of this property, Plugin default if not defined
     */
    @Override
    public int getValue() {
        return (value == null) ? MonsterMoon.DEFAULT_DAYORDER : value;
    }

    /**
     *  @return Plugin default (Property not defined in Bukkit world)
     */
    @Override
    public int getFromWorld(final org.bukkit.World world) {
        return MonsterMoon.DEFAULT_DAYORDER;
    }

    /**
     *  Empty method (Property not defined in Bukkit world)
     */
    @Override
    public void applyToWorld(final org.bukkit.World world, final int value) {}

    /**
     *  @return Value as string (suitable for config.yml)
     */
    @Override
    public String toString(final int value) {
        switch (value) {
            case LINEAR:     return "linear";
            case UNIVERSAL:  return "universal";
            case RANDOM:     return "random";
            default:         return super.toString(value);
        }
    }

    /**
     *  @return Value as string (suitable for config.yml)
     */
    @Override
    public String toString() {
        return (value == null) ? "undef" : toString(getValue());
    }
}
