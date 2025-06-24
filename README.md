# Free camera API
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/AnECanSaiTin/Free-camera-API)

[中文](README_ZH.md)

**Make camera operation easier and more flexible.**

![Dolly zoom](md_resource/zoom.gif)
![Roll](md_resource/roll.gif)

## How to Register and Use the Plugin

Full example: [ExamplePlugin](src/main/java/cn/anecansaitin/freecameraapi/ExamplePlugin.java).

1. Create the plugin class
    ```
    @CameraPlugin(value = "example", priority = ModifierPriority.LOWEST)
    public class ExamplePlugin implements ICameraPlugin {
        // ...
    }
    ```
   value: Plugin unique identifier (string)  
   priority: Priority setting (ModifierPriority.HIGH/LOWEST etc.)

2. Initialization
    ```
    @Override
    public void initialize(ICameraModifier modifier) {
        this.modifier = modifier;
        modifier.enable();
    }
    ```

3. Update camera data every frame
    ```
    modifier
                .enablePos() // Enable position modification
                .enableRotation() // Enable rotation modification
                .enableFov() // Enable FOV modification
                .setPos(1, 2, 3) // Set camera position to (1,2,3), default is local coordinates centered on player
                .addPos(1, 2, 3)
                .setRotationYXZ(90f, 15f, 25f) // Set camera rotation to (90f, 15f, 25f)
                .move(0, 0, -5) // Move camera based on current rotation
                .enableGlobalMode() // Enable global mode, all coordinates and rotations will be modified according to world coordinates
                .enableChunkLoader() // Enable chunk loader, load chunks around the camera. An additional Free Camera API Addition mod needs to be installed to enable this feature, otherwise it will have no effect
                .enableObstacle() // Enable collision detection
    ```

