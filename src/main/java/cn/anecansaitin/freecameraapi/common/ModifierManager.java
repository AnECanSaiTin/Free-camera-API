package cn.anecansaitin.freecameraapi.common;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static cn.anecansaitin.freecameraapi.common.ModifierStates.*;

public class ModifierManager {
    public static final ModifierManager INSTANCE = new ModifierManager();
    private final Vector3f
            pos,// 坐标
            rot,// 旋转
            posO,// 上一帧坐标
            rotO;// 上一帧旋转
    private float
            fov,
            fovO;// 上一帧fov
    private int
            state,// 状态
            stateO;// 上一帧状态

    private ModifierManager() {
        pos = new Vector3f();
        rot = new Vector3f();
        posO = new Vector3f();
        rotO = new Vector3f();
    }

    public void modify() {
        saveToOld();
        setToVanilla();
        applyToCamera();
    }

    private void setToVanilla() {
        Camera camera = camera();
        Vec3 cameraPos = camera.getPosition();
        pos.set(cameraPos.x, cameraPos.y, cameraPos.z);
        rot.set(camera.getXRot(), camera.getYRot() % 360, camera.getRoll());
        this.fov = camera().getFov();
    }

    private void saveToOld() {
        posO.set(pos);
        rotO.set(rot);
        fovO = fov;
        stateO = state;
    }

    private void applyToCamera() {
        ICameraModifier modifier = ModifierRegistry.INSTANCE.getActiveModifier();

        if (modifier == null) {
            state = 0;
            return;
        }

        state = modifier.getState();
        applyPos(modifier);
        applyRot(modifier);
        applyFov(modifier);
        applyGlobal(modifier);
        applyLerp(modifier);
        setCamera();
    }

    private void applyPos(ICameraModifier modifier) {
        if (!modifier.isStateEnabledOr(POS)) {
            return;
        }

        pos.set(modifier.getPos());
    }

    private void applyRot(ICameraModifier modifier) {
        if (!modifier.isStateEnabledOr(ROT)) {
            return;
        }

        rot.set(modifier.getRot());
    }

    private void applyFov(ICameraModifier modifier) {
        if (!modifier.isStateEnabledOr(FOV)) {
            return;
        }

        fov = modifier.getFov();
    }

    private void applyGlobal(ICameraModifier modifier) {
        if (modifier.isStateEnabledOr(GLOBAL_MODE)) {
            return;
        }

        float yRot = player().getYRot() % 360;
        pos.rotateY(yRot * Mth.DEG_TO_RAD);
        rot.add(0, yRot, 0);
    }

    private void applyLerp(ICameraModifier modifier) {
        if (!modifier.isStateEnabledOr(LERP) || !isOldStateEnabledOr(LERP)) {
            return;
        }

        float delta = camera().getPartialTickTime();

        if (isOldStateEnabledOr(POS)) {
            pos.set(
                    Mth.lerp(delta, posO.x, pos.x),
                    Mth.lerp(delta, posO.y, pos.y),
                    Mth.lerp(delta, posO.z, pos.z)
            );
        }

        if (isOldStateEnabledOr(ROT)) {
            rot.set(
                    Mth.lerp(delta, rotO.x, rot.x),
                    Mth.lerp(delta, rotO.y, rot.y),
                    Mth.lerp(delta, rotO.z, rot.z)
            );
        }

        if (isOldStateEnabledOr(FOV)) {
            fov = Mth.lerp(delta, fovO, fov);
        }
    }

    private void setCamera() {
        Camera camera = camera();
        camera.setPosition(pos.x, pos.y, pos.z);
        camera.setRotation(rot.y, rot.x, rot.z);
        camera.setFov(fov);
    }

    private Camera camera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    private LocalPlayer player() {
        return Minecraft.getInstance().player;
    }

    private boolean isOldStateEnabledOr(int state) {
        return (stateO & state) != 0;
    }
}
