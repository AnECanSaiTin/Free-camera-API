package cn.anecansaitin.freecameraapi.api;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@SuppressWarnings("unused")
public class ModifierStates {
    //相机状态常量
    public static final int ENABLE;
    public static final int POS;
    public static final int ROT;
    public static final int FOV;
    public static final int OBSTACLE;
    public static final int GLOBAL_MODE;
    public static final Object2IntOpenHashMap<String> NAME_STATE = new Object2IntOpenHashMap<>();
    public static final Int2ObjectOpenHashMap<String> STATE_NAMES = new Int2ObjectOpenHashMap<>();

    private static int COUNTER = 1;

    static {
        ENABLE = 1;
        NAME_STATE.put("enable", ENABLE);
        STATE_NAMES.put(ENABLE, "enable");
        POS = nextState("pos");
        ROT = nextState("rot");
        FOV = nextState("fov");
        OBSTACLE = nextState("obstacle");
        GLOBAL_MODE = nextState("global_mode");
    }

    public static int nextState(String name) {
        if (NAME_STATE.containsKey(name)) {
            throw new IllegalArgumentException("State name already exists: " + name);
        }

        COUNTER <<= 1;
        NAME_STATE.put(name, COUNTER);
        STATE_NAMES.put(COUNTER, name);
        return COUNTER;
    }

    public static int getState(String name) {
        return NAME_STATE.getInt(name);
    }

    public static String getName(int state) {
        return STATE_NAMES.get(state);
    }
}
