package cn.anecansaitin.freecameraapi.api;

/// IPlugin must have the {@link CameraPlugin} annotation to get loaded by FreeCameraAPI.
///
/// Example :{@link cn.anecansaitin.freecameraapi.ExamplePlugin}
public interface ICameraPlugin {
    void initialize(ICameraModifier modifier);

    void update();
}
