package cn.anecansaitin.freecameraapi.api;

/// Handles obstacle avoidance for camera position.
///
/// Implementations can either:
/// <ul>
///   <li>Fully handle avoidance in {@link #obstacleAvoid} by mutating the [ObstacleContext]
///       and returning COLLIDE or NO_COLLIDE.</li>
///   <li>Return PASS to let the default algorithm run, then receive the post-collision
///       state in {@link #onCollision}.</li>
/// </ul>
///
/// Use {@link #DEFAULT} when you only want the built-in avoidance without a custom callback.
@FunctionalInterface
public interface ObstacleHandler {

    /// Uses the built-in obstacle avoidance algorithm with an empty collision callback.
    /// Equivalent to {@code enableObstacle()} without a custom handler.
    ObstacleHandler DEFAULT = ctx -> { };

    /// Custom obstacle avoidance behavior.
    ///
    /// The handler may inspect the current pos/rot/fov via [ObstacleContext] and overwrite
    /// them if needed. When returning COLLIDE or NO_COLLIDE, the manager uses the context's
    /// values as the final camera state. Returning PASS falls back to the built-in algorithm.
    ///
    /// @param ctx Mutable obstacle context. Read via `ctx.pos()` etc.; write via `ctx.setPos()` etc.
    /// @return COLLIDE / NO_COLLIDE if fully handled, PASS to fall back.
    default ObstacleResult obstacleAvoid(ObstacleContext ctx) {
        return ObstacleResult.PASS;
    }

    /// Called after collision resolution (either by the handler or the built-in algorithm).
    ///
    /// @param ctx Final obstacle context after avoidance (read-only).
    void onCollision(ObstacleContext ctx);

    enum ObstacleResult {
        /// Fall back to built-in obstacle avoidance.
        PASS,
        /// Handler resolved a collision; ctx values are the final camera state.
        COLLIDE,
        /// Handler determined no collision; ctx values are the final camera state.
        NO_COLLIDE
    }
}
