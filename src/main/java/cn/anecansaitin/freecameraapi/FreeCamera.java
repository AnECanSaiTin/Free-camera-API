package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.starup.AnnotationFinder;
import cn.anecansaitin.freecameraapi.zoom.Zoom;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "free_camera_api";

    public FreeCamera(IEventBus modEventBus, ModContainer modContainer) {
        Zoom.serverInit(modContainer);
    }
}
