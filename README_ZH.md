# 自由相机API

**让相机操作更简单灵活！**

![Dolly zoom](md_resource/zoom.gif)
![Roll](md_resource/roll.gif)

## 如何创建修改器

```
//创建普通修改器
//有多个修改器时仅有一个生效
//生效顺序取决于修改器id的哈希值排序，但可由玩家自主选择优先顺序
CameraModifier.createModifier("modifier id", false)

//创建背景修改器
//所有背景修改器的修改结果会叠加作用域相机
CameraModifier.createBackgroundModifier("background id")
```

## 如何使用修改器

```
//在任何你想进行相机修改的类里
//通常可以在 EventViewportEvent.ComputeFov 事件中处理

ICameraModifier modifier;

modifier
        .enable() //启用修改器
        .enablePos() //启用坐标修改
        .enableRotation() //启用旋转修改
        .enableFOV() //启用FOV修改
        .enableLer() //启用线性插值
        .setPos(1, 2, 3) //设置相机坐标为(1,2,3)，默认为以玩家坐标为中心的局部坐标
        .addPos(1, 2, 3)
        .setRotationYXZ(90f, 15f, 25f) //设置相机旋转为 (90f, 15f, 25f)
        .move(0, 0, -5) //根据当前旋转情况移动相机
        .enableFirstPersonArmFixed() //固定玩家第一人称手臂渲染，不再跟随相机移动
        .enableGlobalMode() //启用全局模式，所有坐标与选装将按照世界坐标修改，不兼容手臂固定
```