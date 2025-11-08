package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.zoom.Zoom;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = FreeCameraClient.MODID)
public class FreeCameraCommon {
    public FreeCameraCommon(IEventBus modEventBus, ModContainer modContainer) {
        Zoom.serverInit(modContainer);
    }
}
