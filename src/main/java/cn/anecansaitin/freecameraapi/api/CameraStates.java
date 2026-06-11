package cn.anecansaitin.freecameraapi.api;

public enum CameraStates {
    ENABLE(1),
    POS(1 << 1),
    ROT(1 << 2),
    FOV(1 << 3),
    OBSTACLE(1 << 4),
    GLOBAL_MODE(1 << 5);

    public final int code;

    CameraStates(int code) {
        this.code = code;
    }
}
