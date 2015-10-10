package fi.nimbus.bukkit.plugin.monstermoon;

import fi.nimbus.bukkit.plugin.monstermoon.properties.DayOrder;

/**
 *  List of precompiled days
 */
public class Calendar extends org.bukkit.scheduler.BukkitRunnable {
    public static final int INACTIVE = -2;
    public static final int STOPPED  = -1;
    public static final int RUNNING  =  0;

    protected final World   world;
    protected final Day[]   days;
    private final DayOrder  dayOrder;
    private final int       calendarLength;

    private int             currentDay;
    private int             nextDay;

    private int             taskID;
    private long            taskScheduledAt;

    /**
     *  Compile all days for the requested world
     */
    public Calendar(final World world) {
        this.world = world;
        taskID = -1;
        calendarLength =
          world.getPlugin().getConfig().getInt(world.getName() +
          ".calendar-length");

        org.bukkit.configuration.ConfigurationSection cfg =
          world.getPlugin().getConfig().getConfigurationSection(world.getName() + ".calendar");


        // No calendar days == inactive calendar
        java.util.Set<String> dayNames;
        if (cfg == null || (dayNames = cfg.getKeys(false)) == null) {
            days = new Day[0];
            dayOrder = new DayOrder(null);
            nextDay = INACTIVE;
            return;
        }

        // Add days to the array
        java.util.List<Day> days = new java.util.ArrayList<Day>();
        for (String name : dayNames) {
            days.add(new Day(this, name, cfg.getConfigurationSection(name)));
        }
        this.days = days.toArray(new Day[0]);

        // Order of days
        dayOrder = (DayOrder) world.getWorldDefaults().get(DayOrder.NAME);

        // Calendar is ready to be run!
        currentDay = -1;
        nextDay = STOPPED;
    }

    /**
     *  @return Bound day index
     */
    private int wrapIndex(int day) {
        return ((day % days.length) + days.length) % days.length;
    }

    /**
     *  @return Status of the calendar: INACTIVE, STOPPED, RUNNING
     */
    public int getStatus() {
        return (nextDay < 0) ? nextDay : RUNNING;
    }

    /**
     *  Start calendar execution
     */
    public void start() {
        if (getStatus() != STOPPED) return;
        nextDay = (dayOrder.getValue() == DayOrder.RANDOM) ? MonsterMoon.random.nextInt(days.length)
                                                           : wrapIndex(currentDay + 1);
        run();
    }

    /**
     *  Stop calendar execution
     */
    public void stop() {
        if (getStatus() != RUNNING) return;

        nextDay = STOPPED;

        if (taskID != -1) {
            final org.bukkit.scheduler.BukkitScheduler scheduler =
                    world.getPlugin().getServer().getScheduler();

            scheduler.cancelTask(taskID);
            taskID = -1;
        }
    }

    /**
     *  Run daily routine
     */
    public void run() {
        if (getStatus() != RUNNING) return;

        final org.bukkit.scheduler.BukkitScheduler scheduler =
                world.getPlugin().getServer().getScheduler();

        final long fullTime = world.getFullTime();
        final long time = fullTime % 24000L;
        long nextDayBegins;

        if (dayOrder.getValue() == DayOrder.UNIVERSAL) {
            /*
             *  Universal Order uses only world time to determine
             *  the current and next day
             */
            currentDay = (int) (fullTime / 24000L) % days.length;
            nextDay = wrapIndex(currentDay + 1);
            nextDayBegins = 0;

            if (time < days[currentDay].begins()) {
                currentDay = wrapIndex(currentDay - 1);
                nextDay = wrapIndex(nextDay - 1);
                nextDayBegins = -24000;
            } else if (time >= 24000 + days[nextDay].begins()) {
                currentDay = wrapIndex(currentDay + 1);
                nextDay = wrapIndex(nextDay + 1);
                nextDayBegins = +24000;
            }

            nextDayBegins += 24000 + days[nextDay].begins() - time;
            if (nextDayBegins < 100) {
                // Do not bother with changes that last less than 100 ticks
                scheduler.scheduleSyncDelayedTask(world.getPlugin(), this, 100);
                return;
            }

            nextDay = wrapIndex(currentDay + 1);

        } else {
            /*
             *  Linear and Random Order
             */
            currentDay = nextDay;
            nextDay = (dayOrder.getValue() == DayOrder.LINEAR) ? wrapIndex(currentDay + 1)
                                                               : MonsterMoon.random.nextInt(days.length);

            nextDayBegins = 24000 + days[nextDay].begins() - time;
            if (nextDayBegins <= 100) nextDayBegins += 24000;
        }

        // Apply current dayly properties and print possible changes
        MonsterMoon.message(days[currentDay].getName() +" has begun");
        days[currentDay].getProperties().applyToWorld(world.getWorld());
        world.getPlugin().getLogger().info("["+ world.getName() +"] "+ MonsterMoon.getMessages());
        MonsterMoon.clearMessages();

        // Execute commands
        world.getPlugin().dispatchCommands(world.getName(), days[currentDay].getName(),
                                           days[currentDay].getCommands());
        // Schedule next day
        taskID = scheduler.scheduleSyncDelayedTask(world.getPlugin(), this, nextDayBegins);
        taskScheduledAt = fullTime + nextDayBegins;
    }

    /**
     *  @return The world in which this list belongs
     */
    public World getWorld() {
        return world;
    }

    /**
     *  @return Day order for this calendar
     */
    public DayOrder getDayOrder() {
        return dayOrder;
    }

    /**
     *  @return The current day, null if none
     */
    public Day getCurrentDay() {
        try {
            return days[currentDay];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     *  @return The next day, null if none
     */
    public Day getNextDay() {
        try {
            return days[nextDay];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     *  @return Full time when the next day will begin (-1 if not queued)
     */
    public long getNextDayBegins() {
        if (taskID == -1) return -1;

        final org.bukkit.scheduler.BukkitScheduler scheduler =
                world.getPlugin().getServer().getScheduler();

        if (scheduler.isQueued(taskID)) {
            return taskScheduledAt;
        } else {
            return -1;
        }
    }

    public Day[] getDays() {

      return days;
    }
    /**
     *  @return Number of days in this list
     */
    public int size() {
        return days.length;
    }

    /**
     *  Day names to string
     *  @return dayName, dayName, ...
     */
    public String dayNamesToString() {
        StringBuilder sb = new StringBuilder(256);
        for (Day d : days) {
            if (sb.length() != 0) sb.append(", ");
            sb.append(d.getName());
        }
        return sb.toString();
    }

    /**
     *  Calendar to string
     *  @return [status=...]
     */
    @Override
    public String toString() {
        return "[status="+           new String[]{"inactive", "stopped", "running"}[getStatus() + 2]
              +", taskID="+          taskID
              +", taskScheduledAt="+ taskScheduledAt
              +", dayOrder="+        dayOrder
              +", currentDay="+      currentDay
              +", nextDay="+         nextDay
              +", days="+            java.util.Arrays.toString(days)
              +"]";
    }
}


