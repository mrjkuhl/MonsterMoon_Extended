package fi.nimbus.bukkit.plugin.monstermoon.properties;

import fi.nimbus.bukkit.plugin.monstermoon.MonsterMoon;

/**
 *  A single property in a property group
 */
public abstract class Property {
    protected static final int SERVER_DEFAULT = -2;
    protected static final int WORLD_DEFAULT  = -1;
    protected static final int NO_CHANGE      = 1000;

    private final Integer[] values;

    /**
     *  Property with undefined value
     */
    protected Property() {
        values = null;
    }

    /**
     *  Property from Bukkit world
     */
    protected Property(final org.bukkit.World world) {
        values = new Integer[] { getFromWorld(world) };
    }

    /**
     *  Property from Configuration Section
     */
    protected Property(final org.bukkit.configuration.ConfigurationSection cfg) {
        values = (cfg == null) ? new Integer[] { null }
                               : parseValues(cfg.getString(getName(), ""));
    }

    /**
     *  Precompile property value into a ready-to-use array
     *  by combining it with the defaults
     */
    public Property(final Property dayProperty,
                    final Property worldDefault,
                    final Property serverDefault) {
        /*
         *  Probability is expressed by having multiple possible values
         *  on the same property field line.
         *
         *  In addition, we have three levels of properties:
         *
         *      Calendar day   (can have multiple possible values)
         *      World default  (can have multiple possible values)
         *      Server default (always only one value)
         *
         *  Consider the following:
         *
         *      Calendar day:  no-change no-change world-default
         *      World default: true false
         *
         *  To express the Calendar day probabilities correcty we must multiply
         *  each of its values by the length of the World default array, thus:
         *
         *      Calendar day:  no-change no-change no-change no-change true false
         *                     \_ 1st no-change _/ \_ 2nd no-change _/ \_ w.d. _/
         */
        final Integer serverValue = serverDefault.values[0];
        final java.util.List<Integer> values = new java.util.ArrayList<Integer>();

        for (final Integer dayValue : dayProperty.values) {
            for (final Integer worldValue : worldDefault.values) {
                Integer value = dayValue;

                value = (value == null || value < 0) ? worldValue  : value;
                value = (value == null || value < 0) ? serverValue : value;

                values.add(value);
            }
        }

        this.values = values.toArray(new Integer[0]);
    }

    /**
     *  Parse a list of whitespace separated property values
     *  into an integer array
     */
    protected Integer[] parseValues(final String values) {
        java.util.List<Integer> results = new java.util.ArrayList<Integer>();

        for (final String value : values.split("\\s+")) {
            results.add(parseValue(value));
        }

        return results.toArray(new Integer[0]);
    }

    /**
     *  Parse single (non-standard) extended property value: server-default, default, no-change
     *  @return SERVER_DEFAULT, WORLD_DEFAULT, NO_CHANGE, null for unrecognized values
     */
    protected Integer parseExtendedValue(final String value) {
        switch (value.toLowerCase()) {
            case "server-default": case "server":                     case "s": return SERVER_DEFAULT;
            case "world-default":  case "default":  case "world":     case "d": return WORLD_DEFAULT;
            case "no-change":      case "nochange": case "unchanged": case "_": return NO_CHANGE;
        }
        return null;
    }

    /**
     *  Parse single boolean property value, log possible syntax errors
     *  @return 1 for true, 0 for false, null for undefined or unrecognized values
     */
    protected Integer parseValue(final String value) {
        final Integer result = parseExtendedValue(value);
        if (result != null) return result;

        switch (value.toLowerCase()) {
            case "":  case "undef":                                 return null;
            case "1": case "true":  case "yes": case "t": case "y": return 1;
            case "0": case "false": case "no":  case "f": case "n": return 0;
        }

        syntaxError(value);
        return null;
    }

    /**
     *  Log a parsing error
     */
    protected void syntaxError(final String value) {
        MonsterMoon.message("Unknown "+ getName() +" value '"+ value +"'");
    }

    /**
     *  @return Name of this property
     */
    public abstract String getName();

    /**
     *  @return Random value from a precomplied array
     */
    public int getValue() {
        if (values.length < 1) {
            return NO_CHANGE;
        } else if (values.length == 1) {
            return values[0];
        } else {
            return values[MonsterMoon.random.nextInt(values.length)];
        }
    }

    /**
     *  @return Value from Bukkit world
     */
    public abstract int getFromWorld(org.bukkit.World world);

    /**
     *  Apply property value to Bukkit world
     */
    public abstract void applyToWorld(org.bukkit.World world, int value);

    /**
     *  Apply this property to Bukkit world
     */
    public void applyToWorld(org.bukkit.World world) {
        final int value = getValue();
        if (value == NO_CHANGE) return;

        final int worldValue = getFromWorld(world);
        if (value == worldValue) return;

        applyToWorld(world, value);
        MonsterMoon.message(getName() +" set to "+ toString(value));
    }

    /**
     *  @return Value as string (suitable for config.yml)
     */
    public String toString(final int value) {
        switch (value) {
            case SERVER_DEFAULT: return "server-default";
            case WORLD_DEFAULT:  return "default";
            case NO_CHANGE:      return "no-change";
            case 1:              return "true";
            case 0:              return "false";
            default:             return Integer.toString(value);
        }
    }

    /**
     *  @return Array of possible values
     */
    @Override
    public String toString() {
        java.util.List<String> list = new java.util.ArrayList<String>();
        for (Integer value : values) {
            list.add((value == null) ? "undef" : toString(value));
        }
        return list.toString();
    }
}
