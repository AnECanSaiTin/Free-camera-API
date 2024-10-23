package cn.anecansaitin.freecameraapi;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.api.animation.AnimationState;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import org.joml.Vector2f;
import org.joml.Vector3f;

class CameraForSimpleAnimator {
    private static final ICameraModifier MODIFIER = CameraModifierManager.createModifier("freecameraapi_simple_animator", false);

    public static void modifyCamera(ViewportEvent.ComputeFov event) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientAnimator animator = (ClientAnimator) ((IAnimateHandler) player).simpleanimator$getAnimator();

        if (animator.getCurState() != AnimationState.LOOP || !(animator.hasVariable("tCameraRot") || animator.hasVariable("fCameraRot"))) {
            MODIFIER.disable();
            return;
        }

        player.setYBodyRot(player.getYHeadRot());
        Options options = Minecraft.getInstance().options;
        String prefix = "t";

        if (animator.hasVariable("mandatory")) {
            Vector2f mandatory = animator.getVariable("mandatory").getAsVector2f();

            //相机人称强制模式开启
            if (mandatory.x > 0) {
                if (mandatory.y > 0 && options.getCameraType() == CameraType.FIRST_PERSON) {
                    //锁定第三人称
                    options.setCameraType(CameraType.THIRD_PERSON_BACK);
                } else if (mandatory.y < 0 && options.getCameraType() != CameraType.FIRST_PERSON) {
                    //锁定第一人称
                    options.setCameraType(CameraType.FIRST_PERSON);
                    prefix = "f";
                }
            }
        } else if (options.getCameraType() == CameraType.FIRST_PERSON) {
            prefix = "f";
        }

        VariableHolder pos = animator.getVariable(prefix + "CameraPos");

        if (pos == VariableHolder.Immutable.INSTANCE) {
            MODIFIER.disablePos();
        } else {
            Vector3f posVec = pos.getAsVector3f().mul(0.0625f);
            MODIFIER.enable()
                    .enablePos()
                    .setPos(posVec.x, posVec.y, -posVec.z);
        }

        VariableHolder rot = animator.getVariable(prefix + "CameraRot");

        if (rot == VariableHolder.Immutable.INSTANCE) {
            MODIFIER.disableRotation();
        } else {
            MODIFIER.enable()
                    .enableRotation()
                    .setRotationZYX(rot.getAsVector3f());
        }

        VariableHolder zoom = animator.getVariable(prefix + "CameraZoom");

        if (zoom == VariableHolder.Immutable.INSTANCE) {
            MODIFIER.disableFov();
        } else {
            MODIFIER.enable()
                    .enableFov()
                    .setFov((zoom.get() - 1) * 100 + 70);
        }
    }
}
