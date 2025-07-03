package cn.anecansaitin.freecameraapi.api.extension;

public sealed interface ControlScheme permits
        ControlScheme.PLAYER_RELATIVE,
        ControlScheme.CAMERA_RELATIVE,
        ControlScheme.VANILLA,
        ControlScheme.PLAYER_RELATIVE_STRAFE {
    VANILLA VANILLA = new VANILLA();
    CAMERA_RELATIVE CAMERA_RELATIVE = new CAMERA_RELATIVE();
    PLAYER_RELATIVE_STRAFE PLAYER_RELATIVE_STRAFE = new PLAYER_RELATIVE_STRAFE();

    static PLAYER_RELATIVE PLAYER_RELATIVE(int angle) {
        return new PLAYER_RELATIVE(angle);
    }

    record VANILLA() implements ControlScheme {}

    record CAMERA_RELATIVE() implements ControlScheme {}

    record PLAYER_RELATIVE(int angle) implements ControlScheme {}

    record PLAYER_RELATIVE_STRAFE() implements ControlScheme {}
}
