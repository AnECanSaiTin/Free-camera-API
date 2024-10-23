package cn.anecansaitin.zoom.client.gui.overlay;

import cn.anecansaitin.zoom.client.ZoomKeyMapping;
import cn.anecansaitin.zoom.client.listener.FirstPersonPlus;
import cn.anecansaitin.zoom.client.listener.FreeMode;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

public class ZoomOverlay implements LayeredDraw.Layer {
    public static boolean FREE_MODE_ENABLED;
    public static int FREE_MODE_OFFSET_X;
    public static int FREE_MODE_OFFSET_Y;
    public static boolean FPS_PLUS_ENABLED;
    public static int FPS_PLUS_OFFSET_X;
    public static int FPS_PLUS_OFFSET_Y;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        renderFreeMode(guiGraphics);
        renderFPSPlus(guiGraphics);
    }

    private void renderFreeMode(GuiGraphics guiGraphics) {
        if (!FreeMode.ENABLED || !FREE_MODE_ENABLED) {
            return;
        }

        int x = FREE_MODE_OFFSET_X;
        int y = FREE_MODE_OFFSET_Y;
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.free_mode_overlay.mode_on", ZoomKeyMapping.FREE_MODE.get().getKey().getDisplayName()), x, y, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.free_mode_overlay.move_mode", FreeMode.MOVE_MODE ? Component.translatable("gui.free_mode_overlay.move_mode.cross_hair") : Component.translatable("gui.free_mode_overlay.move_mode.normal"), ZoomKeyMapping.MOVE_MODE.get().getKey().getDisplayName()), x, y + 10, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.free_mode_overlay.speed", String.format("%.2f", FreeMode.SPEED).substring(2)), x, y + 20, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.common_overlay.fov", String.format("%.0f", FreeMode.getFov()), ZoomKeyMapping.FOV_UP.get().getKey().getDisplayName(), ZoomKeyMapping.FOV_DOWN.get().getKey().getDisplayName()), x, y + 30, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.common_overlay.z_rot", String.format("%.0f", FreeMode.getZRot()), ZoomKeyMapping.Z_ROT_CLOCKWISE.get().getKey().getDisplayName(), ZoomKeyMapping.Z_ROT_ANTICLOCKWISE.get().getKey().getDisplayName()), x, y + 40, 0xFFFFFF);
    }

    private void renderFPSPlus(GuiGraphics guiGraphics) {
        if (!FirstPersonPlus.ENABLED || !FPS_PLUS_ENABLED || Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }

        int x = FPS_PLUS_OFFSET_X;
        int y = FPS_PLUS_OFFSET_Y;
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.fps_mode_overlay.mode_on", ZoomKeyMapping.FPS_PLUS_MODE.get().getKey().getDisplayName()), x, y, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.fps_mode_overlay.turn_head", ZoomKeyMapping.TURN_HEAD.get().getKey().getDisplayName()), x, y + 10, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.common_overlay.fov", String.format("%.0f", FirstPersonPlus.getFov()), ZoomKeyMapping.FOV_UP.get().getKey().getDisplayName(), ZoomKeyMapping.FOV_DOWN.get().getKey().getDisplayName()), x, y + 20, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.common_overlay.z_rot", String.format("%.0f", FirstPersonPlus.getZRot()), ZoomKeyMapping.Z_ROT_CLOCKWISE.get().getKey().getDisplayName(), ZoomKeyMapping.Z_ROT_ANTICLOCKWISE.get().getKey().getDisplayName()), x, y + 30, 0xFFFFFF);
    }
}
