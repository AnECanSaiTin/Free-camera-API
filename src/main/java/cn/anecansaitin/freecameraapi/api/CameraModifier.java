package cn.anecansaitin.freecameraapi.api;

import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashMap;

/// Camera modifier interface, used to define various camera modification operations.
///
/// Must have a constructor with an Identifier parameter.
/// Example :{@link cn.anecansaitin.freecameraapi.core.Modifier}
@SuppressWarnings("unused")
public interface CameraModifier {
    /// Enables position modification.
    CameraModifier enablePos();

    /// Disables position modification.
    CameraModifier disablePos();

    /// Sets the camera position.
    ///
    /// @param x X-axis coordinate.
    /// @param y Y-axis coordinate.
    /// @param z Z-axis coordinate.
    CameraModifier setPos(float x, float y, float z);

    /// Sets the camera position.
    ///
    /// @param pos Position vector.
    CameraModifier setPos(Vector3f pos);

    /// Adds a position offset to the camera.
    ///
    /// @param x X-axis offset.
    /// @param y Y-axis offset.
    /// @param z Z-axis offset.
    CameraModifier addPos(float x, float y, float z);

    /// Adds a position offset to the camera.
    ///
    /// @param pos Offset vector.
    CameraModifier addPos(Vector3f pos);

    /// Enables rotation modification.
    CameraModifier enableRotation();

    /// Disables rotation modification.
    CameraModifier disableRotation();

    /// Sets the rotation angles in YXZ order.
    ///
    /// @param xRot X-axis rotation angle.
    /// @param yRot Y-axis rotation angle.
    /// @param zRot Z-axis rotation angle.
    CameraModifier setRotationYXZ(float xRot, float yRot, float zRot);

    /// Sets the rotation angles in YXZ order.
    ///
    /// @param rot Rotation vector.
    CameraModifier setRotationYXZ(Vector3f rot);

    /// Sets the rotation angles in ZYX order.
    ///
    /// @param xRot X-axis rotation angle.
    /// @param yRot Y-axis rotation angle.
    /// @param zRot Z-axis rotation angle.
    CameraModifier setRotationZYX(float xRot, float yRot, float zRot);

    /// Sets the rotation angles in ZYX order.
    ///
    /// @param rot Rotation vector.
    CameraModifier setRotationZYX(Vector3f rot);

    /// Rotates the camera in YXZ order.
    ///
    /// @param xRot X-axis rotation angle.
    /// @param yRot Y-axis rotation angle.
    /// @param zRot Z-axis rotation angle.
    CameraModifier rotateYXZ(float xRot, float yRot, float zRot);

    /// Enables field-of-view (FOV) modification.
    CameraModifier enableFov();

    /// Disables field-of-view (FOV) modification.
    CameraModifier disableFov();

    /// Sets the field-of-view (FOV) angle.
    ///
    /// @param fov Field-of-view angle.
    CameraModifier setFov(float fov);

    /// Moves the camera position.
    ///
    /// @param x X-axis movement.
    /// @param y Y-axis movement.
    /// @param z Z-axis movement.
    CameraModifier move(float x, float y, float z);

    /// Aims the camera at a specified point.
    ///
    /// @param x X-axis coordinate of the target point.
    /// @param y Y-axis coordinate of the target point.
    /// @param z Z-axis coordinate of the target point.
    CameraModifier aimAt(float x, float y, float z);

    /// Gets the current camera position (read-only view).
    ///
    /// 调用方不得修改返回值；如需写入使用 [setPos] 或先复制为 [Vector3f]。
    ///
    /// @return Returns the camera position vector (read-only).
    Vector3fc getPos();

    /// Gets the current camera rotation angles (read-only view).
    ///
    /// 调用方不得修改返回值；如需写入使用 [setRotationYXZ] / [setRotationZYX] 或先复制为 [Vector3f]。
    ///
    /// @return Returns the rotation vector (read-only).
    Vector3fc getRot();

    /// Gets the current field-of-view (FOV) angle.
    ///
    /// @return Returns the FOV angle.
    float getFov();

    /// Enables the modifier.
    CameraModifier enable();

    /// Disables the modifier.
    CameraModifier disable();

    /// Disables all states.
    CameraModifier disableAll();

    /// Enables global mode.
    CameraModifier enableGlobalMode();

    /// Disables global mode.
    CameraModifier disableGlobalMode();

    /// Enables default obstacle avoidance.
    CameraModifier enableObstacle();

    /// Enables default obstacle avoidance.
    ///@param handler When the obstacle is hit, this handler will be called.
    CameraModifier enableObstacle(ObstacleHandler handler);

    ObstacleHandler getObstacleHandler();

    /// Disables default obstacle avoidance.
    CameraModifier disableObstacle();

    /// Reverts to vanilla camera settings.
    CameraModifier setToVanilla();

    /// Sets position, rotation, and FOV to zero.
    CameraModifier clean();

    /// Resets all parameters and states.
    ///
    /// Disables all states. Sets position, rotation, and FOV to zero.
    CameraModifier reset();

    /// Sets the modifier state via an integer bitmask.
    ///
    /// Example:
    /// <pre>
    ///    modifier.setState(ModifierStates.ENABLE | ModifierStates.POS_ENABLED);
    /// </pre>
    /// This is equivalent to:
    /// <pre>
    ///    modifier.enable().enablePos()
    /// </pre>
    ///
    /// @param state State bitmask.
    CameraModifier setState(int state);

    /// Gets the current state bitmask.
    ///
    /// @return Returns the state integer value.
    int getState();

    /// Checks if any bits in the given state are enabled.
    ///
    /// @param state State bitmask.
    /// @return Returns true if at least one bit matches.
    default boolean isStateEnabledOr(int state) {
        return (getState() & state) != 0;
    }

    /// Determines whether the modifier is active.
    ///
    /// @return Returns true if the modifier is active.
    default boolean isActive() {
        int state = getState();
        return state >= 1 && isStateEnabledOr(CameraStates.ENABLE.code) && isStateEnabledOr(CameraStates.POS.code | CameraStates.ROT.code | CameraStates.FOV.code);
    }

    /// Gets the unique identifier of the modifier.
    ///
    /// @return Returns the resource location.
    Identifier getId();

    /// Gets the camera data of the given type.
    ///
    /// @return Returns the camera data.
    <T extends CameraData> T getData(CameraDataType<T> dataType);

    HashMap<Class<?>, CameraData> getAllData();
}
