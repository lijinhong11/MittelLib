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
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Handle for a task scheduled by {@link SimpleTaskManager}. */
public final class MittelTask {
    private final SimpleTaskManager manager;
    private final String id;
    private final Runnable action;

    @Getter
    private final boolean repeating;

    private final AtomicBoolean cancelled = new AtomicBoolean();
    private volatile @Nullable ScheduledTask handle;
    private volatile long nextExecutionMillis;
    private volatile long periodMillis;

    MittelTask(SimpleTaskManager manager, String id, Runnable action, boolean repeating) {
        this.manager = Objects.requireNonNull(manager, "manager");
        this.id = Objects.requireNonNull(id, "id");
        this.action = Objects.requireNonNull(action, "action");
        this.repeating = repeating;
    }

    void bind(ScheduledTask handle, long delayTicks, long periodTicks) {
        this.handle = handle;
        this.periodMillis = periodTicks > 0 ? periodTicks * 50L : 0;
        this.nextExecutionMillis = System.currentTimeMillis() + delayTicks * 50L;
        if (cancelled.get()) {
            handle.cancel();
        }
    }

    void run() {
        if (cancelled.get()) {
            return;
        }
        try {
            action.run();
        } finally {
            if (!repeating) {
                cancel();
            } else {
                nextExecutionMillis = System.currentTimeMillis() + periodMillis;
            }
        }
    }

    public @NotNull String getId() {
        return id;
    }

    public boolean isCancelled() {
        if (cancelled.get()) {
            return true;
        }

        if (handle == null) {
            return true;
        }

        return handle.isCancelled();
    }

    public long getNextExecutionTime() {
        return isCancelled() ? -1 : nextExecutionMillis;
    }

    public long getRemainingMillis() {
        long next = getNextExecutionTime();
        return next < 0 ? -1 : Math.max(0, next - System.currentTimeMillis());
    }

    public long getRemainingTicks() {
        long remaining = getRemainingMillis();
        return remaining < 0 ? -1 : (remaining + 49) / 50;
    }

    public @NotNull Duration getRemainingDuration() {
        return Duration.ofMillis(Math.max(0, getRemainingMillis()));
    }

    public void cancel() {
        if (cancelled.compareAndSet(false, true)) {
            ScheduledTask current = handle;
            if (current != null) {
                current.cancel();
            }
            nextExecutionMillis = -1;
            manager.remove(id, this);
        }
    }
}
