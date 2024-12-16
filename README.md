# Free camera API
[中文](README_ZH.md)

**Make camera operation easier and more flexible.**
![Dolly zoom](md_resource/zoom.gif)
![Roll](md_resource/roll.gif)
## How to create modifier

```
//Create a common modifier
//Only one modifier will be used
//The order is determined by the hash value, or it can be determined by the player
CameraModifier.createModifier("modifier id", false)

//Create a background modifier
//The results of all modifiers will be applied to the camera
CameraModifier.createBackgroundModifier("background id")
```
## How to use modifier

```
//in some class you want to modify camera
//Suggest making modifications by subscribing to eventViewportEvent.ComputeFov

ICameraModifier modifier;

modifier
        .enable() //Enable modifier
        .enablePos() //Enable pos
        .enableRotation() //Enable rotation
        .enableFOV() //Enable FOV
        .enableLer() //Enable default linear interpolation for pos, rotation, fov
        .setPos(1, 2, 3) //Set camera pos at (1, 2, 3). Take the player's coordinates as the origin
        .addPos(1, 2, 3)
        .setRotationYXZ(90f, 15f, 25f) //Set camera rotation at (90f, 15f, 25f)
        .move(0, 0, -5) //Move the pos according to the current rotation
        .enableFirstPersonArmFixed() //The first person arm will not follow the camera
        .enableGlobalMode() //Pos and rotations will no longer be in the player's local coordinate system, but rather in the world coordinate system.
                            //This mode not support FirstPersonArmFixed
```

