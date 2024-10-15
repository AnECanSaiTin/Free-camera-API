package cn.anecansaitin.freecameraapi.client.modernui;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.client.ModKeyClicked;
import icyllis.modernui.mc.MuiModApi;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class CheckModernUI {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("modernui")) {
            ModKeyClicked.GUI_OPENER = v -> MuiModApi.openScreen(new ModernCameraModifierScreen());
        }
    }
}
