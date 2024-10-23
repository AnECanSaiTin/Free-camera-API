package cn.anecansaitin.freecameraapi;

public class ModifierStates {
    //相机状态常量
    public static final int ENABLE = 1;
    public static final int POS_ENABLED = 1 << 1;
    public static final int ROT_ENABLED = 1 << 2;
    public static final int FOV_ENABLED = 1 << 3;
    public static final int FIRST_PERSON_ARM_FIXED = 1 << 4;
    public static final int GLOBAL_MODE_ENABLED = 1 << 5;
    public static final int LERP = 1 << 6;
}
