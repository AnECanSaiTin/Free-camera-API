package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.zoom.client.event.MouseMoveEvent;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @WrapWithCondition(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    public boolean freecameraapi$$onMove(LocalPlayer instance, double yRot, double xRot) {
        MouseMoveEvent event = new MouseMoveEvent(xRot, yRot);
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }
}