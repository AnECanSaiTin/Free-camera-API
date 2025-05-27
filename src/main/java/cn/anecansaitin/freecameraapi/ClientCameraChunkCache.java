package cn.anecansaitin.freecameraapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;

public class ClientCameraChunkCache {
    private boolean enable;
    private final int[] range = new int[6];

    public ClientCameraChunkCache() {

    }

    private void dropAll() {
        ClientChunkCache cache = chunkSource();

    }

    private ClientChunkCache chunkSource() {
        return Minecraft.getInstance().level.getChunkSource();
    }
}
