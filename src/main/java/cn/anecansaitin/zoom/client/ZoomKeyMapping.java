package cn.anecansaitin.zoom.client;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.zoom.Zoom;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ZoomKeyMapping {
    public static final Lazy<KeyMapping> FREE_MODE = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".free_mode",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> MOVE_MODE = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".move_mode",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> SETTING = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".setting",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> FOV_UP = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".fov_up",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> FOV_DOWN = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".fov_down",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> FPS_PLUS_MODE = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".fps_plus_mode",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> TURN_HEAD = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".turn_head",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> Z_ROT_ANTICLOCKWISE = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".z_rot_anticlockwise",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    public static final Lazy<KeyMapping> Z_ROT_CLOCKWISE = Lazy.of(() ->
            new KeyMapping(
                    "key." + Zoom.MODID + ".z_rot_clockwise",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "key.categories." + Zoom.MODID
            ));

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(FREE_MODE.get());
        event.register(MOVE_MODE.get());
        event.register(SETTING.get());
        event.register(FOV_UP.get());
        event.register(FOV_DOWN.get());
        event.register(FPS_PLUS_MODE.get());
        event.register(TURN_HEAD.get());
        event.register(Z_ROT_ANTICLOCKWISE.get());
        event.register(Z_ROT_CLOCKWISE.get());
    }
}
