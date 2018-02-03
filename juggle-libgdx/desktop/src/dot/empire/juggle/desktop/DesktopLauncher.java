package dot.empire.juggle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import static dot.empire.juggle.Core.Game;

public final class DesktopLauncher {

    /**
     * @param args arguments from the command line
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String[] args) {
        final LwjglApplicationConfiguration cfg
                = new LwjglApplicationConfiguration();

        cfg.title = Game.getName();

        cfg.samples = 16;
        cfg.resizable = false;

        cfg.width = 360;
        cfg.height = 640;

        cfg.r = 24;
        cfg.g = 24;
        cfg.b = 24;
        cfg.a = 24;

        new LwjglApplication(Game, cfg);
    }
}
