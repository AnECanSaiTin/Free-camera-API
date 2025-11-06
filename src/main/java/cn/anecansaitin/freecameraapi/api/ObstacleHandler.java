package cn.anecansaitin.freecameraapi.api;

import org.joml.Vector3f;

@FunctionalInterface
public interface ObstacleHandler {
    ObstacleHandler NULL = (pos, rot, fov) -> {
    };

    /// Custom obstacle avoidance behavior
    /// @param pos Position before collision avoidance
    /// @param rot Rotation before collision avoidance
    /// @param fov FOV before collision avoidance
    /// @return Returns COLLIDE or NO_COLLIDE if obstacle avoidance has been handled, PASS if not handled, default obstacle avoidance will be executed
    ///
    default ObstacleResult obstacleAvoid(Vector3f pos, Vector3f rot, float[] fov) {
        return ObstacleResult.PASS;
    }

    /// @param pos Position after collision avoidance
    /// @param rot Rotation after collision avoidance
    /// @param fov FOV after collision avoidance
    void onCollision(Vector3f pos, Vector3f rot, float fov);

    enum ObstacleResult{
        PASS,
        COLLIDE,
        NO_COLLIDE
    }
}
