package cn.anecansaitin.freecameraapi.api;

import java.util.function.Supplier;

public class CameraDataType<T extends CameraData> {
    private final Class<T> type;
    private final Supplier<T> create;

    public CameraDataType(Class<T> type, Supplier<T> create) {
        this.type = type;
        this.create = create;
    }

    public T create() {
        return create.get();
    }

    public Class<T> type() {
        return type;
    }
}
