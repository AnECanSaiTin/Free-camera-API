package cn.anecansaitin.freecameraapi;

import org.joml.Vector3d;
import org.joml.Vector3f;

public interface ICameraModifier {
    ICameraModifier enablePos();

    ICameraModifier disablePos();

    ICameraModifier setPos(double x, double y, double z);

    ICameraModifier setPos(Vector3d pos);

    ICameraModifier addPos(double x, double y, double z);

    ICameraModifier addPos(Vector3d pos);

    ICameraModifier enableRotation();

    ICameraModifier disableRotation();

    ICameraModifier setRotationYXZ(float xRot, float yRot, float zRot);

    ICameraModifier setRotationYXZ(Vector3f rot);

    ICameraModifier setRotationZYX(float xRot, float yRot, float zRot);

    ICameraModifier setRotationZYX(Vector3f rot);

    ICameraModifier rotateYXZ(float xRot, float yRot, float zRot);

    ICameraModifier enableFov();

    ICameraModifier disableFov();

    ICameraModifier setFov(double fov);

    ICameraModifier move(double x, double y, double z);

    ICameraModifier aimAt(double x, double y, double z);

    Vector3d getPos();

    Vector3f getRot();

    double getFov();

    ICameraModifier enable();

    ICameraModifier disable();

    ICameraModifier disableAll();

    ICameraModifier enableFirstPersonArmFixed();

    ICameraModifier disableFirstPersonArmFixed();

    ICameraModifier enableGlobalMode();

    ICameraModifier disableGlobalMode();

    ICameraModifier enableLerp();

    ICameraModifier disableLerp();

    ICameraModifier reset();

    String getModId();
}
