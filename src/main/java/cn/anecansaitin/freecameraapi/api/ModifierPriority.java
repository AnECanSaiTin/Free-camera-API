package cn.anecansaitin.freecameraapi.api;

/// Controls execution order and active-modifier selection.
///
/// <p>Higher priorities execute first in two contexts:
/// <ul>
///   <li>{@code updateController} — plugins are ticked in priority order (HIGHEST first).</li>
///   <li>{@code getActiveModifier} — the first modifier whose {@link CameraModifier#isActive}
///       returns true (in priority order) becomes the active modifier for the current frame.</li>
/// </ul>
///
/// <p>Within the same priority level, registration order is preserved.
public enum ModifierPriority {
    /// First to execute; first candidate for active modifier.
    HIGHEST,
    HIGH,
    /// Default priority.
    NORMAL,
    LOW,
    /// Last to execute.
    LOWEST
}