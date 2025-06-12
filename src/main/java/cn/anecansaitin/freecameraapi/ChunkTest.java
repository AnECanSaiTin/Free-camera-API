package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.core.network.CameraPoseUpdate;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.core.SectionPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChunkTest {
    public static final ChunkTest INSTANCE = new ChunkTest();
    public ClientChunkCache.Storage cameraStorage;
    public boolean inCamera;

    public void testOpen(float x, float y, float z) {
        inCamera = true;
        int vx = cameraStorage.viewCenterX;
        int vz = cameraStorage.viewCenterZ;
        int nvx = SectionPos.blockToSectionCoord(x);
        int nvz = SectionPos.blockToSectionCoord(z);

        if (vx == nvx && vz == nvz) {
            return;
        }

        cameraStorage.viewCenterX = nvx;
        cameraStorage.viewCenterZ = nvz;
        PacketDistributor.sendToServer(new CameraPoseUpdate(true, true, x, y, z, 2));
    }

    public void testClose() {
        inCamera = false;
        cameraStorage.viewCenterX = Integer.MAX_VALUE;
        cameraStorage.viewCenterZ = Integer.MAX_VALUE;
        PacketDistributor.sendToServer(new CameraPoseUpdate(false, false, 0, 0, 0, 0));
    }
}
