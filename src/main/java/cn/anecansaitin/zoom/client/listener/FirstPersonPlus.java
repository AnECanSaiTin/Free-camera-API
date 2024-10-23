package cn.anecansaitin.zoom.client.listener;

import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import cn.anecansaitin.zoom.Zoom;
import cn.anecansaitin.zoom.client.ZoomKeyMapping;
import cn.anecansaitin.zoom.client.event.MouseMoveEvent;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class FirstPersonPlus {
    public static boolean ENABLED;
    private static final Vector3f pos = new Vector3f();
    private static final Vector3f rot = new Vector3f();
    private static float fov = 70;
    private static boolean enabledO;
    private static boolean turnHeadO;
    private static final ICameraModifier modifier = CameraModifierManager.createModifier(Zoom.MODID, false);

    @SubscribeEvent
    public static void computeFov(ViewportEvent.ComputeFov event) {
        if (!ENABLED || !enabledO || Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }

        fov = Mth.clamp(fov, 30, 110);
        rot.x = Mth.clamp(rot.x, -90, 90);
        rot.y = Mth.clamp(rot.y, -180, 180);
        rot.z = Mth.clamp(rot.z, -180, 180);
        modifier.setRotationYXZ(rot)
                .setPos(pos.x, pos.y, pos.z)
                .setFov(fov);
    }

    @SubscribeEvent
    public static void mouseMove(MouseMoveEvent event) {
        Minecraft mc = Minecraft.getInstance();
        float partialTick = mc.gameRenderer.getMainCamera().getPartialTickTime();
        LocalPlayer player = mc.player;

        if (ENABLED && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            Vec3 eyePosition = player.getEyePosition(partialTick);
            Vec3 position = player.getPosition(partialTick);
            pos.set(eyePosition.x - position.x, eyePosition.y - position.y, eyePosition.z - position.z);

            if (ZoomKeyMapping.TURN_HEAD.get().isDown()) {
                //转头
                if (!turnHeadO) {
                    modifier.enableFirstPersonArmFixed();
                    turnHeadO = true;
                }

                rot.add((float) event.getX() * 0.15F, (float) event.getY() * 0.15F, 0);
                event.setCanceled(true);
            } else {
                if (turnHeadO) {
                    modifier.disableFirstPersonArmFixed();
                    turnHeadO = false;
                }

                rot.set(player.getViewXRot(partialTick), 0, rot.z);
            }

            if (!enabledO) {
                initializeCamera();
            }
        } else if (enabledO) {
            resetCamera();
        }
    }

    private static void initializeCamera() {
        modifier.enable()
                .enablePos()
                .enableRotation()
                .enableFov()
                .enableLerp();
        enabledO = true;
        modifier.setPos(pos.x, pos.y, pos.z)
                .setRotationYXZ(rot)
                .setFov(fov);
    }

    private static void resetCamera() {
        modifier.reset();
        rot.zero();
        pos.zero();
        fov = 70;
        enabledO = false;
        turnHeadO = false;
    }

    public static void zRot(boolean up) {
        rot.z += up ? -1 : 1;
    }

    public static float getZRot() {
        return rot.z;
    }

    public static void adjustFOV(boolean up) {
        fov += up ? 1 : -1;
    }

    public static float getFov() {
        return fov;
    }
}
