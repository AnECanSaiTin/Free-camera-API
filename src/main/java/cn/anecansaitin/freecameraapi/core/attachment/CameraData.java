package cn.anecansaitin.freecameraapi.core.attachment;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.world.level.ChunkPos;

import java.util.function.Consumer;

public class CameraData {
    public boolean enable;
    public boolean update;
    public float x;
    public float y;
    public float z;
    public CameraChunkTrackingView currentView = new CameraChunkTrackingView(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
    public CameraChunkTrackingView oldView = currentView;

    public boolean update(boolean enable, boolean update, float x, float y, float z, int radius) {
        this.enable = enable;

        if (!enable) {
            return false;
        }

        this.update = update;
        this.x = x;
        this.y = y;
        this.z = z;

        if (!update) {
            return false;
        }

        int newX = SectionPos.blockToSectionCoord(x);
        int newZ = SectionPos.blockToSectionCoord(z);

        if (currentView.x - newX + currentView.z - newZ == 0) {
            return false;
        }

        updateView(newX, newZ, radius);
        return true;
    }

    public void updateView(int x, int z, int radius) {
        oldView = currentView;
        currentView = new CameraChunkTrackingView(x, z, radius);
    }

    public record CameraChunkTrackingView(int x, int z, int radius) implements ChunkTrackingView {
        @Override
        public boolean contains(int x, int z, boolean includeOuterChunksAdjacentToViewBorder) {
            return ChunkTrackingView.isWithinDistance(x, z, radius, x, z, includeOuterChunksAdjacentToViewBorder);
        }

        @Override
        public void forEach(Consumer<ChunkPos> action) {
            for (int i = this.minX(); i <= this.maxX(); i++) {
                for (int j = this.minZ(); j <= this.maxZ(); j++) {
                    if (this.contains(i, j)) {
                        action.accept(new ChunkPos(i, j));
                    }
                }
            }
        }

        private int minX() {
            return x - radius - 1;
        }

        private int minZ() {
            return z - radius - 1;
        }

        private int maxX() {
            return x + radius + 1;
        }

        private int maxZ() {
            return z + radius + 1;
        }
    }
}
