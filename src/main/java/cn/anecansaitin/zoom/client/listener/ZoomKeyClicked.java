package cn.anecansaitin.zoom.client.listener;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.zoom.Zoom;
import cn.anecansaitin.zoom.client.gui.screen.ZoomSettingScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static cn.anecansaitin.zoom.client.ZoomKeyMapping.*;

@EventBusSubscriber(modid = FreeCamera.MODID, value = Dist.CLIENT)
public class ZoomKeyClicked {
    private static Mode mode = Mode.NONE;

    @SubscribeEvent
    public static void keyClick(ClientTickEvent.Post event) {
        while (FREE_MODE.get().consumeClick()) {
            switchMode(Mode.FREE);
        }

        if (FreeMode.ENABLED) {
            while (MOVE_MODE.get().consumeClick()) {
                FreeMode.MOVE_MODE = !FreeMode.MOVE_MODE;
            }
        } else {
            while (MOVE_MODE.get().consumeClick()) {
            }
        }

        while (SETTING.get().consumeClick()) {
            Minecraft.getInstance().setScreen(new ZoomSettingScreen());
        }

        while (FPS_PLUS_MODE.get().consumeClick()) {
            switchMode(Mode.FPS_PLUS);
        }

        if (Z_ROT_ANTICLOCKWISE.get().isDown()) {
            if (FirstPersonPlus.ENABLED) {
                FirstPersonPlus.zRot(true);
            } else if (FreeMode.ENABLED) {
                FreeMode.adjustZRot(true);
            }
        }

        if (Z_ROT_CLOCKWISE.get().isDown()) {
            if (FirstPersonPlus.ENABLED) {
                FirstPersonPlus.zRot(false);
            } else if (FreeMode.ENABLED) {
                FreeMode.adjustZRot(false);
            }
        }

        if (FOV_UP.get().isDown()) {
            if (FirstPersonPlus.ENABLED) {
                FirstPersonPlus.adjustFOV(true);
            } else if (FreeMode.ENABLED) {
                FreeMode.adjustFOV(true);
            }
        }

        if (FOV_DOWN.get().isDown()) {
            if (FirstPersonPlus.ENABLED) {
                FirstPersonPlus.adjustFOV(false);
            } else if (FreeMode.ENABLED) {
                FreeMode.adjustFOV(false);
            }
        }
    }

    private static void switchMode(Mode mode) {
        if (mode == ZoomKeyClicked.mode) {
            mode.launch.run();
            ZoomKeyClicked.mode = Mode.NONE;
        } else {
            ZoomKeyClicked.mode.launch.run();
            ZoomKeyClicked.mode = mode;
            mode.into.run();
        }
    }

    private enum Mode {
        NONE(() -> {}, () -> {}),
        FREE(() -> FreeMode.ENABLED = true, () -> FreeMode.ENABLED = false),
        FPS_PLUS(() -> FirstPersonPlus.ENABLED = true, () -> FirstPersonPlus.ENABLED = false);

        private final Runnable into;
        private final Runnable launch;

        Mode(Runnable into, Runnable launch) {
            this.into = into;
            this.launch = launch;
        }
    }
}
