package cn.anecansaitin.freecameraapi.core;

import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Modifier implements ICameraModifier {
    private final ResourceLocation id;
    private final Vector3f pos = new Vector3f();
    private final Vector3f rot = new Vector3f();
    private float fov;
    private int state;

    public Modifier(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public Modifier enablePos() {
        state |= ModifierStates.POS;
        return this;
    }

    @Override
    public Modifier disablePos() {
        state &= ~ModifierStates.POS;
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
        state |= ModifierStates.ROT;
        return this;
    }

    @Override
    public Modifier disableRotation() {
        state &= ~ModifierStates.ROT;
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
        state |= ModifierStates.FOV;
        return this;
    }

    @Override
    public Modifier disableFov() {
        state &= ~ModifierStates.FOV;
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
                .rotateY(rot.y * Mth.DEG_TO_RAD)
                .rotateX(rot.x * Mth.DEG_TO_RAD)
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
    public Vector3f getPos() {
        return pos;
    }

    @Override
    public Vector3f getRot() {
        return rot;
    }

    @Override
    public float getFov() {
        return fov;
    }

    @Override
    public Modifier enable() {
        state |= ModifierStates.ENABLE;
        return this;
    }

    @Override
    public Modifier disable() {
        state &= ~ModifierStates.ENABLE;
        return this;
    }

    @Override
    public ICameraModifier disableAll() {
        state = 0;
        return this;
    }

    @Override
    public Modifier enableGlobalMode() {
        state |= ModifierStates.GLOBAL_MODE;
        return this;
    }

    @Override
    public Modifier disableGlobalMode() {
        state &= ~ModifierStates.GLOBAL_MODE;
        return this;
    }

    @Override
    public ICameraModifier enableObstacle() {
        state |= ModifierStates.OBSTACLE;
        return this;
    }

    @Override
    public ICameraModifier disableObstacle() {
        state &= ~ModifierStates.OBSTACLE;
        return this;
    }

    @Override
    public ICameraModifier setToVanilla() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 position = camera.getPosition();

        if (isStateEnabledOr(ModifierStates.GLOBAL_MODE)) {
            pos.set(position.x, position.y, position.z);
            rot.set(camera.getXRot(), camera.getYRot(), camera.getRoll());
        } else {
            LocalPlayer player = Minecraft.getInstance().player;
            Vec3 playerPos = player.getPosition(camera.getPartialTickTime());
            float viewYRot = player.getViewYRot(camera.getPartialTickTime()) % 360;
            pos.set(position.x - playerPos.x, position.y - playerPos.y, position.z - playerPos.z)
                    .rotateY(viewYRot * Mth.DEG_TO_RAD);
            rot.set(camera.getXRot(), camera.getYRot() + viewYRot, camera.getRoll());
        }

        fov = camera.getFov();
        return this;
    }

    @Override
    public ICameraModifier enableChunkLoader() {
        state |= ModifierStates.CHUNK_LOADER;
        return this;
    }

    @Override
    public ICameraModifier disableChunkLoader() {
        state &= ~ModifierStates.CHUNK_LOADER;
        return this;
    }

    @Override
    public ICameraModifier clean() {
        pos.zero();
        rot.zero();
        fov = 0;
        return this;
    }

    @Override
    public ICameraModifier reset() {
        disableAll();
        clean();
        return this;
    }

    @Override
    public ICameraModifier setState(int state) {
        this.state = state;
        return this;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
