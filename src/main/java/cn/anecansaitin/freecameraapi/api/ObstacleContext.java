package cn.anecansaitin.freecameraapi.api;

import org.joml.Vector3f;

public interface ObstacleContext {
    Vector3f pos();

    ObstacleContext pos(Vector3f pos);

    ObstacleContext pos(float x, float y, float z);

    Vector3f rot();

    ObstacleContext rot(Vector3f rot);

    ObstacleContext rot(float x, float y, float z);

    float fov();

    ObstacleContext fov(float fov);

    int enabledMask();
}
