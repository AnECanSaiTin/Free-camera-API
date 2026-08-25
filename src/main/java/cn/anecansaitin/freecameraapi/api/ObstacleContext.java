package cn.anecansaitin.freecameraapi.api;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/// Carries obstacle-avoidance state between [ModifierManager] and [ObstacleHandler].
///
/// Manager populates `pos` / `rot` / `fov` before calling [ObstacleHandler.obstacleAvoid];
/// handler may overwrite them via `setPos` / `setRot` / `setFov` when returning COLLIDE or NO_COLLIDE.
/// Manager then reads the (possibly modified) values back.
///
/// The `enabledMask` field tells the handler which camera components (POS_X/Y/Z, ROT_X/Y/Z, FOV)
/// are active, so a handler can choose to only adjust enabled components.
public final class ObstacleContext {
    private final Vector3f pos = new Vector3f();
    private final Vector3f rot = new Vector3f();
    private float fov;
    private int enabledMask;

    /// Read-only view of the current camera position.
    public Vector3fc pos() { return pos; }

    /// Read-only view of the current camera rotation (degrees, YXZ order).
    public Vector3fc rot() { return rot; }

    /// Current camera FOV.
    public float fov() { return fov; }

    /// Bitmask of currently enabled [CameraStates] component bits
    /// (POS_X | POS_Y | POS_Z | ROT_X | ROT_Y | ROT_Z | FOV).
    public int enabledMask() { return enabledMask; }

    /// Replaces the camera position.
    public void setPos(Vector3fc pos) { this.pos.set(pos); }
    public void setPos(float x, float y, float z) { this.pos.set(x, y, z); }

    /// Replaces the camera rotation.
    public void setRot(Vector3fc rot) { this.rot.set(rot); }
    public void setRot(float x, float y, float z) { this.rot.set(x, y, z); }

    /// Replaces the camera FOV.
    public void setFov(float fov) { this.fov = fov; }

    // --- internal: ModifierManager populates these ---

    public void pos(Vector3f src) { pos.set(src); }
    public void rot(Vector3f src) { rot.set(src); }
    public void fov(float v) { fov = v; }
    public void enabledMask(int mask) { enabledMask = mask; }

    // --- internal: ModifierManager reads back ---

    public void copyTo(Vector3f destPos, Vector3f destRot) {
        destPos.set(pos);
        destRot.set(rot);
    }
}
