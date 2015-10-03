package fi.nimbus.bukkit.plugin.monstermoon;

import fi.nimbus.bukkit.plugin.monstermoon.MonsterMoon;

/**
 *  Console command
 */
public class Command implements org.bukkit.command.CommandExecutor {
    final MonsterMoon plugin;
    final String permissionPrefix;

    /**
     *  Create new command
     */
    public Command(final MonsterMoon plugin) {
        this.plugin = plugin;
        permissionPrefix = plugin.getName().toLowerCase();
    }

    /**
     *  Handle command request
     */
    public boolean onCommand(final org.bukkit.command.CommandSender sender,
                             final org.bukkit.command.Command command,
                             final String label, final String[] args) {
        int argsIndex = 0;
        World world = null;

	if (argsIndex >= args.length) {

	  return false;
	}

        // Command: "debug"
        if (
	  args[0].equals("debug") &&
	  checkPermissions(sender, "debug")
	) {

            onPrintDebugInfo(sender);
            return true;
        }

	if (
	  (world = plugin.getWorld(args[argsIndex])) == null &&
	  sender instanceof org.bukkit.entity.Player
	) {

	  world = plugin.getWorld(((org.bukkit.entity.Player) sender).getWorld().getName());

 	}

	else if (world != null) {

	  argsIndex++;
	}

	else if ( !(sender instanceof org.bukkit.entity.Player) ) {

          sender.sendMessage(org.bukkit.ChatColor.RED
            +"Please specify WorldName (Note: WorldName is case sensitive)");
          return false;
	}

        // If we have command, process it
        if (argsIndex < args.length) {
          switch (args[argsIndex]) {

            case "start":
              if (checkPermissions(sender, world.getName(), "start")) {
                onStart(sender, world);

		break;
              }

            case "stop":
              if (checkPermissions(sender, world.getName(), "stop")) {
                onStop(sender, world);

		break;
              }

            case "status":
              if (checkPermissions(sender, world.getName(), "status")) {
                onStatus(sender, world);

		break;
              }

            default:
              sender.sendMessage(org.bukkit.ChatColor.RED + "Unknown command '"+ args[argsIndex]
                +"' (Note: Commands are case sensitive)");
              return false;
          }

          return true;
        }

	else {

	  return false;
	}
    }

    /**
     *  @return Are we allowed to run command?
     */
    private boolean checkPermissions(final org.bukkit.command.CommandSender sender, final String command) {
        if (!sender.hasPermission(permissionPrefix +"."+ command)) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "You do not have permissions to do that, sorry.");
            return false;
        }
        return true;
    }

    /**
     *  @return Are we allowed to run command in world?
     */
    private boolean checkPermissions(final org.bukkit.command.CommandSender sender,
                                     final String world, final String command) {
        if (!sender.hasPermission(permissionPrefix +"."+ command +".*") &&
            !sender.hasPermission(permissionPrefix +"."+ command +"."+ world)) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "You do not have permissions to do that, sorry.");
            return false;
        }
        return true;
    }

    /**
     *  Show world status
     */
    private void onStatus(final org.bukkit.command.CommandSender sender, final World world) {
        final Calendar c = world.getCalendar();
        final StringBuilder sb = new StringBuilder();
        final long time = world.getTime();
        final Day currentDay = c.getCurrentDay();

        sb.append("It is "+ time(time) +" o'clock ("+ time +")");

        if (currentDay != null) {
            sb.append(" on "+ currentDay.getName());
        }

        switch (c.getStatus()) {
            case Calendar.INACTIVE:
                sb.append(". Calendar is currently inactive.");
                break;
            case Calendar.STOPPED:
                sb.append(". Calendar is currently stopped.");
                if (currentDay.getName().indexOf("Monday") != -1) {
                    sb.append(" "+ org.bukkit.ChatColor.RED +"IT IS AN EVERLASTING MONDAY!");
                }
                break;
            default:
                final Day nextDay = c.getNextDay();
                long nextDayBegins = c.getNextDayBegins() - world.getFullTime();
                if (nextDay != null && nextDayBegins >= 0) {
                    sb.append(". "+ nextDay.getName() +" will begin in "+
                              hours(nextDayBegins) +" hours.");
                } else {
                    sb.append(". Tomorrow is a mystery!");
                }
        }

        sender.sendMessage(org.bukkit.ChatColor.GREEN.toString() + sb);
    }

    /**
     *  Start calendar in world
     */
    private void onStart(final org.bukkit.command.CommandSender sender, final World world) {
        Calendar c = world.getCalendar();
        if (c.getStatus() == Calendar.STOPPED) {
            c.start();
            sender.sendMessage(org.bukkit.ChatColor.GREEN + "Calendar in world "+ world.getName() +" is now started.");
        } else {
            sender.sendMessage(org.bukkit.ChatColor.RED + "Calendar in world "+ world.getName() +" was not stopped.");
        }
    }

    /**
     *  Stop calendar in world
     */
    private void onStop(final org.bukkit.command.CommandSender sender, final World world) {
        Calendar c = world.getCalendar();
        if (c.getStatus() == Calendar.RUNNING) {
            c.stop();
            sender.sendMessage(org.bukkit.ChatColor.GREEN + "Calendar in world "+ world.getName() +" is now stopped.");
        } else {
            sender.sendMessage(org.bukkit.ChatColor.RED + "Calendar in world "+ world.getName() +" was not running.");
        }
    }

    /**
     *  Dump full plugin and world info
     */
    private void onPrintDebugInfo(final org.bukkit.command.CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\033[1;32m=== "+ plugin.toString() +" DEBUG INFO BEGINS ===\033[0m\n");

        for (final org.bukkit.World world : plugin.getServer().getWorlds()) {
            sb.append("\n\033[1;33m--- "+ world.getName() +" Current Status ---\033[0m\n");

            sb.append("\ngetTime: "+ world.getTime());
            sb.append("\ngetFullTime: "+ world.getFullTime());
            sb.append("\ngetAllowAnimals: "+ world.getAllowAnimals());
            sb.append("\ngetAllowMonsters: "+ world.getAllowMonsters());
            sb.append("\ngetDifficulty: "+ world.getDifficulty());
            sb.append("\ngetPVP: "+ world.getPVP());

            World w = plugin.getWorld(world.getName());
            if (w == null) { sb.append("\nNo "+ plugin.getName() +" world");    continue; }
            Calendar c = w.getCalendar();
            if (c == null) { sb.append("\nNo "+ plugin.getName() +" calendar"); continue; }
            sb.append("\ncalendar: "+ new String[]{"INACTIVE", "STOPPED", "RUNNING"}[c.getStatus() + 2]);
            if (c.getStatus() == Calendar.RUNNING) {
                sb.append("\ncurrentDay: "+ c.getCurrentDay());
                sb.append("\nnextDay: "+ c.getNextDay());
                long nextDayBegins = c.getNextDayBegins();
                sb.append("\nnextDayBegins: "+
                    ((nextDayBegins >= 0) ? nextDayBegins +" (in "+ (nextDayBegins - world.getFullTime()) +")"
                                          : "never"));
            }

            sb.append("\n\n\033[1;33m--- "+ world.getName() +" Configuration ---\033[0m\n\n");
            sb.append(w);
        }

        sb.append("\n\033[1;32m=== "+ plugin.toString() +" DEBUG INFO ENDS ===\033[0m");
        plugin.getLogger().info("Printing debug info"+ sb);

        sender.sendMessage(org.bukkit.ChatColor.GREEN + "Debug information printed to console.");
    }

    /**
     *  @return Round number
     */
    private String hours(long number) {
        return String.format("%.1f", (double) number / 1000.0 );
    }

    /**
     *  @return Current time in hours (fixed from minecraft hours to more "real" hours)
     */
    private String time(long time) {
        time = time + 6000L;
        time %= 24000L;
        return String.format("%.1f", (double) time / 1000.0 );
    }
}
