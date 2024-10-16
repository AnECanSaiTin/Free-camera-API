package cn.anecansaitin.freecameraapi;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class CameraModifierManager {
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

    //相机全局模式坐标
    private static final Vector3d globalPos = new Vector3d();
    //上一次的相机全局模式坐标
    private static final Vector3d globalPosO = new Vector3d();
    //相机局部模式坐标
    private static final Vector3d selfPos = new Vector3d();
    //上一次的相机局部坐标
    private static final Vector3d selfPosO = new Vector3d();

    //相机旋转
    private static final Vector3f rotation = new Vector3f();
    //上一次的相机局部旋转
    private static final Vector3f rotationO = new Vector3f();

    //相机FOV
    private static double FOV;
    //上一次的相机FOV
    private static double FOV_O;

    //相机状态常量
    private static final int ENABLE = 1;
    private static final int POS_ENABLED = 1 << 1;
    private static final int ROT_ENABLED = 1 << 2;
    private static final int FOV_ENABLED = 1 << 3;
    private static final int FIRST_PERSON_ARM_FIXED = 1 << 4;
    private static final int GLOBAL_MODE_ENABLED = 1 << 5;
    private static final int LERP = 1 << 6;
    //相机状态
    private static int STATE;
    //上一次相机状态
    private static int STATE_O;

    public static void modify() {
        cleanCache();
        //按照玩家指定顺序应用第一个可用操作器
        applyPlayerOrderModifier();

        if (!isStateEnabledOr(ENABLE)) {
            //按照优先级应用第一个可用操作器
            applyEffectiveModifierFromPositive();
        }

        //应用背景操作器
        applyBackgroundModifier();

        if (!isStateEnabledOr(ENABLE)) {
            cleanCache();
            //记录上一次的相机局部坐标、旋转、FOV
            saveToOld();
            //无任何修改，直接结束
            return;
        }

        float partialTick = camera().getPartialTickTime();
        Entity entity = camera().getEntity();

        //这里到底是取头部还是身体的旋转？
        float yRot = entity.getYRot() % 360;

        //操作坐标
        applyModifyToPos(partialTick, yRot, entity);

        //操作旋转
        applyModifyToRot(partialTick, yRot);

        saveToOld();
    }

    private static void applyPlayerOrderModifier() {
        //优先从玩家指定排序获取
        for (int i = playerOrder.size() - 1; i >= 0; i--) {
            String id = playerOrder.get(i);
            Modifier modifier = findModifierFromNegativeById(id);

            if (modifier == null || !modifier.isStateEnabledOr(ENABLE) || !modifier.isEffective) {
                continue;
            }

            //没有启用任何修改则跳过
            if (!modifier.isStateEnabledOr(POS_ENABLED | ROT_ENABLED | FOV_ENABLED)) {
                continue;
            }

            applyValue(modifier);

            return;
        }
    }

    @Nullable
    private static Modifier findModifierFromNegativeById(String modId) {
        Modifier modifier = null;

        for (HashMap<String, Modifier> map : negativeModifiers) {
            modifier = map.get(modId);

            if (modifier != null) {
                break;
            }
        }

        return modifier;
    }

    private static void applyEffectiveModifierFromPositive() {
        for (HashMap<String, Modifier> map : positiveModifiers) {
            Modifier modifier = getEffectiveModifierFromMap(map);

            if (modifier == null) {
                continue;
            }

            applyValue(modifier);

            return;
        }
    }

    @Nullable
    private static Modifier getEffectiveModifierFromMap(HashMap<String, Modifier> map) {
        for (Modifier modifier : map.values()) {
            if (modifier.isStateEnabledOr(ENABLE) && modifier.isEffective && (modifier.isStateEnabledOr(POS_ENABLED | ROT_ENABLED | FOV_ENABLED))) {
                return modifier;
            }
        }

        return null;
    }

    private static void applyBackgroundModifier() {
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
            if (!modifier.isStateEnabledOr(ENABLE) || !modifier.isEffective) {
                continue;
            }

            if (!modifier.isStateEnabledOr(POS_ENABLED | ROT_ENABLED | FOV_ENABLED)) {
                continue;
            }

            applyValue(modifier);
        }
    }

    private static void applyValue(Modifier modifier) {
        STATE |= modifier.state;

        if (modifier.isStateEnabledOr(POS_ENABLED)) {
            if (modifier.isStateEnabledOr(GLOBAL_MODE_ENABLED)) {
                globalPos.add(modifier.pos);
            } else {
                selfPos.add(modifier.pos);
            }
        }

        if (isStateEnabledOr(ROT_ENABLED)) {
            rotation.add(modifier.rot);
        }

        if (isStateEnabledOr(FOV_ENABLED)) {
            FOV += modifier.fov;
        }
    }

    private static void saveToOld() {
        //记录上一次的相机局部坐标、旋转、FOV
        globalPosO.set(globalPos);
        selfPosO.set(selfPos);
        rotationO.set(rotation);
        FOV_O = FOV;
        STATE_O = STATE;
    }

    private static void applyModifyToRot(float partialTick, float yRot) {
        if (!isStateEnabledOr(ROT_ENABLED)) {
            return;
        }

        Vector3f rot;

        if (isStateEnabledOr(GLOBAL_MODE_ENABLED)) {
            //全局模式，不应用玩家旋转
            if (isOldStateEnabledAnd(ENABLE | ROT_ENABLED | LERP)) {
                //如果上次开启了旋转，则要计算插值
                rot = new Vector3f(
                        Mth.lerp(partialTick, rotationO.x, rotation.x),
                        Mth.lerp(partialTick, rotationO.y, rotation.y),
                        Mth.lerp(partialTick, rotationO.z, rotation.z)
                );
            } else {
                //否则直接使用原始值
                rot = rotation;
            }
        } else {
            //局部模式应用玩家旋转
            rot = new Vector3f(0, yRot, 0);

            if (isOldStateEnabledAnd(ENABLE | ROT_ENABLED | LERP)) {
                //如果上次开启了旋转，则要计算插值
                rot.add(
                        Mth.lerp(partialTick, rotationO.x, rotation.x),
                        Mth.lerp(partialTick, rotationO.y, rotation.y),
                        Mth.lerp(partialTick, rotationO.z, rotation.z)
                );
            } else {
                //否则直接使用原始值
                rot.add(rotation);
            }
        }

        camera().setRotation(rot.y, rot.x, rot.z);
    }

    private static void applyModifyToPos(float partialTick, float yRot, Entity entity) {
        if (!isStateEnabledOr(POS_ENABLED)) {
            return;
        }

        Vector3d pos;

        if (isStateEnabledOr(GLOBAL_MODE_ENABLED)) {
            //全局模式
            if (isOldStateEnabledAnd(GLOBAL_MODE_ENABLED | ENABLE | POS_ENABLED | LERP)) {
                //如果上次开启了全局模式，则要计算插值
                pos = new Vector3d(
                        Mth.lerp(partialTick, globalPosO.x, globalPos.x),
                        Mth.lerp(partialTick, globalPosO.y, globalPos.y),
                        Mth.lerp(partialTick, globalPosO.z, globalPos.z)
                );
            } else {
                //否则直接使用原始值
                pos = new Vector3d(globalPos);
            }
        } else {
            //局部模式
            if (isOldStateEnabledAnd(ENABLE | POS_ENABLED | LERP) && !isOldStateEnabledOr(GLOBAL_MODE_ENABLED)) {
                //如果上次是局部模式，则计算插值
                pos = new Vector3d(
                        Mth.lerp(partialTick, selfPosO.x, selfPos.x),
                        Mth.lerp(partialTick, selfPosO.y, selfPos.y),
                        Mth.lerp(partialTick, selfPosO.z, selfPos.z)
                );
            } else {
                //否则直接使用原始值
                pos = new Vector3d(selfPos);
            }

            //根据玩家旋转来移动坐标
            pos.rotateY(-yRot * Mth.DEG_TO_RAD);
            //加上玩家坐标，考虑了插值
            pos.add(Mth.lerp(partialTick, entity.xo, entity.getX()),
                    Mth.lerp(partialTick, entity.yo, entity.getY()),
                    Mth.lerp(partialTick, entity.zo, entity.getZ()));
        }

        //应用坐标到相机
        camera().setPosition(pos.x, pos.y, pos.z);
    }

    private static void cleanCache() {
        selfPos.zero();
        globalPos.zero();
        rotation.zero();
        FOV = 70;
        STATE = 0;
    }

    private static boolean isStateEnabledOr(int state) {
        return (STATE & state) != 0;
    }

    private static boolean isStateEnabledAnd(int state) {
        return (STATE & state) == state;
    }

    private static boolean isOldStateEnabledOr(int state) {
        return (STATE_O & state) != 0;
    }

    private static boolean isOldStateEnabledAnd(int state) {
        return (STATE_O & state) == state;
    }

    private static Camera camera() {
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
    public static ICameraModifier createModifier(String modID, boolean high) {
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
    public static ICameraModifier createBackgroundModifier(String modID) {
        return modifiersB.computeIfAbsent(modID, Modifier::new);
    }

    public static Map<String, Modifier> getModifiersH() {
        return modifiersH;
    }

    public static Map<String, Modifier> getModifiersL() {
        return modifiersL;
    }

    public static Map<String, Modifier> getModifiersB() {
        return modifiersB;
    }

    public static List<String> getPlayerOrder() {
        return playerOrder;
    }

    public static List<String> getPlayerRemovedBackground() {
        return playerRemovedBackground;
    }

    public static class Modifier implements ICameraModifier {
        private final String modId;
        private final Vector3d pos = new Vector3d();
        private final Vector3f rot = new Vector3f();
        private double fov;
        private boolean isEffective = true;
        private int state;

        private Modifier(String modId) {
            this.modId = modId;
        }

        @Override
        public Modifier enablePos() {
            state |= POS_ENABLED;
            return this;
        }

        @Override
        public Modifier disablePos() {
            state &= ~POS_ENABLED;
            return this;
        }

        @Override
        public Modifier setPos(double x, double y, double z) {
            pos.set(x, y, z);
            return this;
        }

        @Override
        public Modifier setPos(Vector3d pos) {
            return setPos(pos.x, pos.y, pos.z);
        }

        @Override
        public Modifier addPos(double x, double y, double z) {
            pos.add(x, y, z);
            return this;
        }

        @Override
        public Modifier addPos(Vector3d pos) {
            return addPos(pos.x, pos.y, pos.z);
        }

        @Override
        public Modifier enableRotation() {
            state |= ROT_ENABLED;
            return this;
        }

        @Override
        public Modifier disableRotation() {
            state &= ~ROT_ENABLED;
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
            if (x == 90f || x == -90f) {
                x += 0.00001f;
            }

            if (y == 90f || y == -90f) {
                y += 0.00001f;
            }

            if (z == 90f || z == -90f) {
                z += 0.00001f;
            }

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
            state |= FOV_ENABLED;
            return this;
        }

        @Override
        public Modifier disableFov() {
            state &= ~FOV_ENABLED;
            return this;
        }

        @Override
        public Modifier setFov(double fov) {
            this.fov = fov;
            return this;
        }

        @Override
        public Modifier move(double x, double y, double z) {
            Vector3d vec = new Vector3d(x, y, z)
                    .rotateX(rot.x * Mth.DEG_TO_RAD)
                    .rotateY(-rot.y * Mth.DEG_TO_RAD)
                    .rotateZ(rot.z * Mth.DEG_TO_RAD);
            pos.add(vec);
            return this;
        }

        @Override
        public Modifier aimAt(double x, double y, double z) {
            Vector3d aim = new Vector3d(x - pos.x, y - pos.y, z - pos.z);

            rot.x = (float) Math.acos(Math.sqrt(aim.x * aim.x + aim.z * aim.z) / aim.length()) * Mth.RAD_TO_DEG * (aim.y < 0 ? 1 : -1);
            rot.y = (float) -(Mth.atan2(aim.x, aim.z) * Mth.RAD_TO_DEG);
            return this;
        }

        @Override
        public Vector3d getPos() {
            return pos;
        }

        @Override
        public Vector3f getRot() {
            return rot;
        }

        @Override
        public double getFov() {
            return fov;
        }

        @Override
        public Modifier enable() {
            state |= ENABLE;
            return this;
        }

        @Override
        public Modifier disable() {
            state &= ~ENABLE;
            return this;
        }

        @Override
        public Modifier enableFirstPersonArmFixed() {
            state |= FIRST_PERSON_ARM_FIXED;
            return this;
        }

        @Override
        public Modifier disableFirstPersonArmFixed() {
            state &= ~FIRST_PERSON_ARM_FIXED;
            return this;
        }

        @Override
        public Modifier enableGlobalMode() {
            state |= GLOBAL_MODE_ENABLED;
            return this;
        }

        @Override
        public Modifier disableGlobalMode() {
            state &= ~GLOBAL_MODE_ENABLED;
            return this;
        }

        @Override
        public Modifier enableLerp() {
            state |= LERP;
            return this;
        }

        @Override
        public Modifier disableLerp() {
            state &= ~LERP;
            return this;
        }

        private boolean isStateEnabledOr(int state) {
            return (this.state & state) != 0;
        }

        @Override
        public String getModId() {
            return modId;
        }
    }

    @SubscribeEvent
    public static void modifyFov(ViewportEvent.ComputeFov event) {
        if (!isStateEnabledAnd(ENABLE | FOV_ENABLED)) {
            return;
        }

        double fov;

        if (isOldStateEnabledAnd(ENABLE | FOV_ENABLED | LERP)) {
            //上次有FOV修改，需插值
            fov = Mth.lerp(event.getPartialTick(), FOV_O, FOV);
        } else {
            //无需插值，直接应用
            fov = FOV;
        }

        event.setFOV(fov);
    }

    @SubscribeEvent
    public static void modifyFirstPersonHand(RenderHandEvent event) {
        //全局模式下不能固定手臂
        if (!isStateEnabledAnd(ENABLE | FIRST_PERSON_ARM_FIXED) || isStateEnabledOr(GLOBAL_MODE_ENABLED)) {
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        float partialTick = event.getPartialTick();
        LocalPlayer player = Minecraft.getInstance().player;

        //旋转
        if (isStateEnabledOr(ROT_ENABLED)) {
            poseStack.mulPose(new Quaternionf().rotateZ(rotation.z * Mth.DEG_TO_RAD).rotateX(rotation.x * Mth.DEG_TO_RAD).rotateY(rotation.y * Mth.DEG_TO_RAD));
        }

        //坐标
        if (isStateEnabledOr(POS_ENABLED)) {
            Vector3d pos;
            //局部模式
            if (isOldStateEnabledAnd(ENABLE | POS_ENABLED | LERP)) {
                //上次开启了坐标，计算插值
                pos = new Vector3d(
                        Mth.lerp(partialTick, selfPosO.x, selfPos.x),
                        player.getEyeHeight() - Mth.lerp(partialTick, selfPosO.y, selfPos.y),
                        Mth.lerp(partialTick, selfPosO.z, selfPos.z));
            } else {
                //否则直接使用原始值
                pos = new Vector3d(selfPos.x, player.getEyeHeight() - selfPos.y, selfPos.z);
            }

            poseStack.translate(pos.x, pos.y, pos.z);
        }
    }
}
