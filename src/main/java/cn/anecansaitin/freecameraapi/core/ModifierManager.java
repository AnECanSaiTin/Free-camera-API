package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.api.extension.ControlScheme;
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
    private final Vector3f pos;// 坐标
    private final Vector3f rot;// 旋转
    private float fov;// 视角
    private int state;// 状态

    private ModifierManager() {
        pos = new Vector3f();
        rot = new Vector3f();
    }

    public void modify() {
        setToVanilla();
        applyToCamera();
    }

    private void setToVanilla() {
        Camera camera = camera();
        Vec3 cameraPos = camera.getPosition();
        pos.set(cameraPos.x, cameraPos.y, cameraPos.z);
        rot.set(camera.getXRot(), camera.getYRot() % 360, camera.getRoll());
        this.fov = camera().getFov();
        resetExtension();
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
        applyExtension(modifier);
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

        if (modifier.isStateEnabledOr(POS)) {
            Vec3 playerPos = player().getPosition(camera().getPartialTickTime());
            pos.add((float) playerPos.x, (float) playerPos.y, (float) playerPos.z);
        }
    }

    private void applyObstacle(ICameraModifier modifier) {
        if (!modifier.isStateEnabledOr(OBSTACLE)) {
            return;
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

    public Vector3f pos() {
        return pos;
    }

    public Vector3f rot() {
        return rot;
    }

    public float fov() {
        return fov;
    }

    public boolean isStateEnabledAnd(int mask) {
        return (state & mask) == mask;
    }

    public boolean isStateEnabledOr(int mask) {
        return (state & mask) != 0;
    }

    //--------------------------------------------------EXTENSION---------------------------------------------------
    private ControlScheme controlScheme = ControlScheme.VANILLA;// 控制模式

    private void applyExtension(ICameraModifier modifier) {
        applyControlScheme(modifier);
    }

    private void resetExtension() {
        controlScheme = ControlScheme.VANILLA;
    }

    private void applyControlScheme(ICameraModifier modifier) {
        controlScheme = modifier.asExtension().getControlScheme();
    }

    public ControlScheme controlScheme() {
        return controlScheme;
    }
}
