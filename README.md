# Free camera API
[![Static Badge](https://img.shields.io/badge/%E4%B8%AD%E6%96%87-brightgreen)](README_ZH.md)

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/AnECanSaiTin/Free-camera-API)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/KCR6bnbY?logo=modrinth)](https://modrinth.com/mod/free-camera-api)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1091599?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/free-camera-api)

![logo](https://cdn.modrinth.com/data/KCR6bnbY/images/41d032a0a6bcfcf92ae30d7c2fa8d68f92ad752c.png)

**Make camera operation easier and more flexible.**
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

![Dolly zoom](https://cdn.modrinth.com/data/KCR6bnbY/images/0fd9ede2dfc1818fbb4638bbbf3bd6a0598df4bd.gif)
![Roll](https://cdn.modrinth.com/data/KCR6bnbY/images/ee51dcdae13dc4714e8f6f1faa7a5e127b2abba1.gif)
