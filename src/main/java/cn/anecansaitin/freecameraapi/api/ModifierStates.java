package cn.anecansaitin.freecameraapi.api;

public class ModifierStates {
    //相机状态常量
    public static final int ENABLE;
    public static final int POS;
    public static final int ROT;
    public static final int FOV;
    public static final int OBSTACLE;
    public static final int GLOBAL_MODE;

    private static int STATE = 1;

    static {
        ENABLE = 1;
        POS = nextState();
        ROT = nextState();
        FOV = nextState();
        OBSTACLE = nextState();
        GLOBAL_MODE = nextState();
    }

    public static int nextState() {
        return STATE <<= 1;
    }
}
