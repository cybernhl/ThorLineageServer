package me.aodamiao.pool.timer;

import java.util.Timer;

public class TimerPool {

    private final Timer[] timers;
    private final int size;
    private int index;

    public TimerPool(final int size) {
        this.timers = new Timer[size];
        for (int i = 0; i < size; i++) {
            this.timers[i] = new Timer();
        }
        this.size = size;
        this.index = 0;
    }

    public synchronized Timer getTimer() {
        if (this.size <= this.index) {
            this.index = 0;
        }
        return this.timers[this.index++];
    }
}
