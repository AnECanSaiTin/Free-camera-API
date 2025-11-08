package cn.anecansaitin.freecameraapi.zoom;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ZoomConfig {
    public static class Client {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        private static final ModConfigSpec.BooleanValue HURT_EXIT;
        private static final ModConfigSpec.DoubleValue SPEED;
        private static final ModConfigSpec.IntValue FOV;

        static {
            HURT_EXIT = BUILDER
                    .comment("When the player is injured, the zoom will exit.")
                    .translation("zoom.configuration.hurt_exit")
                    .define("hurt_exit", true);

            SPEED = BUILDER
                    .comment("Camera move speed")
                    .translation("zoom.configuration.speed")
                    .defineInRange("speed", 0.4, 0.1, 5);

            FOV = BUILDER
                    .comment("Camera FOV")
                    .translation("zoom.configuration.fov")
                    .defineInRange("fov", 90, 1, 179);
        }

        public static final ModConfigSpec SPEC = BUILDER.build();

        public static boolean hurtExit() {
            return HURT_EXIT.getAsBoolean();
        }

        public static float speed() {
            return (float) SPEED.getAsDouble();
        }

        public static int fov() {
            return FOV.getAsInt();
        }
    }

    public static class Server {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        private static final ModConfigSpec.BooleanValue BLOCK_COLLISION;

        static {
            BLOCK_COLLISION = BUILDER
                    .comment("Ignore block collision box")
                    .translation("zoom.configuration.block_collision")
                    .define("block_collision", true);
        }

        public static final ModConfigSpec SPEC = BUILDER.build();

        public static boolean blockCollision() {
            if (SPEC.isLoaded()) {
                return BLOCK_COLLISION.getAsBoolean();
            } else {
                return true;
            }
        }
    }
}
