package cn.anecansaitin.freecameraapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.api.animation.AnimationState;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import org.joml.Vector3f;

class CameraForSimpleAnimator {
    private static CameraModifier.Modifier MODIFIER = CameraModifier.createModifier("freecameraapi_simple_animator", false);

    public static void modifyCamera(ViewportEvent.ComputeFov event) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientAnimator animator = (ClientAnimator) ((IAnimateHandler) player).simpleanimator$getAnimator();

        if (animator.getCurState() == AnimationState.IDLE) {
            MODIFIER.disable();
            return;
        }

        VariableHolder pos = animator.getVariable("cameraPos");

        if (pos == VariableHolder.Immutable.INSTANCE) {
            MODIFIER.disablePos();
        } else {
            Vector3f posVec = pos.getAsVector3f().mul(0.0625f);
            MODIFIER.enable()
                    .enablePos()
                    .setPos(posVec.x, posVec.y, -posVec.z);
        }

        VariableHolder rot = animator.getVariable("cameraRot");

        if (rot == VariableHolder.Immutable.INSTANCE) {
            MODIFIER.disableRotation();
        } else {
            MODIFIER.enable()
                    .enableRotation()
                    .setRotationZYX(rot.getAsVector3f());
        }

        VariableHolder zoom = animator.getVariable("cameraZoom");

        if (zoom == VariableHolder.Immutable.INSTANCE) {
            MODIFIER.disableFov();
        } else {
            MODIFIER.enable()
                    .enableFov()
                    .setFov((zoom.get() - 1) * 100);
        }
    }
}
