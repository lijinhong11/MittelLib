/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.utils.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Owns Bukkit tasks and provides lifecycle and countdown management.
 *
 * <p>Tasks are identified by a caller-provided ID. Starting a task with an
 * existing ID replaces and cancels the previous task.
 */
public final class SimpleTaskManager {
    private final JavaPlugin plugin;
    private final Map<String, MittelTask> tasks = new ConcurrentHashMap<>();
    private volatile boolean closing;

    public SimpleTaskManager(@NotNull JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        closing = false;
    }

    /**
     * Schedules a one-shot task.
     *
     * @param id task ID
     * @param delayTicks delay before execution
     * @param action action executed on the Bukkit scheduler
     */
    public @NotNull MittelTask runLater(@NotNull String id, long delayTicks, @NotNull Runnable action) {
        return start(id, delayTicks, -1, action);
    }

    /** Schedules a repeating task with an initial delay and period in ticks. */
    public @NotNull MittelTask runRepeating(
            @NotNull String id, long delayTicks, long periodTicks, @NotNull Runnable action) {
        if (periodTicks < 1) {
            throw new IllegalArgumentException("periodTicks must be greater than zero");
        }
        return start(id, delayTicks, periodTicks, action);
    }

    /** Runs an action immediately on the Bukkit main scheduler. */
    public boolean runSync(@NotNull Runnable action) {
        if (closing) {
            return false;
        }
        plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> action.run());
        return true;
    }

    /** Runs an action after a delay on the Bukkit main scheduler. */
    public boolean runSyncDelayed(long delayTicks, @NotNull Runnable action) {
        if (closing) {
            return false;
        }
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> action.run(), Math.max(1, delayTicks));
        return true;
    }

    /**
     * Runs an action on the scheduler associated with a player.
     * This uses the player scheduler when available on the server implementation.
     */
    public boolean runSync(@NotNull Player player, @NotNull Runnable action) {
        if (closing) {
            return false;
        }
        player.getScheduler().run(plugin, task -> action.run(), null);
        return true;
    }

    /**
     * Runs an action on the region containing a location when Folia is present;
     * otherwise falls back to the Bukkit scheduler.
     */
    public boolean runSync(@NotNull Location location, @NotNull Runnable action) {
        if (closing || location.getWorld() == null) {
            return false;
        }
        if (isFolia()) {
            plugin.getServer().getRegionScheduler().run(plugin, location, task -> action.run());
        } else {
            plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> action.run());
        }
        return true;
    }

    public boolean cancel(@NotNull String id) {
        MittelTask task = tasks.remove(id);
        if (task == null) {
            return false;
        }
        task.cancel();
        return true;
    }

    public boolean isRunning(@NotNull String id) {
        MittelTask task = tasks.get(id);
        return task != null && !task.isCancelled();
    }

    public @Nullable MittelTask get(@NotNull String id) {
        return tasks.get(id);
    }

    public @NotNull Collection<MittelTask> getTasks() {
        return List.copyOf(tasks.values());
    }

    void remove(String id, MittelTask task) {
        tasks.remove(id, task);
    }

    /** Cancels all tasks and prevents new tasks from being scheduled. */
    public void close() {
        closing = true;
        tasks.values().forEach(MittelTask::cancel);
        tasks.clear();
    }

    /** Cancels all tasks and allows scheduling again. */
    public void reload() {
        close();
        closing = false;
    }

    private @NotNull MittelTask start(String id, long delayTicks, long periodTicks, Runnable action) {
        if (closing) {
            throw new IllegalStateException("TaskMaker is closed");
        }
        if (delayTicks < 0) {
            throw new IllegalArgumentException("delayTicks cannot be negative");
        }

        cancel(id);
        MittelTask task = new MittelTask(this, id, action, periodTicks > 0);
        tasks.put(id, task);
        ScheduledTask handle = periodTicks > 0
                ? plugin.getServer()
                        .getGlobalRegionScheduler()
                        .runAtFixedRate(plugin, ignored -> task.run(), delayTicks, periodTicks)
                : plugin.getServer()
                        .getGlobalRegionScheduler()
                        .runDelayed(plugin, ignored -> task.run(), Math.max(1, delayTicks));
        task.bind(handle, delayTicks, periodTicks);
        return task;
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
