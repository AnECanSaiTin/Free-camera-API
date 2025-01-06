package cn.anecansaitin.freecameraapi.common;

import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class TestListener {
    private static final ICameraModifier modifier = CameraModifierManager
            .createModifier("test", true)
            .enablePos()
            .enableFov()
            .enableRotation();

    @SubscribeEvent
    public static void on(ViewportEvent.ComputeFov event) {
        int i = 10;
    }
}
