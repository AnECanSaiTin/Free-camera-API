package cn.anecansaitin.freecameraapi.api.extension;

public sealed interface ControlScheme permits ControlScheme.PLAYER_RELATIVE, ControlScheme.CAMERA_RELATIVE, ControlScheme.VANILLA {
    VANILLA VANILLA = new VANILLA();
    CAMERA_RELATIVE CAMERA_RELATIVE = new CAMERA_RELATIVE();
    static PLAYER_RELATIVE PLAYER_RELATIVE(int angle) {
        return new PLAYER_RELATIVE(angle);
    }

    record VANILLA() implements ControlScheme {}
    record CAMERA_RELATIVE() implements ControlScheme {}
    record PLAYER_RELATIVE(int angle) implements ControlScheme {}
}
