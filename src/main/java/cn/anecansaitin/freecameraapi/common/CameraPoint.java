package cn.anecansaitin.freecameraapi.common;

import org.joml.Vector3f;

/// 相机点位
public class CameraPoint {
    // 基础信息
    private final Vector3f position;
    private final Vector3f rotation;
    private float fov;

    public CameraPoint(Vector3f position, Vector3f rotation, float fov) {
        this.position = position;
        this.rotation = rotation;
        this.fov = fov;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }
}
