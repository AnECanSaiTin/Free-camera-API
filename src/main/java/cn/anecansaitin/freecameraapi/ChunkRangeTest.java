package cn.anecansaitin.freecameraapi;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class ChunkRangeTest {
    private static int range = 5;

    public static boolean test(int x, int z) {
        BlockPos pos = Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition();
        int
                cx = pos.getX() >> 4,
                cz = pos.getZ() >> 4;

//        return Math.abs(cx - x) <= range && Math.abs(cz - z) <= range;
        return false;
    }
}
