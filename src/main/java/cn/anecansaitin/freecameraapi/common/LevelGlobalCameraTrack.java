package cn.anecansaitin.freecameraapi.common;

import java.util.HashMap;

public class LevelGlobalCameraTrack {
    private final HashMap<String, GlobalCameraTrack> trackMap = new HashMap<>();

    public void addTrack(String name, GlobalCameraTrack track) {
        trackMap.put(name, track);
    }

    public GlobalCameraTrack getTrack(String name) {
        return trackMap.get(name);
    }
}
