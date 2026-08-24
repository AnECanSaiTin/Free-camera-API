package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.api.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashMap;

public class Modifier implements CameraModifier {
    private final Identifier id;
    private final Vector3f pos = new Vector3f();
    private final Vector3f rot = new Vector3f();
    private float fov;
    private int state;
    private ObstacleHandler obstacleHandler;
    private final HashMap<Class<?>, CameraData> cameraData = new HashMap<>();

    public Modifier(Identifier id) {
        this.id = id;
    }

    @Override
    public Modifier enablePos() {
        state |= CameraStates.POS.code;
        return this;
    }

    @Override
    public Modifier disablePos() {
        state &= ~CameraStates.POS.code;
        return this;
    }

    @Override
    public Modifier setPos(float x, float y, float z) {
        pos.set(x, y, z);
        return this;
    }

    @Override
    public Modifier setPos(Vector3f pos) {
        return setPos(pos.x, pos.y, pos.z);
    }

    @Override
    public Modifier addPos(float x, float y, float z) {
        pos.add(x, y, z);
        return this;
    }

    @Override
    public Modifier addPos(Vector3f pos) {
        return addPos(pos.x, pos.y, pos.z);
    }

    @Override
    public Modifier enableRotation() {
        state |= CameraStates.ROT.code;
        return this;
    }

    @Override
    public Modifier disableRotation() {
        state &= ~CameraStates.ROT.code;
        return this;
    }

    @Override
    public Modifier setRotationYXZ(float xRot, float yRot, float zRot) {
        rot.set(xRot, yRot, zRot);
        return this;
    }

    @Override
    public Modifier setRotationYXZ(Vector3f rot) {
        return setRotationYXZ(rot.x, rot.y, rot.z);
    }

    @Override
    public Modifier setRotationZYX(float xRot, float yRot, float zRot) {
        return setRotationYXZ(eulerZYXToYXZ(xRot, yRot, zRot));
    }

    @Override
    public Modifier setRotationZYX(Vector3f rot) {
        return setRotationYXZ(eulerZYXToYXZ(rot.x, rot.y, rot.z));
    }

    @Override
    public Modifier rotateYXZ(float xRot, float yRot, float zRot) {
        rot.add(xRot, yRot, zRot);
        return this;
    }

    private Vector3f eulerZYXToYXZ(float x, float y, float z) {
        x *= Mth.DEG_TO_RAD;
        y *= Mth.DEG_TO_RAD;
        z *= Mth.DEG_TO_RAD;

        return new Quaternionf()
                .rotationZYX(z, y, x)
                .getEulerAnglesYXZ(new Vector3f())
                .mul(Mth.RAD_TO_DEG);
    }

    @Override
    public Modifier enableFov() {
        state |= CameraStates.FOV.code;
        return this;
    }

    @Override
    public Modifier disableFov() {
        state &= ~CameraStates.FOV.code;
        return this;
    }

    @Override
    public Modifier setFov(float fov) {
        this.fov = fov;
        return this;
    }

    @Override
    public Modifier move(float x, float y, float z) {
        Vector3f vec = new Vector3f(x, y, z)
                .rotateX(rot.x * Mth.DEG_TO_RAD)
                .rotateY(-rot.y * Mth.DEG_TO_RAD)
                .rotateZ(rot.z * Mth.DEG_TO_RAD);
        pos.add(vec);
        return this;
    }

    @Override
    public Modifier aimAt(float x, float y, float z) {
        Vector3f aim = new Vector3f(x - pos.x, y - pos.y, z - pos.z);

        rot.x = org.joml.Math.acos(Math.sqrt(aim.x * aim.x + aim.z * aim.z) / aim.length()) * Mth.RAD_TO_DEG * (aim.y < 0 ? 1 : -1);
        rot.y = (float) -(Mth.atan2(aim.x, aim.z) * Mth.RAD_TO_DEG);
        return this;
    }

    @Override
    public Vector3fc getPos() {
        return pos;
    }

    @Override
    public Vector3fc getRot() {
        return rot;
    }

    @Override
    public float getFov() {
        return fov;
    }

    @Override
    public Modifier enable() {
        state |= CameraStates.ENABLE.code;
        return this;
    }

    @Override
    public Modifier disable() {
        state &= ~CameraStates.ENABLE.code;
        return this;
    }

    @Override
    public CameraModifier disableAll() {
        state = 0;
        return this;
    }

    @Override
    public Modifier enableGlobalMode() {
        state |= CameraStates.GLOBAL_MODE.code;
        return this;
    }

    @Override
    public Modifier disableGlobalMode() {
        state &= ~CameraStates.GLOBAL_MODE.code;
        return this;
    }

    @Override
    public CameraModifier enableObstacle() {
        state |= CameraStates.OBSTACLE.code;
        obstacleHandler = ObstacleHandler.NULL;
        return this;
    }

    @Override
    public CameraModifier enableObstacle(@NotNull ObstacleHandler handler) {
        state |= CameraStates.OBSTACLE.code;
        obstacleHandler = handler;
        return this;
    }

    @Override
    public CameraModifier disableObstacle() {
        state &= ~CameraStates.OBSTACLE.code;
        return this;
    }

    @Override
    public ObstacleHandler getObstacleHandler() {
        return obstacleHandler;
    }

    @Override
    public CameraModifier setToVanilla() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 position = camera.position();

        if (isStateEnabledOr(CameraStates.GLOBAL_MODE.code)) {
            pos.set(position.x, position.y, position.z);
            rot.set(camera.xRot(), camera.yRot(), camera.getRoll());
        } else {
            Vec3 playerPos = ClientUtil.player().getPosition(ClientUtil.partialTicks());
            pos.set(position.x - playerPos.x, position.y - playerPos.y, position.z - playerPos.z);
            rot.set(camera.xRot(), camera.yRot(), camera.getRoll());
        }

        fov = camera.getFov();
        return this;
    }

    @Override
    public CameraModifier clean() {
        pos.zero();
        rot.zero();
        fov = 0;
        return this;
    }

    @Override
    public CameraModifier reset() {
        disableAll();
        clean();
        return this;
    }

    @Override
    public CameraModifier setState(int state) {
        this.state = state;
        return this;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public <T extends CameraData> T getData(CameraDataType<T> dataType) {
        CameraData data = this.cameraData.get(dataType.type());

        if (data == null) {
            data = dataType.create();
            this.cameraData.put(dataType.type(), data);
        }

        return dataType.type().cast(data);
    }

    @Override
    public HashMap<Class<?>, CameraData> getAllData() {
        return cameraData;
    }
}
