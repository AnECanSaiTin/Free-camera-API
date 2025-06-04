package cn.anecansaitin.freecameraapi;

import net.minecraft.client.multiplayer.ClientChunkCache;

public class ChunkTest {
    public static final ChunkTest INSTANCE = new ChunkTest();
    private ClientChunkCache.Storage cameraStorage;
    private boolean inCamera;

    public ClientChunkCache.Storage getCameraStorage() {
        return cameraStorage;
    }

    public void setCameraStorage(ClientChunkCache.Storage cameraStorage) {
        this.cameraStorage = cameraStorage;
    }

    public boolean isInCamera() {
        return inCamera;
    }

    public void setInCamera(boolean inCamera) {
        this.inCamera = inCamera;
    }
}
