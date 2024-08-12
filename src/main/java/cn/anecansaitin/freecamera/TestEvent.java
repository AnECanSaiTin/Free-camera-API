package cn.anecansaitin.freecamera;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class TestEvent {
    public static double a;
    public static double b;

    @SubscribeEvent
    public static void e(ViewportEvent.ComputeFov event) {
        ItemStack item = Minecraft.getInstance().player.getMainHandItem();
        if (item.is(Items.BONE)) {
            a -= 0.0055;
            b += 1;

            FreeCamera.modifier
                    .enablePos()
                    .setPos(0, 1.7, a + 2)
                    .enableRotate()
                    .setRotate(0, 180, 0)
                    .enableFov()
                    .setFov(b)
                    .enable();
        } else if (item.is(Items.NAME_TAG)) {
            a = 0;
            b = -30;
            FreeCamera.modifier.disable();
        }
    }
}
