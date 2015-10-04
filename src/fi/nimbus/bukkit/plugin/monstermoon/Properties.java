package fi.nimbus.bukkit.plugin.monstermoon;

import fi.nimbus.bukkit.plugin.monstermoon.properties.*;

/**
 *  A group of properties
 */
@SuppressWarnings("serial")
public class Properties extends java.util.HashMap<String,Property> {

    /**
     *  Create server level properties from Bukkit World object
     */
    public Properties(final org.bukkit.World world) {
        put(new Difficulty(world));
        put(new SpawnMonsters(world));
        put(new SpawnAnimals(world));
        put(new PVP(world));
    }

    /**
     *  Create world level properties from Configuration Section
     */
    public Properties(final org.bukkit.configuration.ConfigurationSection cfg) {
        put(new Difficulty(cfg));
        put(new SpawnMonsters(cfg));
        put(new SpawnAnimals(cfg));
        put(new PVP(cfg));
        put(new DayBegins(cfg));
        put(new DayOrder(cfg));
    }

    /**
     *  Create precompiled properties by combining day with defaults
     */
    public Properties(final Properties dayProperties,
                      final Properties worldDefaults,
                      final Properties serverDefaults) {

                                     put(Difficulty.NAME,
        new Difficulty(dayProperties.get(Difficulty.NAME),
                       worldDefaults.get(Difficulty.NAME),
                      serverDefaults.get(Difficulty.NAME) ));

                                        put(SpawnMonsters.NAME,
        new SpawnMonsters(dayProperties.get(SpawnMonsters.NAME),
                          worldDefaults.get(SpawnMonsters.NAME),
                         serverDefaults.get(SpawnMonsters.NAME)));

                                       put(SpawnAnimals.NAME,
        new SpawnAnimals(dayProperties.get(SpawnAnimals.NAME),
                         worldDefaults.get(SpawnAnimals.NAME),
                        serverDefaults.get(SpawnAnimals.NAME)));

                              put(PVP.NAME,
        new PVP(dayProperties.get(PVP.NAME),
                worldDefaults.get(PVP.NAME),
               serverDefaults.get(PVP.NAME)));

                                                put(DayBegins.NAME,
        new DayBegins((DayBegins) dayProperties.get(DayBegins.NAME),
                      (DayBegins) worldDefaults.get(DayBegins.NAME)));
    }

    public Property getDifficulty() {

      return get("difficulty");
    }

    public Property getSpawnMonsters() {

      return get("spawn-monsters");
    }

    public Property getSpawnAnimals() {

      return get("spawn-animals");
    }

    public Property getPVP() {

      return get("pvp");
    }

    /**
     *  Add property to group
     *  @return Possible previous value of the property, null if none
     */
    public Property put(Property p) {
        return put(p.getName(), p);
    }

    /**
     *  Apply properties to Bukkit world
     */
    public void applyToWorld(org.bukkit.World world) {
        for (Property property : this.values()) {
            property.applyToWorld(world);
        }
    }

    /**
     *  Property group to string
     *  @return [property-name=[property-values], ...]
     */
    @Override
    public String toString() {
        java.util.List<String> list = new java.util.ArrayList<String>();
        for (java.util.Map.Entry<String,Property> entry : entrySet()) {
            list.add(entry.getKey() +"="+ entry.getValue());
        }
        return list.toString();
    }
}
