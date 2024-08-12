package cn.anecansaitin.freecamera;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class CameraModifier {
    public static final CameraModifier INSTANCE = new CameraModifier();

    //缓存操作器
    //高优先级
    private static final HashMap<String, Modifier> modifiersH = new HashMap<>();
    //低优先级
    private static final HashMap<String, Modifier> modifiersL = new HashMap<>();
    //背景操作器
    private static final HashMap<String, Modifier> modifiersB = new HashMap<>();
    //正排序
    private static final HashMap<String, Modifier>[] positiveModifiers = new HashMap[]{modifiersH, modifiersL};
    //负排序
    private static final HashMap<String, Modifier>[] negativeModifiers = new HashMap[]{modifiersL, modifiersH};
    //玩家指定顺序
    private static final List<String> playerOrder = new ArrayList<>();
    //玩家移除的背景操作器
    private static final List<String> playerRemovedBackground = new ArrayList<>();

    private final Vector3d selfPos = new Vector3d();
    //上一次的相机局部坐标
    private final Vector3d selfPosO = new Vector3d();
    private final Vector3f selfRot = new Vector3f();
    //上一次的相机局部旋转
    private final Vector3d selfRotO = new Vector3d();
    private double selfFov;
    //上一次的相机FOV
    private double selfFovO;

    private CameraModifier() {
    }

    public void modify() {
        selfPos.zero();
        selfRot.zero();
        selfFov = 70;
        boolean[] modified = new boolean[4];
        //按照玩家指定顺序应用第一个可用操作器
        applyPlayerOrderModifier(modified);

        if (!modified[0]) {
            //按照优先级应用第一个可用操作器
            applyEffectiveModifierFromPositive(modified);
        }

        //应用背景操作器
        applyBackgroundModifier(modified);
        //记录上一次的相机局部坐标、旋转、FOV
        selfPosO.set(selfPos);
        selfRotO.set(selfRot);
        selfFovO = selfFov;

        if (!modified[0]) {
            //无任何修改，直接结束
            return;
        }

        float partialTick = camera().getPartialTickTime();
        Entity entity = camera().getEntity();

        //这里到底是取头部还是身体的旋转？
        float yRot = entity.getYRot();

        selfPos.rotateY(-(yRot % 360) * Mth.DEG_TO_RAD);

        if (modified[1]) {
            //加上玩家坐标，考虑了插值
            selfPos.add(Mth.lerp(partialTick, entity.xo, entity.getX()),
                    Mth.lerp(partialTick, entity.yo, entity.getY()),
                    Mth.lerp(partialTick, entity.zo, entity.getZ()));
            //应用坐标到相机
            camera().setPosition(selfPos.x, selfPos.y, selfPos.z);
        }

        if (modified[2]) {
            camera().setRotation(yRot + selfRot.y, selfRot.x, selfRot.z);
        }

        if (!modified[3]) {
            selfFov = Double.MAX_VALUE;
            selfFovO = Double.MAX_VALUE;
        }
    }

    private void applyBackgroundModifier(boolean[] result) {
        //背景修改器会全部相加并应用
        //将玩家移除的背景操作器设为未修改
        for (int i = playerRemovedBackground.size() - 1; i >= 0; i--) {
            String modId = playerRemovedBackground.get(i);
            Modifier modifier = modifiersB.get(modId);

            if (modifier == null) {
                //不存在这个操作器？移除它
                playerRemovedBackground.remove(i);
                continue;
            }

            modifier.isEffective = false;
        }

        for (Modifier modifier : modifiersB.values()) {
            if (!modifier.enable || !modifier.isEffective) {
                continue;
            }

            if (!modifier.posEnabled && !modifier.rotEnabled && !modifier.fovEnabled) {
                continue;
            }

            result[0] = true;
            result[1] = modifier.posEnabled || result[1];
            result[2] = modifier.rotEnabled || result[2];
            result[3] = modifier.fovEnabled || result[3];

            if (result[1]) {
                selfPos.add(modifier.pos);
            }

            if (result[2]) {
                selfRot.add(modifier.rot);
            }

            if (result[3]) {
                selfFov += modifier.fov;
            }
        }

    }

    private void applyPlayerOrderModifier(boolean[] result) {
        //优先从玩家指定排序获取
        for (int i = playerOrder.size() - 1; i >= 0; i--) {
            String id = playerOrder.get(i);
            Modifier modifier = findModifierFromNegativeById(id);

            if (modifier == null || !modifier.enable || !modifier.isEffective) {
                continue;
            }

            //没有启用任何修改则跳过
            if (!modifier.posEnabled && !modifier.rotEnabled && !modifier.fovEnabled) {
                continue;
            }

            result[0] = true;
            result[1] = modifier.posEnabled;
            result[2] = modifier.rotEnabled;
            result[3] = modifier.fovEnabled;

            if (result[1]) {
                selfPos.add(modifier.pos);
            }

            if (result[2]) {
                selfRot.add(modifier.rot);
            }

            if (result[3]) {
                selfFov += modifier.fov;
            }

            return;
        }

        result[0] = false;
    }

    @Nullable
    private Modifier findModifierFromNegativeById(String modId) {
        Modifier modifier = null;

        for (HashMap<String, Modifier> map : negativeModifiers) {
            modifier = map.get(modId);

            if (modifier != null) {
                break;
            }
        }

        return modifier;
    }

    private void applyEffectiveModifierFromPositive(boolean[] result) {
        for (HashMap<String, Modifier> map : positiveModifiers) {
            Modifier modifier = getEffectiveModifierFromMap(map);

            if (modifier == null) {
                continue;
            }

            result[0] = true;
            result[1] = modifier.posEnabled;
            result[2] = modifier.rotEnabled;
            result[3] = modifier.fovEnabled;

            if (result[1]) {
                selfPos.add(modifier.pos);
            }

            if (result[2]) {
                selfRot.add(modifier.rot);
            }

            if (result[3]) {
                selfFov += modifier.fov;
            }

            return;
        }

        result[0] = false;
    }

    @Nullable
    private Modifier getEffectiveModifierFromMap(HashMap<String, Modifier> map) {
        for (Modifier modifier : map.values()) {
            if (modifier.enable && modifier.isEffective && (modifier.posEnabled || modifier.rotEnabled || modifier.fovEnabled)) {
                return modifier;
            }
        }

        return null;
    }

    private Camera camera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    /**
     * Create a modifier that returns the already created modifier if the provided name has already been used.
     * Although it's not mandatory to use modid as the name, considering there is only one camera, usually one modifier is sufficient.
     *
     * @param modID You mod's id, but this is not mandatory.
     * @param high  True for high priority. More likely to be used, but there is no guarantee that it will definitely be used.
     * @return A modifier named by modID.
     */
    public static Modifier createModifier(String modID, boolean high) {
        if (high) {
            return modifiersH.computeIfAbsent(modID, Modifier::new);
        } else {
            return modifiersL.computeIfAbsent(modID, Modifier::new);
        }
    }

    /**
     * Create a background modifier.
     * All background modifier will be applied.
     *
     * @param modID You mod's id, but this is not mandatory.
     * @return A background modifier named by modID.
     */
    public static Modifier createBackgroundModifier(String modID) {
        return modifiersB.computeIfAbsent(modID, Modifier::new);
    }

    public static class Modifier {
        private final String modId;
        private final Vector3d pos = new Vector3d();
        private boolean posEnabled = false;
        private final Vector3f rot = new Vector3f();
        private boolean rotEnabled = false;
        private double fov;
        private boolean fovEnabled = false;
        private boolean enable = false;
        private boolean isEffective = true;

        private Modifier(String modId) {
            this.modId = modId;
        }

        public Modifier enablePos() {
            posEnabled = true;
            return this;
        }

        public Modifier disablePos() {
            posEnabled = false;
            return this;
        }

        public Modifier setPos(double x, double y, double z) {
            pos.set(x, y, z);
            return this;
        }

        public Modifier addPos(double x, double y, double z) {
            pos.add(x, y, z);
            return this;
        }

        public Modifier enableRotate() {
            rotEnabled = true;
            return this;
        }

        public Modifier disableRotate() {
            rotEnabled = false;
            return this;
        }

        public Modifier setRotate(float xRot, float yRot, float zRot) {
            rot.set(xRot, yRot, zRot);
            return this;
        }

        public Modifier addRotate(float xRot, float yRot, float zRot) {
            rot.add(xRot, yRot, zRot);
            return this;
        }

        public Modifier enableFov() {
            fovEnabled = true;
            return this;
        }

        public Modifier disableFov() {
            fovEnabled = false;
            return this;
        }

        public Modifier setFov(double fov) {
            this.fov = fov;
            return this;
        }

        public Modifier enable() {
            enable = true;
            return this;
        }

        public Modifier disable() {
            enable = false;
            return this;
        }

        public String getModId() {
            return modId;
        }
    }

    @SubscribeEvent
    public static void modifyFov(ViewportEvent.ComputeFov event) {
        if (INSTANCE.selfFov == Double.MAX_VALUE) {
            return;
        }

        if (INSTANCE.selfFovO == Double.MAX_VALUE) {
            INSTANCE.selfFovO = event.getFOV();
        }

        event.setFOV(Mth.lerp(event.getPartialTick(), INSTANCE.selfFovO, INSTANCE.selfFov));
    }
}
