package fi.nimbus.bukkit.plugin.monstermoon.properties;

/**
 *  "difficulty" property value
 */
public class Difficulty extends Property {
    public static final String NAME = "difficulty";

    /**
     *  Server default value
     */
    public Difficulty(final org.bukkit.World world) {
        super(world);
    }

    /**
     *  Property value from Configuration Section
     */
    public Difficulty(final org.bukkit.configuration.ConfigurationSection cfg) {
        super(cfg);
    }

    /**
     *  Precompile property value into a ready-to-use array
     *  by combining it with the defaults
     */
    public Difficulty(final Property dayProperty,
                      final Property worldDefault,
                      final Property serverDefault) {
        super(dayProperty, worldDefault, serverDefault);
    }

    /**
     *  Parse single difficulty property value, log possible syntax errors
     *  @return 0-3 for different difficulty levels, null for undefined or unrecognized values
     */
    @Override
    protected Integer parseValue(final String value) {
        Integer result = parseExtendedValue(value);
        if (result != null) return result;

        switch (value.toLowerCase()) {
            case "":  case "undef":               return null;
            case "0": case "peaceful": case "p":  return 0;
            case "1": case "easy":     case "e":  return 1;
            case "2": case "normal":   case "n":  return 2;
            case "3": case "hard":     case "h":  return 3;
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
     *  @return Value from Bukkit world
     */
    @Override
    public int getFromWorld(org.bukkit.World world) {
        return world.getDifficulty().getValue();
    }

    /**
     *  Apply property value to Bukkit world
     */
    @Override
    public void applyToWorld(org.bukkit.World world, int value) {
        world.setDifficulty(org.bukkit.Difficulty.getByValue(value));
    }

    /**
     *  @return Value as string (suitable for config.yml)
     */
    @Override
    public String toString(final int value) {
        switch (value) {
            case 0:   return "peaceful";
            case 1:   return "easy";
            case 2:   return "normal";
            case 3:   return "hard";
            default:  return super.toString(value);
        }
    }
}
