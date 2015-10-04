package fi.nimbus.bukkit.plugin.monstermoon;

import fi.nimbus.bukkit.plugin.monstermoon.properties.DayBegins;

/**
 *  A single precompiled calendar day
 */
public class Day {
    private final String     name;
    private final Properties properties;
    private final String[]   commands;
    private final int        begins;

    /**
     *  Compile day pointed by Configuration Section by using the defaults from the calendar
     */
    public Day(final Calendar calendar, String name, final org.bukkit.configuration.ConfigurationSection cfg) {
        this.name = name;
        properties = new Properties((cfg == null) ? calendar.getWorld().getWorldDefaults() : new Properties(cfg),
                                    calendar.getWorld().getWorldDefaults(),
                                    calendar.getWorld().getServerDefaults());
        commands = (cfg == null) ? new String[0] : cfg.getStringList("commands").toArray(new String[0]);
        begins = ((DayBegins) properties.get(DayBegins.NAME)).getValue();
    }

    /**
     *  @return Name of this day
     */
    public String getName() {
        return name;
    }

    /**
     *  @return Properties of this day
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     *  @return Commands of this day
     */
    public String[] getCommands() {
        return commands;
    }

    public String getPropertiesDetails() {

      String propertiesString = new String();

      propertiesString = name + " - Difficulty: " +
        properties.getDifficulty().toString() + ", " + "Monsters: " +
        properties.getSpawnMonsters().toString() + ", " + "Animals: " +
        properties.getSpawnAnimals().toString() + ", " + "PVP: " +
        properties.getPVP().toString();

      return propertiesString;
    }

    /**
     *  @return (Relative) time when this day begins
     */
    public int begins() {
        return begins;
    }

    /**
     *  @return dayName.properties=[...], dayName.commands=[...]
     */
    @Override
    public String toString() {
        return "\n\t\t"+ name +".properties="+ properties
              +"\n\t\t"+ name +".commands="+ java.util.Arrays.toString(commands);
    }
}
