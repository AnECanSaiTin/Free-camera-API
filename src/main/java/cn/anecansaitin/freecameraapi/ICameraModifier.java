package cn.anecansaitin.freecameraapi;

import org.joml.Vector3d;
import org.joml.Vector3f;

public interface ICameraModifier {
    ICameraModifier enablePos();

    ICameraModifier disablePos();

    ICameraModifier setPos(float x, float y, float z);

    ICameraModifier setPos(Vector3f pos);

    ICameraModifier addPos(double x, double y, double z);

    ICameraModifier addPos(Vector3f pos);

    ICameraModifier enableRotation();

    ICameraModifier disableRotation();

    ICameraModifier setRotationYXZ(float xRot, float yRot, float zRot);

    ICameraModifier setRotationYXZ(Vector3f rot);

    ICameraModifier setRotationZYX(float xRot, float yRot, float zRot);

    ICameraModifier setRotationZYX(Vector3f rot);

    ICameraModifier rotateYXZ(float xRot, float yRot, float zRot);

    ICameraModifier enableFov();

    ICameraModifier disableFov();

    ICameraModifier setFov(float fov);

    ICameraModifier move(float x, float y, float z);

    ICameraModifier aimAt(float x, float y, float z);

    Vector3d getPos();

    Vector3f getRot();

    float getFov();

    /**
     * Enable modifier.<br/>
     * 启用操作器。
     */
    ICameraModifier enable();

    /**
     * Disable modifier.<br/>
     * 关闭操作器。
     */
    ICameraModifier disable();

    /**
     * Disable all state.<br/>
     * 关闭所有状态。
     */
    ICameraModifier disableAll();

    ICameraModifier enableFirstPersonArmFixed();

    ICameraModifier disableFirstPersonArmFixed();

    ICameraModifier enableGlobalMode();

    ICameraModifier disableGlobalMode();

    ICameraModifier enableLerp();

    ICameraModifier disableLerp();

    /**
     * Disable all state. Set pos, rotation, fov to zero.<br/>
     * 关闭所有状态。将位置，旋转，FOV设置为零。
     */
    ICameraModifier reset();

    /**
     * Set modifier state by integer. For example:<br/>
     * 通过整数来设置状态。例如：
     * <pre>
     *     modifier.setState(ModifierStates.ENABLE | ModifierStates.POS_ENABLED);
     * </pre>
     * This is the same as:<br/>
     * 等效于：
     * <pre>
     *     modifier.enable().enablePos()
     * </pre>
     */
    ICameraModifier setState(int state);

    /**
     * @param dest state save into int[0]. 状态会保存到int[0]中。
     *
     */
    ICameraModifier getState(int[] dest);

    String getModId();
}
