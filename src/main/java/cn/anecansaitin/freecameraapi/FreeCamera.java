package cn.anecansaitin.freecameraapi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(FreeCamera.MODID)
public class FreeCamera {
    public static final String MODID = "freecameraapi";

    public FreeCamera(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModConf.SPEC);

        if (ModList.get().isLoaded("simple_animator")) {
            NeoForge.EVENT_BUS.addListener(ViewportEvent.ComputeFov.class, CameraForSimpleAnimator::modifyCamera);
        }
    }
}
