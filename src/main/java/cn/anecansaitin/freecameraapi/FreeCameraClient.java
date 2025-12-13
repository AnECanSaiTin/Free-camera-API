package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.core.CameraConfig;
import cn.anecansaitin.freecameraapi.starup.AnnotationFinder;
import cn.anecansaitin.freecameraapi.zoom.Zoom;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = FreeCamera.MODID, dist = Dist.CLIENT)
public class FreeCameraClient {
    public FreeCameraClient(IEventBus modEventBus, ModContainer modContainer) {
        AnnotationFinder.clientLoading();
        modContainer.registerConfig(ModConfig.Type.CLIENT, CameraConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        Zoom.clientInit(modContainer);
    }
}
