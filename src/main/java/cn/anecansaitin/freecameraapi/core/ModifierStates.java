package cn.anecansaitin.freecameraapi.core;

public class ModifierStates {
    //相机状态常量
    public static final int ENABLE = 1;
    public static final int POS = 1 << 1;
    public static final int ROT = 1 << 2;
    public static final int FOV = 1 << 3;
    public static final int OBSTACLE = 1 << 4;
    public static final int GLOBAL_MODE = 1 << 5;
    public static final int LERP = 1 << 6;
    public static final int CHUNK_LOADER = 1 << 7;
}
