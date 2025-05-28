package cn.anecansaitin.freecameraapi;

public interface IClientChunkCacheExtension {
    default boolean chunkInRange(int x, int z) {
        return false;
    }
}
