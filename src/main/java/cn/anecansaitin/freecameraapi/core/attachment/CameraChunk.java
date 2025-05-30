package cn.anecansaitin.freecameraapi.core.attachment;

import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.world.level.ChunkPos;

import java.util.function.Consumer;

public record CameraChunk(boolean enable, boolean update, int x, int z, int radius) implements ChunkTrackingView {
    public CameraChunk() {
        this(false, false, 0, 0, 0);
    }

    public CameraChunk updated() {
        return new CameraChunk(enable, false, x, z, radius);
    }

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
