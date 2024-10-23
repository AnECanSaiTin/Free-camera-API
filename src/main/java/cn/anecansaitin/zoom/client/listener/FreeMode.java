package cn.anecansaitin.zoom.client.listener;

import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import cn.anecansaitin.zoom.Zoom;
import cn.anecansaitin.zoom.client.event.MouseMoveEvent;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Vector3d;
import org.joml.Vector3f;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class FreeMode {
    public static boolean ENABLED;
    public static boolean MOVE_MODE;
    private static float fov = 70;
    private static final Vector3f pos = new Vector3f(0, 3, 0);
    private static final Vector3f rot = new Vector3f();
    public static float SPEED = 0.2f;
    private static CameraType typeCache = CameraType.FIRST_PERSON;
    private static boolean check = false;
    public static boolean forward = false;
    public static boolean left = false;
    public static boolean right = false;
    public static boolean back = false;
    public static boolean jump = false;
    public static boolean shift = false;
    private static final ICameraModifier modifier = CameraModifierManager.createModifier(Zoom.MODID, false);

    @SubscribeEvent
    public static void computeFov(ViewportEvent.ComputeFov event) {
        if (!ENABLED || !check) {
            return;
        }

        fov = Mth.clamp(fov, 30, 110);
        modifier.setRotationYXZ(rot)
                .setFov(fov);
        Vector3f move = new Vector3f();

        if (forward != back) {
            move.z = forward ? 1 : -1;
        }

        if (left != right) {
            move.x = left ? 1 : -1;
        }

        if (jump != shift) {
            move.y = jump ? 1 : -1;
        }

        move.mul(SPEED);
        int distance = 16 * (Minecraft.getInstance().options.renderDistance().get() - 2);

        if (MOVE_MODE) {
            //向准心位置移动
            modifier.move(move.x, 0, move.z);
            Vector3d modifierPos = modifier.getPos();

            if (modifierPos.x > distance) {
                modifierPos.x = distance;
            } else if (modifierPos.x < -distance) {
                modifierPos.x = -distance;
            }

            if (modifierPos.z > distance) {
                modifierPos.z = distance;
            } else if (modifierPos.z < -distance) {
                modifierPos.z = -distance;
            }

            modifier.addPos(0, move.y, 0);
            pos.set(modifierPos);
        } else {
            //类创造模式移动
            move.rotateY(-rot.y * Mth.DEG_TO_RAD);
            pos.add(move);

            if (pos.x > distance) {
                pos.x = distance;
            } else if (pos.x < -distance) {
                pos.x = -distance;
            }

            if (pos.z > distance) {
                pos.z = distance;
            } else if (pos.z < -distance) {
                pos.z = -distance;
            }

            modifier.setPos(pos.x, pos.y, pos.z);
        }
    }

    @SubscribeEvent
    public static void mouseMove(MouseMoveEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;

        if (ENABLED) {
            if (!check) {
                //初始化
                float partial = Minecraft.getInstance().gameRenderer.getMainCamera().getPartialTickTime();
                Vec3 viewVector = player.getViewVector(partial);
                rot.set(viewVector.x, viewVector.y, 0);
                Vec3 eyePosition = player.getEyePosition();
                Vec3 position = player.getPosition(partial);
                pos.set(eyePosition.x - position.x, eyePosition.y - position.y, eyePosition.z - position.z);
                check = true;
                modifier.enable()
                        .enableRotation()
                        .enablePos()
                        .enableFov()
                        .enableLerp();
                typeCache = options.getCameraType();
                options.setCameraType(CameraType.THIRD_PERSON_BACK);
            }

            //普通执行
            rot.add((float) event.getX() * 0.15F, (float) event.getY() * 0.15F, 0);
            rot.set(Mth.clamp(rot.x, -90.0F, 90.0F), rot.y, rot.z);
            event.setCanceled(true);
        } else if (check) {
            //复位
            rot.zero();
            pos.zero();
            check = false;
            modifier.reset();
            options.setCameraType(typeCache);
            forward = false;
            left = false;
            right = false;
            back = false;
            jump = false;
            shift = false;
            MOVE_MODE = false;
            fov = 70;
        }
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        Input input = event.getInput();

        if (ENABLED) {
            FreeMode.forward = input.up;
            FreeMode.back = input.down;
            FreeMode.left = input.left;
            FreeMode.right = input.right;
            FreeMode.jump = input.jumping;
            FreeMode.shift = input.shiftKeyDown;

            input.forwardImpulse = 0;
            input.leftImpulse = 0;
            input.jumping = false;
            input.shiftKeyDown = false;
        }
    }

    @SubscribeEvent
    public static void mouseScroll(InputEvent.MouseScrollingEvent event) {
        //开启时，滚轮变为速度切换
        if (!ENABLED) {
            return;
        }

        event.setCanceled(true);
        SPEED = Mth.clamp(SPEED + (float) (event.getScrollDeltaY() * 0.01f), 0.01f, 0.7f);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        //屏蔽切换视角
        if (!ENABLED) {
            return;
        }

        KeyMapping key = Minecraft.getInstance().options.keyTogglePerspective;

        while (key.consumeClick()) {
        }
        key.setDown(true);
    }

    public static void adjustZRot(boolean anticlockwise) {
        rot.z += anticlockwise ? -1 : 1;
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
