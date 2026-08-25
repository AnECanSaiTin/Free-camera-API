package cn.anecansaitin.freecameraapi.api;

public enum CameraStates {
    ENABLE(1),

    /// 位置分量（独立开关）。
    POS_X(1 << 6),
    POS_Y(1 << 7),
    POS_Z(1 << 8),
    /// 旋转分量（独立开关）。
    ROT_X(1 << 9),
    ROT_Y(1 << 10),
    ROT_Z(1 << 11),

    /// 同时启用 X+Y+Z 三个位置分量。组合码，setState 直接使用等价于 enablePos()。
    POS(POS_X.code | POS_Y.code | POS_Z.code),
    /// 同时启用 X+Y+Z 三个旋转分量。组合码，setState 直接使用等价于 enableRotation()。
    ROT(ROT_X.code | ROT_Y.code | ROT_Z.code),

    FOV(1 << 3),
    OBSTACLE(1 << 4),
    GLOBAL_MODE(1 << 5);

    public final int code;

    CameraStates(int code) {
        this.code = code;
    }
}
