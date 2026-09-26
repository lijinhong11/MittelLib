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

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages Unix Cron tasks on the Bukkit scheduler.
 *
 * <p>Cron expressions use the standard five-field Unix format:
 * {@code minute hour day-of-month month day-of-week}.
 */
public class CronTaskManager {
    private static final long MAX_DELAY_TICKS = Integer.MAX_VALUE;

    private final JavaPlugin plugin;
    private final ZoneId zone;
    private final Map<String, ManagedCronTask> tasks = new LinkedHashMap<>();
    private final CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    public CronTaskManager(@NotNull JavaPlugin plugin) {
        this(plugin, ZoneId.systemDefault());
    }

    public CronTaskManager(@NotNull JavaPlugin plugin, @NotNull ZoneId zone) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.zone = Objects.requireNonNull(zone, "zone");
    }

    /**
     * Registers and starts a Cron task using the manager's time zone.
     *
     * @param expression Unix five-field Cron expression
     * @param action action executed on the Bukkit main thread
     * @return the registered task handle
     * @throws IllegalArgumentException if the expression is invalid
     */
    public synchronized @NotNull CronTask schedule(@NotNull String expression, @NotNull Runnable action) {
        return schedule(UUID.randomUUID().toString(), expression, action);
    }

    /**
     * Registers a named Cron task, replacing an existing task with the same ID.
     */
    public synchronized @NotNull CronTask schedule(
            @NotNull String id, @NotNull String expression, @NotNull Runnable action) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(expression, "expression");
        Objects.requireNonNull(action, "action");

        Cron cron;
        try {
            cron = parser.parse(expression);
            cron.validate();
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid Cron expression: " + expression, exception);
        }

        cancel(id);
        ManagedCronTask task = new ManagedCronTask(id, expression, cron, action);
        tasks.put(id, task);
        task.scheduleNext();
        return task;
    }

    public synchronized boolean cancel(@NotNull String id) {
        ManagedCronTask task = tasks.remove(id);
        if (task == null) {
            return false;
        }
        task.cancelInternal();
        return true;
    }

    public synchronized void cancelAll() {
        tasks.values().forEach(ManagedCronTask::cancelInternal);
        tasks.clear();
    }

    public synchronized @Nullable CronTask get(@NotNull String id) {
        return tasks.get(id);
    }

    public synchronized @NotNull Collection<CronTask> getTasks() {
        return java.util.List.copyOf(tasks.values());
    }

    public synchronized void shutdown() {
        cancelAll();
    }

    public interface CronTask {
        @NotNull String getId();

        @NotNull String getExpression();

        @NotNull ZoneId getZone();

        boolean isCancelled();

        @Nullable ZonedDateTime getNextExecution();

        default long getRemainingMillis() {
            ZonedDateTime next = getNextExecution();
            return next == null
                    ? 0
                    : Math.max(
                            0,
                            Duration.between(ZonedDateTime.now(getZone()), next).toMillis());
        }

        default @NotNull Duration getRemainingDuration() {
            return Duration.ofMillis(getRemainingMillis());
        }

        default long getRemainingTicks() {
            return (getRemainingMillis() + 49) / 50;
        }

        void cancel();
    }

    private final class ManagedCronTask implements CronTask {
        private final String id;
        private final String expression;
        private final Cron cron;
        private final Runnable action;
        private final AtomicBoolean cancelled = new AtomicBoolean();
        private volatile @Nullable ZonedDateTime nextExecution;
        private volatile @Nullable ScheduledTask scheduledTask;

        private ManagedCronTask(String id, String expression, Cron cron, Runnable action) {
            this.id = id;
            this.expression = expression;
            this.cron = cron;
            this.action = action;
        }

        @Override
        public @NotNull String getId() {
            return id;
        }

        @Override
        public @NotNull String getExpression() {
            return expression;
        }

        @Override
        public @NotNull ZoneId getZone() {
            return zone;
        }

        @Override
        public boolean isCancelled() {
            return cancelled.get();
        }

        @Override
        public @Nullable ZonedDateTime getNextExecution() {
            return nextExecution;
        }

        @Override
        public void cancel() {
            synchronized (CronTaskManager.this) {
                if (tasks.get(id) == this) {
                    tasks.remove(id);
                }
                cancelInternal();
            }
        }

        private void cancelInternal() {
            if (cancelled.compareAndSet(false, true)) {
                ScheduledTask task = scheduledTask;
                if (task != null) {
                    task.cancel();
                }
                nextExecution = null;
            }
        }

        private void scheduleNext() {
            if (cancelled.get()) {
                return;
            }

            ZonedDateTime now = ZonedDateTime.now(zone);
            Optional<ZonedDateTime> next = ExecutionTime.forCron(cron).nextExecution(now);
            if (next.isEmpty()) {
                cancel();
                return;
            }

            nextExecution = next.get();
            long delayTicks = Math.clamp(ticksUntil(now, next.get()), 1, MAX_DELAY_TICKS);
            scheduledTask = plugin.getServer()
                    .getGlobalRegionScheduler()
                    .runDelayed(
                            plugin,
                            ignored -> {
                                if (cancelled.get()) {
                                    return;
                                }
                                if (Duration.between(ZonedDateTime.now(zone), next.get())
                                                .toMillis()
                                        > 0) {
                                    scheduleNext();
                                    return;
                                }
                                try {
                                    action.run();
                                } finally {
                                    scheduleNext();
                                }
                            },
                            delayTicks);
        }

        private long ticksUntil(ZonedDateTime from, ZonedDateTime to) {
            long millis = Math.max(0, Duration.between(from, to).toMillis());
            return (millis + 49) / 50;
        }
    }
}
