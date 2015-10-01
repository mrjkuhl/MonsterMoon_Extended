package fi.nimbus.bukkit.plugin.monstermoon.properties;

/**
 *  "pvp" property value
 */
public class PVP extends Property {
    public static final String NAME = "pvp";

    /**
     *  Server default value
     */
    public PVP(final org.bukkit.World world) {
        super(world);
    }

    /**
     *  Property value from Configuration Section
     */
    public PVP(final org.bukkit.configuration.ConfigurationSection cfg) {
        super(cfg);
    }

    /**
     *  Precompile property value into a ready-to-use array
     *  by combining it with the defaults
     */
    public PVP(final Property dayProperty,
               final Property worldDefault,
               final Property serverDefault) {
        super(dayProperty, worldDefault, serverDefault);
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
    public int getFromWorld(final org.bukkit.World world) {
        return world.getPVP() ? 1 : 0;
    }

    /**
     *  Apply property value to Bukkit world
     */
    @Override
    public void applyToWorld(final org.bukkit.World world, final int value) {
        world.setPVP(value == 1);
    }
}
