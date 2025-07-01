package cn.anecansaitin.freecameraapi.api.extension;

public sealed interface ControlScheme permits ControlScheme.PlayerRelative, ControlScheme.CameraRelative, ControlScheme.Vanilla {
    Vanilla VANILLA = new Vanilla();
    CameraRelative CAMERA_RELATIVE = new CameraRelative();
    static PlayerRelative PLAYER_RELATIVE(int angle) {
        return new PlayerRelative(angle);
    }

    final class Vanilla implements ControlScheme {}
    final class CameraRelative implements ControlScheme {}
    record PlayerRelative(int angle) implements ControlScheme {}
}
