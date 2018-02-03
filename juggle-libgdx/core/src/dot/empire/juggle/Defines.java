package dot.empire.juggle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * Contains the general definitions for the engine.
 */
public final class Defines {

    //<editor-fold defaultstate="uncollapsed" desc="Constants">
    public static final String SFX_DEATH = "death.mp3";
    public static final String SFX_JUMP = "181602__coby12388__enerjump.ogg";
    public static final String SFX_COLLECT = "170147__timgormly__8-bit-coin.ogg";
    public static final String SFX_MUSIC = "bgMusic.mp3";
    public static final String SFX_INTRO = "intro.mp3";

    public static final Color[] COLOURS = {
        new Color(0.0f, 0.8565f, 0.1435f, 0.0f),
        new Color(0.1269f, 0.3392f, 0.5339f, 0.0f),
        new Color(0.1543f, 0.1543f, 0.6914f, 0.0f),
        new Color(0.3060f, 0.0820f, 0.6858f, 0.0f),
        new Color(0.4783f, 0.2530f, 0.2688f, 0.0f),
        new Color(0.6451f, 0.3316f, 0.0233f, 0.0f),
        new Color(0.6537f, 0.2946f, 0.5170f, 0.0f),
        new Color(0.7406f, 0.1406f, 0.1188f, 0.0f)
    };
    private static final String[] MSG_DEATH = {
        "yikes",
        "whoops",
        "sorry"
//        ""
    };
    public static int GRAVITY = -1;
    public static int PLAYER_SIZE = -1;
    public static int COIN_SIZE = -1;
    public static int PIPE_SIZE = -1;

    public static String getRandDeathMsg() {
        return MSG_DEATH[MathUtils.random(MSG_DEATH.length - 1)];
    }

    public interface Messages {

        public static final int DIE = 0x0;
        public static final int COLLECT = 0x1;
        public static final int START = 0x2;
        public static final int AD_SHOW = 0x3;
        public static final int AD_HIDE = 0x4;
        public static final int REALIGN = 0x5;
    }
    //</editor-fold>
}
