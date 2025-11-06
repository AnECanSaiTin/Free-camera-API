package cn.anecansaitin.zoom;

import cn.anecansaitin.freecameraapi.ClientUtil;
import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import cn.anecansaitin.freecameraapi.api.ObstacleHandler;
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
                .enableGlobalMode()
                .enableObstacle(new ObstacleHandler() {
                    @Override
                    public ObstacleResult obstacleAvoid(Vector3f position, Vector3f rotation, float[] fieldOfView) {
                        BlockHitResult result = ClientUtil.clientLevel().clipIncludingBorder(new ClipContext(new Vec3(posO), new Vec3(position), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));

                        if (result.getType() == HitResult.Type.BLOCK) {
                            Vec3 location = result.getLocation();
                            float length = 0.15f;
                            float x = (float) location.x;
                            float y = (float) location.y;
                            float z = (float) location.z;

                            switch (result.getDirection()) {
                                case DOWN -> y -= length;
                                case UP -> y += length;
                                case NORTH -> z -= length;
                                case SOUTH -> z += length;
                                case WEST -> x -= length;
                                case EAST -> x += length;
                            }

                            position.set(x, y, z);
                            return ObstacleResult.COLLIDE;
                        }

                        return ObstacleResult.NO_COLLIDE;
                    }

                    @Override
                    public void onCollision(Vector3f position, Vector3f rotation, float fov) {
                        pos.set(position);
                    }
                });
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
        instance.posO.set(instance.pos);
        instance.pos.add(instance.forward);
    }
}
