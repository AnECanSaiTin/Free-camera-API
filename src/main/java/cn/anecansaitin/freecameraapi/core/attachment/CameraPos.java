package cn.anecansaitin.freecameraapi.core.attachment;

public record CameraPos(float x, float y, float z) {
    public CameraPos() {
        this(0, 0, 0);
    }
}
