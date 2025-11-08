package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.core.CameraConfig;
import cn.anecansaitin.freecameraapi.starup.PluginFinder;
import cn.anecansaitin.freecameraapi.zoom.Zoom;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = FreeCameraClient.MODID, dist = Dist.CLIENT)
public class FreeCameraClient {
    public static final String MODID = "free_camera_api";

    public FreeCameraClient(IEventBus modEventBus, ModContainer modContainer) {
        PluginFinder.loadPlugin();
        modContainer.registerConfig(ModConfig.Type.CLIENT, CameraConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        Zoom.clientInit(modContainer);
    }
}
