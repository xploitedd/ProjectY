package me.xploited.projecty.task;

import me.xploited.projecty.NPCLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface ScheduledRun<T extends ScheduledRun<T>> {

    default void runEvery(Consumer<T> consumer, long time, TimeUnit timeUnit) {
        runEvery(consumer, 0, time, timeUnit);
    }

    @SuppressWarnings("unchecked")
    default void runEvery(Consumer<T> consumer, long delay, long time, TimeUnit timeUnit) {
        Plugin plugin = NPCLib.getPlugin();
        T obj = (T) this;

        long delayTicks = timeUnit.toSeconds(delay) * 20;
        long timeTicks = timeUnit.toSeconds(time) * 20;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> consumer.accept(obj), delayTicks, timeTicks);
    }

}
