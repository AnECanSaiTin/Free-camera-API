package cn.anecansaitin.freecameraapi.api;

import cn.anecansaitin.freecameraapi.core.ModifierStates;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

/// Camera modifier interface, used to define various camera modification operations.
public interface ICameraModifier {
    /// Enables position modification.
    ICameraModifier enablePos();

    /// Disables position modification.
    ICameraModifier disablePos();

    /// Sets the camera position.
    ///
    /// @param x X-axis coordinate.
    /// @param y Y-axis coordinate.
    /// @param z Z-axis coordinate.
    ICameraModifier setPos(float x, float y, float z);

    /// Sets the camera position.
    ///
    /// @param pos Position vector.
    ICameraModifier setPos(Vector3f pos);

    /// Adds a position offset to the camera.
    ///
    /// @param x X-axis offset.
    /// @param y Y-axis offset.
    /// @param z Z-axis offset.
    ICameraModifier addPos(float x, float y, float z);

    /// Adds a position offset to the camera.
    ///
    /// @param pos Offset vector.
    ICameraModifier addPos(Vector3f pos);

    /// Enables rotation modification.
    ICameraModifier enableRotation();

    /// Disables rotation modification.
    ICameraModifier disableRotation();

    /// Sets the rotation angles in YXZ order.
    ///
    /// @param xRot X-axis rotation angle.
    /// @param yRot Y-axis rotation angle.
    /// @param zRot Z-axis rotation angle.
    ICameraModifier setRotationYXZ(float xRot, float yRot, float zRot);

    /// Sets the rotation angles in YXZ order.
    ///
    /// @param rot Rotation vector.
    ICameraModifier setRotationYXZ(Vector3f rot);

    /// Sets the rotation angles in ZYX order.
    ///
    /// @param xRot X-axis rotation angle.
    /// @param yRot Y-axis rotation angle.
    /// @param zRot Z-axis rotation angle.
    ICameraModifier setRotationZYX(float xRot, float yRot, float zRot);

    /// Sets the rotation angles in ZYX order.
    ///
    /// @param rot Rotation vector.
    ICameraModifier setRotationZYX(Vector3f rot);

    /// Rotates the camera in YXZ order.
    ///
    /// @param xRot X-axis rotation angle.
    /// @param yRot Y-axis rotation angle.
    /// @param zRot Z-axis rotation angle.
    ICameraModifier rotateYXZ(float xRot, float yRot, float zRot);

    /// Enables field-of-view (FOV) modification.
    ICameraModifier enableFov();

    /// Disables field-of-view (FOV) modification.
    ICameraModifier disableFov();

    /// Sets the field-of-view (FOV) angle.
    ///
    /// @param fov Field-of-view angle.
    ICameraModifier setFov(float fov);

    /// Moves the camera position.
    ///
    /// @param x X-axis movement.
    /// @param y Y-axis movement.
    /// @param z Z-axis movement.
    ICameraModifier move(float x, float y, float z);

    /// Aims the camera at a specified point.
    ///
    /// @param x X-axis coordinate of the target point.
    /// @param y Y-axis coordinate of the target point.
    /// @param z Z-axis coordinate of the target point.
    ICameraModifier aimAt(float x, float y, float z);

    /// Gets the current camera position.
    ///
    /// @return Returns the camera position vector.
    Vector3f getPos();

    /// Gets the current camera rotation angles.
    ///
    /// @return Returns the rotation vector.
    Vector3f getRot();

    /// Gets the current field-of-view (FOV) angle.
    ///
    /// @return Returns the FOV angle.
    float getFov();

    /// Enables the modifier.
    ICameraModifier enable();

    /// Disables the modifier.
    ICameraModifier disable();

    /// Disables all states.
    ICameraModifier disableAll();

    /// Enables global mode.
    ICameraModifier enableGlobalMode();

    /// Disables global mode.
    ICameraModifier disableGlobalMode();

    /// Enables default obstacle avoidance.
    ICameraModifier enableObstacle();

    /// Disables default obstacle avoidance.
    ICameraModifier disableObstacle();

    /// Reverts to vanilla camera settings.
    ICameraModifier setToVanilla();

    /// Enables chunk loading.
    ICameraModifier enableChunkLoader();

    /// Disables chunk loading.
    ICameraModifier disableChunkLoader();

    /// Sets position, rotation, and FOV to zero.
    ICameraModifier clean();

    /// Resets all parameters and states.
    ///
    /// Disables all states. Sets position, rotation, and FOV to zero.
    ICameraModifier reset();

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
    ICameraModifier setState(int state);

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
        return state >= 1 && isStateEnabledOr(ModifierStates.ENABLE) && isStateEnabledOr(ModifierStates.POS | ModifierStates.ROT | ModifierStates.FOV);
    }

    /// Gets the unique identifier of the modifier.
    ///
    /// @return Returns the resource location.
    ResourceLocation getId();
}
