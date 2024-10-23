package cn.anecansaitin.zoom.client.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class MouseMoveEvent extends Event implements ICancellableEvent {
    private final double x;
    private final double y;

    public MouseMoveEvent(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}