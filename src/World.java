package fi.nimbus.bukkit.plugin.monstermoon;

import fi.nimbus.bukkit.plugin.monstermoon.properties.DayOrder;

/**
 *  World level configuration container
 */
public class World {
    private final org.bukkit.World world;

    private final String      name;
    private final MonsterMoon plugin;
    private final Properties  serverDefaults;
    private final Properties  worldDefaults;
    private final Calendar    calendar;
    private final String[]    commandsOnEnable;
    private final String[]    commandsOnDisable;

    /**
     *  Create new world level configuration from Bukkit World object,
     *  log status and possible syntax errors
     */
    public World(final MonsterMoon plugin, final org.bukkit.World world) {
        this.world = world;
        this.plugin = plugin;
        name = world.getName();

        org.bukkit.configuration.ConfigurationSection cfg = plugin.getConfig().getConfigurationSection(name);

        // Properties and calendar days
        serverDefaults = new Properties(world);
        worldDefaults  = new Properties(cfg);
        calendar = new Calendar(this);

        // Commands on plugin enable
        String[] list;
        list = (cfg == null) ? new String[0] : cfg.getStringList("commands-on-enable").toArray(new String[0]);
        list = (cfg != null && list.length == 0) ? cfg.getStringList("commands").toArray(new String[0]) : list;
        this.commandsOnEnable = list;

        // Commands on plugin disable
        list = (cfg == null) ? new String[0] : cfg.getStringList("commands-on-disable").toArray(new String[0]);
        this.commandsOnDisable = list;

        // If we have errors, log them first
        displayMessages(true);

        // Log world status
        if (calendar.getStatus() == Calendar.INACTIVE) {
            MonsterMoon.message("No days in the calendar");
        } else {
            MonsterMoon.message("Day order is "+ calendar.getDayOrder());
            MonsterMoon.message(calendar.size() +" day"+ (calendar.size() > 1 ? "s" : "")
                                +" in the calendar: "+ calendar.dayNamesToString());
        }
        displayMessages();
    }

    /**
     *  @return Name of this world
     */
    public String getName() {
        return name;
    }

    /**
     *  @return Bukkit world object
     */
    public org.bukkit.World getWorld() {
        return world;
    }

    /**
     *  @return Calendar for this world
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     *  @return Plugin interface
     */
    public MonsterMoon getPlugin() {
        return plugin;
    }

    /**
     *  @return Default properties for server
     */
    public Properties getServerDefaults() {
        return serverDefaults;
    }

    /**
     *  @return Default properties for this world
     */
    public Properties getWorldDefaults() {
        return worldDefaults;
    }

    /**
     *  Set world properties to world defaults
     */
    public void applyWorldDefaults() {
        MonsterMoon.message("Setting world defaults");
        new Properties(worldDefaults, worldDefaults, serverDefaults).applyToWorld(world);
        displayMessages();
    }

    /**
     *  Restore world properties to server defaults
     */
    public void applyServerDefaults() {
        MonsterMoon.message("Restoring server defaults");
        serverDefaults.applyToWorld(world);
        displayMessages();
    }

    /**
     *  Startup world
     */
    public void onEnable() {
        getPlugin().dispatchCommands(name, "commands-on-enable", commandsOnEnable);
        if (calendar.getStatus() == Calendar.INACTIVE) {
            applyWorldDefaults();
        } else {
            calendar.start();
        }
        displayMessages();
    }

    /**
     *  Shutdown world
     */
    public void onDisable() {
        calendar.stop();
        applyServerDefaults();
        getPlugin().dispatchCommands(name, "commands-on-disable", commandsOnDisable);
    }

    /**
     *  Get current (absolute) time in this world
     */
    public long getFullTime() {
        return world.getFullTime();
    }

    /**
     *  Get current (relative) time in this world
     */
    public long getTime() {
        return world.getTime();
    }

    /**
     *  Display possible messages (and clear message buffer)
     */
    private void displayMessages() {
        displayMessages(false);
    }

    /**
     *  Display possible messages (and clear message buffer)
     */
    private void displayMessages(boolean severe) {
        if (MonsterMoon.hasMessages()) {
            if (severe) {
                plugin.getLogger().severe("["+ name +"] "+ MonsterMoon.getMessages());
            } else {
                plugin.getLogger().info("["+ name +"] "+ MonsterMoon.getMessages());
            }
            MonsterMoon.clearMessages();
        }
    }

    /**
     *  World to string
     *  @return World.serverDefaults=[...], World.worldDefaults=[...],
     *          World.commandsOnEnable=[...], World.commandsOnDisable=[...], World.calendar=[...]
     */
    @Override
    public String toString() {
        return "\t"+ name +".serverDefaults="+    serverDefaults                               +"\n\t"+
                     name +".worldDefaults="+     worldDefaults                                +"\n\t"+
                     name +".commandsOnEnable="+  java.util.Arrays.toString(commandsOnEnable)  +"\n\t"+
                     name +".commandsOnDisable="+ java.util.Arrays.toString(commandsOnDisable) +"\n\t"+
                     name +".calendar="+          calendar                                     +"\n";
    }
}
