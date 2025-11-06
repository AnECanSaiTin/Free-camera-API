package cn.anecansaitin.zoom;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import cn.anecansaitin.freecameraapi.api.ObstacleHandler;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.joml.Vector3f;

@CameraPlugin(value = "zoom")
@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ZoomPlugin implements ICameraPlugin {
    private static ZoomPlugin instance;
    private boolean enabled = false;
    private final Vector3f forward = new Vector3f();
    private final Vector3f pos = new Vector3f();
    private final Vector3f posO = new Vector3f();
    private float fov = 0;
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
        modifier.setFov(fov);
    }

    private void enable() {
        enabled = true;
        modifier.enable();
        ClientUtil.playerEyePos(pos);
        posO.set(pos);
        fov = ClientUtil.fov();
        ClientUtil.disableBobView();
        ClientUtil.toThirdView();
    }

    private void disable() {
        enabled = false;
        modifier.disable();
        ClientUtil.resetBobView();
        ClientUtil.resetCameraType();
    }

    @SubscribeEvent
    public static void keyPress(ClientTickEvent.Post event) {
        while (ModKeyMapping.ZOOM_MODE.get().consumeClick()) {
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

    @SubscribeEvent
    public static void mouseInput(InputEvent.MouseButton.Pre event) {
        if (!instance.enabled) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (!instance.enabled) {
            return;
        }

        instance.forward.rotateY(-ClientUtil.playerYHeadRot() * Mth.DEG_TO_RAD).mul(0.4f);
        Vector3f oldPos = instance.posO;
        Vector3f newPos = instance.pos;
        oldPos.set(newPos);
        newPos.add(instance.forward);

        if (oldPos.equals(newPos)) {
            return;
        }

        ClientLevel level = ClientUtil.clientLevel();
        float length = 0.1f;
        Vec3 from = new Vec3(oldPos),
                to = new Vec3(newPos);// todo 每次设置newPos都要额外在其方向上添加一个length长度

        for (int i = 0; i < 2; i++) {
            BlockHitResult blockHitResult = level.clipIncludingBorder(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));

            if (blockHitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }

            Vec3 hitPoint = blockHitResult.getLocation();

            switch (blockHitResult.getDirection()) {
                case DOWN -> {
                    from = new Vec3(hitPoint.x, hitPoint.y - length, hitPoint.z);
                    to = new Vec3(to.x, hitPoint.y - length, to.z);
                }
                case UP -> {
                    from = new Vec3(hitPoint.x, hitPoint.y + length, hitPoint.z);
                    to = new Vec3(to.x, hitPoint.y + length, to.z);
                }
                case NORTH -> {
                    from = new Vec3(hitPoint.x, hitPoint.y, hitPoint.z - length);
                    to = new Vec3(to.x, to.y, hitPoint.z - length);
                }
                case SOUTH -> {
                    from = new Vec3(hitPoint.x, hitPoint.y, hitPoint.z + length);
                    to = new Vec3(to.x, to.y, hitPoint.z + length);
                }
                case WEST -> {
                    from = new Vec3(hitPoint.x - length, hitPoint.y, hitPoint.z);
                    to = new Vec3(hitPoint.x - length, to.y, to.z);
                }
                case EAST -> {
                    from = new Vec3(hitPoint.x + length, hitPoint.y, hitPoint.z);
                    to = new Vec3(hitPoint.x + length, to.y, to.z);
                }
            }

            newPos.set(to.x, to.y, to.z);
        }
    }
}
