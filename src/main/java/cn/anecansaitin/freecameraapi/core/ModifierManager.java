package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static cn.anecansaitin.freecameraapi.core.ModifierStates.*;

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
        applyObstacle(modifier);
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

        float yRot = -player().getViewYRot(camera().getPartialTickTime()) % 360;

        if (modifier.isStateEnabledOr(POS)) {
            Vec3 playerPos = player().getPosition(camera().getPartialTickTime());
            pos.rotateY(yRot * Mth.DEG_TO_RAD)
                    .add((float) playerPos.x, (float) playerPos.y, (float) playerPos.z);
        }

        if (modifier.isStateEnabledOr(ROT)) {
            rot.add(0, yRot, 0);
        }
    }

    private void applyObstacle(ICameraModifier modifier) {
        if (!modifier.isStateEnabledOr(OBSTACLE)) {
            return;//todo 测试
        }

        Vector3f
                origin = player().getEyePosition(camera().getPartialTickTime()).toVector3f(),
                direction = pos.sub(origin, new Vector3f());
        float
                size = 0.1F,
                max = direction.length(),
                length = max;

        for (int i = 0; i < 8; i++) {
            float
                    x = size * (float) ((i & 1) * 2 - 1),
                    y = size * (float) ((i >> 1 & 1) * 2 - 1),
                    z = size * (float) ((i >> 2 & 1) * 2 - 1);

            Vec3
                    begin = new Vec3(origin.x + x, origin.y + y, origin.z + z),
                    end = new Vec3(pos.x + x, pos.y + y, pos.z + z);

            HitResult hitresult = player().level().clip(new ClipContext(begin, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player()));

            if (hitresult.getType() != HitResult.Type.MISS) {
                float distance = (float) hitresult.getLocation().distanceToSqr(origin.x, origin.y, origin.z);

                if (distance < Mth.square(max)) {
                    max = Mth.sqrt(distance);
                }
            }
        }

        if (max == length) {
            return;
        }

        pos.set(direction.normalize(max).add(origin));
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
        camera.setRotation(rot.y, rot.x, rot.z);
        camera.setPosition(pos.x, pos.y, pos.z);
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
