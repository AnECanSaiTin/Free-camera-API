package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.api.ObstacleContext;
import org.joml.Vector3f;

public class Context implements ObstacleContext {
    private final Vector3f pos = new Vector3f();
    private final Vector3f rot = new Vector3f();
    private float fov;
    private int enabledMask;

    @Override
    public Vector3f pos() {
        return pos;
    }

    @Override
    public Context pos(Vector3f pos) {
        this.pos.set(pos);
        return this;
    }

    @Override
    public Context pos(float x, float y, float z) {
        pos.set(x, y, z);
        return this;
    }

    @Override
    public Vector3f rot() {
        return rot;
    }

    @Override
    public Context rot(Vector3f rot) {
        this.rot.set(rot);
        return this;
    }

    @Override
    public Context rot(float x, float y, float z) {
        rot.set(x, y, z);
        return this;
    }

    @Override
    public float fov() {
        return fov;
    }

    @Override
    public Context fov(float fov) {
        this.fov = fov;
        return this;
    }

    @Override
    public int enabledMask() {
        return enabledMask;
    }

    public Context enabledMask(int enabledMask) {
        this.enabledMask = enabledMask;
        return this;
    }
}
