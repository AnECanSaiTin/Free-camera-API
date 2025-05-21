package cn.anecansaitin.freecameraapi.common;

import cn.anecansaitin.freecameraapi.starup.IPlugin;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;

public class ModifierRegistry {
    public static final ModifierRegistry INSTANCE = new ModifierRegistry();
    private Map<ModifierPriority, List<ICameraModifier>> priorityMap;
    private final Map<String, ICameraModifier> modifierMap;
    private final List<ICameraModifier> modifierList;
    private final List<ICameraModifier> removedList;
    private final Map<ICameraModifier, IPlugin> plugins;
    private boolean frozen = false;

    private ModifierRegistry() {
        priorityMap = new EnumMap<>(ModifierPriority.class);
        modifierMap = new HashMap<>();
        modifierList = new ArrayList<>();
        removedList = new ArrayList<>();
        plugins = new HashMap<>();

        for (ModifierPriority priority : ModifierPriority.values()) {
            priorityMap.put(priority, new ArrayList<>());
        }
    }

    public void register(String id, IPlugin plugin) {
        register(id, plugin, ModifierPriority.NORMAL);
    }

    public void register(String id, IPlugin plugin, ModifierPriority priority) {
        register(plugin, priority, new Modifier(id));
    }

    public void register(IPlugin plugin, ModifierPriority priority, ICameraModifier modifier) {
        if (frozen) {
            throw new IllegalStateException("ModifierRegistry is frozen");
        }

        if (!modifierMap.containsKey(modifier.getId())) {
            modifierMap.put(modifier.getId(), modifier);
            priorityMap.get(priority).add(modifier);
            plugins.put(modifier, plugin);
        }
    }

    public void freeze(List<String> order, List<String> removed) {
        if (frozen) {
            return;
        }

        frozen = true;
        sort();
        setOrderById(order, removed);
    }

    private void sort() {
        for (ModifierPriority priority : ModifierPriority.values()) {
            modifierList.addAll(priorityMap.get(priority));
        }

        priorityMap = null;
    }

    private void setOrderById(List<String> order, List<String> removed) {
        ArrayList<ICameraModifier> orderList = new ArrayList<>();
        ArrayList<ICameraModifier> removedList = new ArrayList<>();

        for (String id : order) {
            ICameraModifier modifier = modifierMap.get(id);

            if (modifier == null) {
                continue;
            }

            orderList.add(modifier);
        }

        for (String id : removed) {
            ICameraModifier modifier = modifierMap.get(id);

            if (modifier == null) {
                continue;
            }

            removedList.add(modifier);
        }

        modifierList.removeAll(orderList);
        modifierList.addAll(0, orderList);
        modifierList.removeAll(removedList);
        this.removedList.clear();
        this.removedList.addAll(removedList);
    }

    /// 移动一个修改器到新位置
    public void move(int index, int newIndex) {
        modifierList.add(newIndex, modifierList.remove(index));
    }

    /// 从列表中移除一个修改器
    public void remove(int index) {
        removedList.add(modifierList.remove(index));
    }

    /// 从已移除取回修改器
    public void moveBack(int index,  int newIndex) {
        modifierList.add(newIndex, modifierList.remove(index));
    }

    public List<ICameraModifier> getModifiers() {
        return modifierList;
    }

    public List<ICameraModifier> getRemovedModifiers() {
        return removedList;
    }

    public void updateController() {
        for (ICameraModifier modifier : modifierList) {
            plugins.get(modifier).update(modifier);
        }
    }

    @Nullable
    public ICameraModifier getActiveModifier() {
        for (ICameraModifier modifier : modifierList) {
            if (modifier.isActive()) {
                return modifier;
            }
        }

        return null;
    }

    private static class Modifier implements ICameraModifier {
        private final String modId;
        private final Vector3f pos = new Vector3f();
        private final Vector3f rot = new Vector3f();
        private float fov;
        private int state;

        public Modifier(String modId) {
            this.modId = modId;
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
                    .rotateX(rot.x * Mth.DEG_TO_RAD)
                    .rotateY(-rot.y * Mth.DEG_TO_RAD)
                    .rotateZ(rot.z * Mth.DEG_TO_RAD);
            pos.add(vec);
            return this;
        }

        @Override
        public Modifier aimAt(float x, float y, float z) {
            Vector3d aim = new Vector3d(x - pos.x, y - pos.y, z - pos.z);

            rot.x = (float) org.joml.Math.acos(Math.sqrt(aim.x * aim.x + aim.z * aim.z) / aim.length()) * Mth.RAD_TO_DEG * (aim.y < 0 ? 1 : -1);
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
        public Modifier enableLerp() {
            state |= ModifierStates.LERP;
            return this;
        }

        @Override
        public Modifier disableLerp() {
            state &= ~ModifierStates.LERP;
            return this;
        }

        @Override
        public ICameraModifier setToVanilla() {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 position = camera.getPosition();
            pos.set(position.x, position.y, position.z);
            rot.set(camera.getXRot(), camera.getYRot(), camera.getRoll());
            fov = camera.getFov();
            return this;
        }

        @Override
        public ICameraModifier reset() {
            disableAll();
            pos.zero();
            rot.zero();
            fov = 0;
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
        public String getId() {
            return modId;
        }
    }
}
