package cn.anecansaitin.freecameraapi.client;

import cn.anecansaitin.freecameraapi.FreeCamera;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ModKeyClicked {
    @SubscribeEvent
    public static void keyClick(ClientTickEvent.Post event) {
        while (ModKeyMapping.CAMERA_MODIFIER_SCREEN_KEY.get().consumeClick()) {
            Minecraft.getInstance().setScreen(new CameraModifierScreen());
        }
    }
}
