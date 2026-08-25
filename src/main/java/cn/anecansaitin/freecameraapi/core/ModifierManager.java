package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.api.CameraData;
import cn.anecansaitin.freecameraapi.api.CameraDataType;
import cn.anecansaitin.freecameraapi.api.ObstacleContext;
import cn.anecansaitin.freecameraapi.api.ObstacleHandler;
import cn.anecansaitin.freecameraapi.api.CameraModifier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashMap;

import static cn.anecansaitin.freecameraapi.api.CameraStates.*;

public class ModifierManager {
    public static final ModifierManager INSTANCE = new ModifierManager();
    private final Vector3f pos;// 坐标
    private final Vector3f rot;// 旋转
    private float fov;// 视场
    private int state;// 状态
    private HashMap<Class<?>, CameraData> cameraData = new HashMap<>();

    private ModifierManager() {
        pos = new Vector3f();
        rot = new Vector3f();
    }

    public void modify() {
        setToVanilla();
        applyToCamera();
        updateCamera();
    }

    private void setToVanilla() {
        Camera camera = camera();
        Vec3 cameraPos = camera.position();
        pos.set(cameraPos.x, cameraPos.y, cameraPos.z);
        rot.set(camera.xRot(), camera.yRot() % 360, camera.getRoll());
        this.fov = camera().getFov();
    }

    private void applyToCamera() {
        CameraModifier modifier = ModifierRegistry.INSTANCE.getActiveModifier();

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
        applyCameraData(modifier);
    }

    private void applyPos(CameraModifier modifier) {
        Vector3fc src = modifier.getPos();

        if (modifier.isStateEnabledOr(POS_X.code)) pos.x = src.x();
        if (modifier.isStateEnabledOr(POS_Y.code)) pos.y = src.y();
        if (modifier.isStateEnabledOr(POS_Z.code)) pos.z = src.z();
    }

    private void applyRot(CameraModifier modifier) {
        Vector3fc src = modifier.getRot();

        if (modifier.isStateEnabledOr(ROT_X.code)) rot.x = src.x();
        if (modifier.isStateEnabledOr(ROT_Y.code)) rot.y = src.y();
        if (modifier.isStateEnabledOr(ROT_Z.code)) rot.z = src.z();
    }

    private void applyFov(CameraModifier modifier) {
        if (!modifier.isStateEnabledOr(FOV.code)) {
            return;
        }

        fov = modifier.getFov();
    }

    private void applyGlobal(CameraModifier modifier) {
        if (modifier.isStateEnabledOr(GLOBAL_MODE.code)) {
            return;
        }

        if (modifier.isStateEnabledOr(POS.code)) {
            Vec3 playerPos = ClientUtil.player().getPosition(ClientUtil.partialTicks());
            float px = (float) playerPos.x;
            float py = (float) playerPos.y;
            float pz = (float) playerPos.z;

            // 分量级叠加：只对启用的分量加玩家位置，未启用分量保持 vanilla 相对值。
            if (modifier.isStateEnabledOr(POS_X.code)) pos.x += px;
            if (modifier.isStateEnabledOr(POS_Y.code)) pos.y += py;
            if (modifier.isStateEnabledOr(POS_Z.code)) pos.z += pz;
        }
    }

    private final ObstacleContext obstacleContext = new ObstacleContext();

    private void applyObstacle(CameraModifier modifier) {
        if (!modifier.isStateEnabledOr(OBSTACLE.code)) {
            return;
        }

        ObstacleHandler obstacleHandler = modifier.getObstacleHandler();

        // Populate context with current manager state + enabled component mask.
        obstacleContext.pos(pos);
        obstacleContext.rot(rot);
        obstacleContext.fov(fov);
        obstacleContext.enabledMask(modifier.getState());

        switch (obstacleHandler.obstacleAvoid(obstacleContext)) {
            case PASS -> defaultObstacle(modifier, obstacleHandler);
            case COLLIDE -> {
                // Handler fully resolved and claims a collision happened.
                // Apply handler's full position / rotation / FOV changes back into manager state.
                obstacleContext.copyTo(pos, rot);
                fov = obstacleContext.fov();
                obstacleHandler.onCollision(obstacleContext);
            }
            case NO_COLLIDE -> {
                // Handler explicitly states there is no collision.
                // Keep the current manager state as-is and do NOT apply any handler
                // edits from the context, and do NOT emit a collision notification.
            }
        }
    }

    /// Built-in obstacle avoidance (PASS path).
    ///
    /// When a collision is detected the resolved position is written back in full — this
    /// models the intuitive behaviour of a camera being physically pushed away from a wall
    /// along the entire eye→camera vector. Callers that need per-component semantics must
    /// resolve it themselves in a custom ObstacleHandler (return COLLIDE or NO_COLLIDE).
    /// FOV is never touched by the built-in algorithm.
    private void defaultObstacle(CameraModifier modifier, ObstacleHandler obstacleHandler) {
        Vector3f
                origin = ClientUtil.player().getEyePosition(ClientUtil.partialTicks()).toVector3f(),
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

            HitResult hitresult = ClientUtil.player().level().clip(new ClipContext(begin, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, ClientUtil.player()));

            if (hitresult.getType() != HitResult.Type.MISS) {
                float distance = (float) hitresult.getLocation().distanceToSqr(origin.x, origin.y, origin.z);

                if (distance < Mth.square(max)) {
                    max = Mth.sqrt(distance);
                }
            }
        }

        if (max == length) {
            // No collision detected. Do NOT notify the handler and keep manager state as-is.
            return;
        }

        // Resolve the collision along the eye→camera direction and write back the full
        // position vector. Full write-back matches the PASS contract: the built-in algorithm
        // is meant to produce a "visually correct" pushed-away camera.
        pos.set(direction.normalize(max).add(origin));

        // Sync context with the final position so the handler can observe it in onCollision.
        obstacleContext.pos(pos);
        obstacleHandler.onCollision(obstacleContext);
    }

    private void applyCameraData(CameraModifier modifier) {
        cameraData = modifier.getAllData();
    }

    private void updateCamera() {
        updateCameraData();
        setCamera();
    }

    private void updateCameraData() {
        for (CameraData data : cameraData.values()) {
            data.update();
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

    public <T extends CameraData> T getData(CameraDataType<T> dataType) {
        Class<T> type = dataType.type();
        CameraData data = cameraData.get(type);

        if (data == null) {
            data = dataType.create();
            cameraData.put(type, data);
        }

        return type.cast(data);
    }
}
