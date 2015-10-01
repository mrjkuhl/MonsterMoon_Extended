package fi.nimbus.bukkit.plugin.monstermoon.properties;

import fi.nimbus.bukkit.plugin.monstermoon.MonsterMoon;

/**
 *  "day-begins" property value
 */
public class DayBegins extends Property {
    public static final String NAME = "day-begins";

    private final Integer value;

    /**
     *  Property value from Configuration Section
     */
    public DayBegins(final org.bukkit.configuration.ConfigurationSection cfg) {
        value = (cfg == null) ? null : parseValue(cfg.getString(NAME, ""));
    }

    /**
     *  Precompile property value by combining it with the world default
     */
    public DayBegins(final DayBegins dayProperty,
                     final DayBegins worldDefault) {
        Integer value = dayProperty.value;

        value = (value == null) ? worldDefault.value            : value;
        value = (value == null) ? MonsterMoon.DEFAULT_DAYBEGINS : value;

        this.value = value;
    }

    /**
     *  Parse day begins value: midnight, sunrise, midday, sunset, [Number]
     *  @return Time of the day begin, null for undefined or unrecognized values
     */
    @Override
    protected Integer parseValue(final String value) {
        switch (value.toLowerCase()) {
            case "":     case "undef":    return null;
                         case "midnight": return -6000;
            case "dawn": case "sunrise":  return 0;
            case "noon": case "midday":   return 6000;
            case "dusk": case "sunset":   return 12000;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {};

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
        return (value == null) ? MonsterMoon.DEFAULT_DAYBEGINS : value;
    }

    /**
     *  @return Plugin default (Property not defined in Bukkit world)
     */
    @Override
    public int getFromWorld(final org.bukkit.World world) {
        return MonsterMoon.DEFAULT_DAYBEGINS;
    }

    /**
     *  Empty method (Property not defined in Bukkit world)
     */
    @Override
    public void applyToWorld(final org.bukkit.World world, final int value) {}

    /**
     *  Empty method (Property not defined in Bukkit world)
     */
    @Override
    public void applyToWorld(org.bukkit.World world) {}

    /**
     *  @return Value as string (suitable for config.yml)
     */
    @Override
    public String toString(final int value) {
        return Integer.toString(value);
    }

    /**
     *  @return Value as string (suitable for config.yml)
     */
    @Override
    public String toString() {
        return (value == null) ? "undef" : toString(getValue());
    }
}
