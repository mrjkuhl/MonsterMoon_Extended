package fi.nimbus.bukkit.plugin.monstermoon.properties;

import fi.nimbus.bukkit.plugin.monstermoon.MonsterMoon;
import fi.nimbus.bukkit.plugin.monstermoon.Properties;

/**
 *  "spawn-monsters" property value
 */
public class SpawnMonsters extends Property {
    public static final String NAME = "spawn-monsters";

    /**
     *  Server default value
     */
    public SpawnMonsters(final org.bukkit.World world) {
        super(world);
    }

    /**
     *  Property value from Configuration Section
     */
    public SpawnMonsters(final org.bukkit.configuration.ConfigurationSection cfg) {
        super(cfg);
    }

    /**
     *  Precompile property value into a ready-to-use array
     *  by combining it with the defaults
     */
    public SpawnMonsters(final Property dayProperty,
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
        return world.getAllowMonsters() ? 1 : 0;
    }

    /**
     *  Apply property value to Bukkit world
     */
    @Override
    public void applyToWorld(final org.bukkit.World world, final int value) {
        world.setSpawnFlags((value == 1), world.getAllowAnimals());
    }
}
