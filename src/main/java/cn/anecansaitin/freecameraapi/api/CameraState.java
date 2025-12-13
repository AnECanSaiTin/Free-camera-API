package cn.anecansaitin.freecameraapi.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/// This annotation is used to mark classes that define extended camera states.
///
/// Developers should add this annotation to their class and declare the states within the class.
///
/// This ensures that the states are assigned values at the correct time.
///
/// ```
/// @CameraState
/// public class MyState {
///     public static final int MY_STATE = CameraStates.nextState("my_state");
/// }
/// ```
@Target(ElementType.TYPE)
public @interface CameraState {
}
