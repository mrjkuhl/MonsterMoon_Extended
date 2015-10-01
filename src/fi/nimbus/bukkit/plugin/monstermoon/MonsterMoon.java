package fi.nimbus.bukkit.plugin.monstermoon;

import fi.nimbus.bukkit.plugin.monstermoon.properties.DayOrder;

/**
 *  CraftBukkit plugin
 */
public class MonsterMoon extends org.bukkit.plugin.java.JavaPlugin {
    public static final int DEFAULT_DAYORDER  = DayOrder.LINEAR;
    public static final int DEFAULT_DAYBEGINS = 0000;

    public static final java.util.Random random = new java.util.Random();

    private final java.util.HashMap<String,World> worlds;

    /**
     *  Create new plugin
     */
    public MonsterMoon() {
        worlds = new java.util.HashMap<String,World>();
    }

    /**
     *  Plugin onLoad event
     */
    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    /**
     *  Plugin onEnable event
     */
    @Override
    public void onEnable() {
        registerCommand();
        initializeWorlds();
        enableWorlds();
    }

    /**
     *  Plugin onDisable event
     */
    @Override
    public void onDisable() {
        cancelTasks();
        disableWorlds();
        MonsterMoon.clearMessages();
    }

    /**
     *  Register plugin console command
     */
    private void registerCommand() {
        getCommand("monstermoon").setExecutor(new Command(this));
    }

    /**
     *  Initialize (only once) all available worlds
     */
    private void initializeWorlds() {
        for (final org.bukkit.World world : getServer().getWorlds()) {
            if (!worlds.containsKey(world.getName())) {
                worlds.put(world.getName(), new World(this, world));
            }
        }
    }

    /**
     *  Set each world according to the current day
     */
    private void enableWorlds() {
        for (World world : worlds.values()) {
            world.onEnable();
        }
    }

    /**
     *  Cancel all tasks
     */
    private void cancelTasks() {
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("Scheduled tasks cancelled");
    }

    /**
     *  Restore world properties to server defaults
     */
    private void disableWorlds() {
        for (World world : worlds.values()) {
            world.onDisable();
        }
    }

    /**
     *  Dispatch commands (as they would have been written on the console)
     */
    protected void dispatchCommands(final String world, final String section, final String[] commands) {
        String prefix = ((world == null)   ? "" : "["+ world +"] ")
                      + ((section == null) ? "" : section);

        for (final String command : commands) {
            getLogger().info(prefix +"> "+ command);
            try {
                getServer().dispatchCommand(getServer().getConsoleSender(), command);
            } catch (org.bukkit.command.CommandException e) {
                getLogger().severe(prefix +"Command '"+ command +"' failed");
            }
        }
    }

    public World getWorld(String name) {
        return worlds.get(name);
    }

    /*
     *  A simple message logging device
     */

    private static StringBuilder messages = new StringBuilder(256);

    public static void message(final String message) {
        if (hasMessages()) messages.append(", ");
        messages.append(message);
    }

    public static boolean hasMessages() {
        return (messages.length() != 0);
    }

    public static String getMessages() {
        return messages.toString();
    }

    public static void clearMessages() {
        messages.setLength(0);
    }
}
