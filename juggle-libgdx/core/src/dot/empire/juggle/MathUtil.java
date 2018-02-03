//package dot.empire.juggle;
//
//import com.badlogic.gdx.math.Vector2;
//
///**
// * Provides an interface for utility math functions and constants. <b>You can
// * not instantiate this class.</b>
// *
// * @author Matthew 'siD' Van der Bijl
// */
//public final class MathUtil {
//
//    @Deprecated
//    private MathUtil() {
//    }
//
//    public static float clamp(float value, float min, float max) {
//        return (value < min) ? min : (value > max) ? max : value;
//    }
//
//    public static float sq(float valf) {
//        return valf * valf;
//    }
//
//    public static float distanceSq(Vector2 vecA, Vector2 vecB) {
//        return MathUtil.distanceSq(vecA.x, vecA.y, vecB.x, vecB.y);
//    }
//
//    public static float distanceSq(float x1, float y1, float x2, float y2) {
//        final float x_d = x2 - x1;
//        final float y_d = y2 - y1;
//        return x_d * x_d + y_d * y_d;
//    }
//}
