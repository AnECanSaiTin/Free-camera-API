package cn.anecansaitin.freecameraapi.client;

import cn.anecansaitin.freecameraapi.FreeCamera;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ModKeyClicked {
    public static Consumer<Void> GUI_OPENER = (a) -> Minecraft.getInstance().setScreen(new CameraModifierScreen());

    @SubscribeEvent
    public static void keyClick(ClientTickEvent.Post event) {
        while (ModKeyMapping.CAMERA_MODIFIER_SCREEN_KEY.get().consumeClick()) {
            GUI_OPENER.accept(null);
        }

        while (ModKeyMapping.SAVE_GLOBAL_CAMERA_POINT.get().consumeClick()) {

        }
    }
}
