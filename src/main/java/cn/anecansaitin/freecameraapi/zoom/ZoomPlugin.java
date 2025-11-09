package cn.anecansaitin.freecameraapi.zoom;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.FreeCameraClient;
import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.ClientInput;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.joml.Vector3f;

@CameraPlugin(value = "zoom")
@EventBusSubscriber(modid = FreeCameraClient.MODID, value = Dist.CLIENT)
public class ZoomPlugin implements ICameraPlugin {
    public static ZoomPlugin instance;
    private boolean enabled = false;
    private final Vector3f forward = new Vector3f();
    private final Vector3f pos = new Vector3f();
    private final Vector3f posO = new Vector3f();
    private ICameraModifier modifier;

    @Override
    public void initialize(ICameraModifier modifier) {
        instance = this;
        this.modifier = modifier;
        modifier.disable()
                .enablePos()
                .enableFov()
                .enableGlobalMode();
    }

    @Override
    public void update() {
        float f = ClientUtil.partialTicks();
        modifier.setPos(Mth.lerp(f, posO.x, pos.x), Mth.lerp(f, posO.y, pos.y), Mth.lerp(f, posO.z, pos.z));
        modifier.setFov(ZoomConfig.Client.fov());
    }

    void enable() {
        enabled = true;
        modifier.enable();
        ClientUtil.playerEyePos(pos);
        posO.set(pos);
        ClientUtil.disableBobView();
        ClientUtil.toThirdView();
    }

    public void disable() {
        enabled = false;
        modifier.disable();
        ClientUtil.resetBobView();
        ClientUtil.resetCameraType();
    }

    @SubscribeEvent
    public static void keyPress(ClientTickEvent.Post event) {
        while (ZoomKeyMapping.ZOOM_MODE.get().consumeClick()) {
            if (instance.enabled) {
                instance.disable();
            } else {
                instance.enable();
            }
        }
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        if (!instance.enabled) {
            return;
        }

        instance.forward.zero();

        ClientInput clientInput = event.getInput();
        Input input = clientInput.keyPresses;
        instance.forward.add(input.left() ? 1 : 0, input.jump() ? 1 : 0, input.forward() ? 1 : 0);
        instance.forward.sub(input.right() ? 1 : 0, input.shift() ? 1 : 0, input.backward() ? 1 : 0);

        clientInput.moveVector = Vec2.ZERO;
        clientInput.keyPresses = Input.EMPTY;
    }

    private static boolean disabled() {
        return !instance.enabled || ClientUtil.hasScreen();
    }

    @SubscribeEvent
    public static void mouseClicking(InputEvent.MouseButton.Pre event) {
        if (disabled()) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void mouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (disabled()) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (!instance.enabled || ClientUtil.gamePaused()) {
            return;
        }

        instance.forward.rotateY(-ClientUtil.playerYHeadRot() * Mth.DEG_TO_RAD).mul(ZoomConfig.Client.speed());
        Vector3f oldPos = instance.posO;
        Vector3f newPos = instance.pos;
        oldPos.set(newPos);
        newPos.add(instance.forward);

        if (!ZoomConfig.Server.blockCollision() || oldPos.equals(newPos)) {
            return;
        }

        ClientLevel level = ClientUtil.clientLevel();
        float length = 0.1f;
        Vec3 from = new Vec3(oldPos),
                to = new Vec3(extend(oldPos, newPos, length));

        for (int i = 0; i < 3; i++) {
            BlockHitResult blockHitResult = level.clipIncludingBorder(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));

            if (blockHitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }

            Vec3 hitPoint = blockHitResult.getLocation();

            switch (blockHitResult.getDirection()) {
                case DOWN -> {
                    from = new Vec3(hitPoint.x, hitPoint.y - length, hitPoint.z);
                    to = new Vec3(extend((float) from.x, (float) from.y, (float) from.z, (float) to.x, (float) from.y, (float) to.z, length));
                }
                case UP -> {
                    from = new Vec3(hitPoint.x, hitPoint.y + length, hitPoint.z);
                    to = new Vec3(extend((float) from.x, (float) from.y, (float) from.z, (float) to.x, (float) from.y, (float) to.z, length));
                }
                case NORTH -> {
                    from = new Vec3(hitPoint.x, hitPoint.y, hitPoint.z - length);
                    to = new Vec3(extend((float) from.x, (float) from.y, (float) from.z, (float) to.x, (float) to.y, (float) from.z, length));
                }
                case SOUTH -> {
                    from = new Vec3(hitPoint.x, hitPoint.y, hitPoint.z + length);
                    to = new Vec3(extend((float) from.x, (float) from.y, (float) from.z, (float) to.x, (float) to.y, (float) from.z, length));
                }
                case WEST -> {
                    from = new Vec3(hitPoint.x - length, hitPoint.y, hitPoint.z);
                    to = new Vec3(extend((float) from.x, (float) from.y, (float) from.z, (float) from.x, (float) to.y, (float) to.z, length));
                }
                case EAST -> {
                    from = new Vec3(hitPoint.x + length, hitPoint.y, hitPoint.z);
                    to = new Vec3(extend((float) from.x, (float) from.y, (float) from.z, (float) from.x, (float) to.y, (float) to.z, length));
                }
            }

            if (Double.isNaN(to.x)) {
                newPos.set(from.x, from.y, from.z);
                return;
            }

            newPos.set(to.x, to.y, to.z);
        }
    }

    private static Vector3f extend(Vector3f from, Vector3f to, float length) {
        return new Vector3f(to).sub(from).normalize(length).add(to);
    }

    private static Vector3f extend(float x1, float y1, float z1, float x2, float y2, float z2, float length) {
        return new Vector3f(x2, y2, z2).sub(x1, y1, z1).normalize(length).add(x2, y2, z2);
    }

    @SubscribeEvent
    public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        instance.disable();
    }

    public static boolean enabled() {
        return instance.enabled;
    }
}
