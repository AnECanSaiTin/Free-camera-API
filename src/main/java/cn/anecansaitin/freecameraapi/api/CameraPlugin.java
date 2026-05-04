package cn.anecansaitin.freecameraapi.api;

/// ICameraPlugin must have the {@link Plugin} annotation to get loaded by FreeCameraAPI.
///
/// Example :{@link cn.anecansaitin.freecameraapi.zoom.ZoomPlugin}
public interface CameraPlugin {
    void update(float partialTicks);
}
