package fi.nimbus.bukkit.plugin.monstermoon.properties;

/**
 *  "spawn-animals" property value
 */
public class SpawnAnimals extends Property {
    public static final String NAME = "spawn-animals";

    /**
     *  Server default value
     */
    public SpawnAnimals(final org.bukkit.World world) {
        super(world);
    }

    /**
     *  Property value from Configuration Section
     */
    public SpawnAnimals(final org.bukkit.configuration.ConfigurationSection cfg) {
        super(cfg);
    }

    /**
     *  Precompile property value into a ready-to-use array
     *  by combining it with the defaults
     */
    public SpawnAnimals(final Property dayProperty,
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
        return world.getAllowAnimals() ? 1 : 0;
    }

    /**
     *  Apply property value to Bukkit world
     */
    @Override
    public void applyToWorld(final org.bukkit.World world, final int value) {
        world.setSpawnFlags(world.getAllowMonsters(), (value == 1));
    }
}
