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

        if (item.is(Items.NAME_TAG)) {
//            b -= 0.1;
            FreeCamera.modifier
                    .enable()
                    .enablePos()
                    .setPos(0, 74.6, 48)
                    .enableRotate()
                    .setRotate(0, -90, 0)
                    .enableFirstPersonArmFixed()
                    .enableGlobalMode();
        } else if (item.is(Items.DIAMOND)) {
            b += 0.1;
            FreeCamera.modifier
                    .enable()
                    .enablePos()
                    .setPos(3, 0, 2)
                    .enableRotate()
                    .setRotate(0, (float) b, 0)
                    .disableFirstPersonArmFixed()
                    .disableGlobalMode();
        } else if (item.is(Items.BONE)) {
            b = 0;
            FreeCamera.modifier.disable().disableGlobalMode();
        }
    }
}
