package dot.empire.juggle.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import static dot.empire.juggle.Core.Game;

public final class IcoSound implements Disposable {

    private final Texture[] icons;
    private float time;

    public IcoSound() {
        this.time = 0f;
        this.icons = new Texture[]{
                new Texture(Gdx.files.internal("data/gfx/speaker.png")),
                new Texture(Gdx.files.internal("data/gfx/speaker-off.png"))
        };
    }

    public IcoSound update(float dt) {
        if (this.isOn()) {
            this.setTime(time - dt);
        }
        return this;
    }

    public IcoSound render(SpriteBatch batch) {
        if (this.isOn()) {
            final Texture tmp = icons[Game.isMuted() ? 1 : 0];
            batch.draw(tmp,
                    (Game.getWidth() - tmp.getWidth()) * 0.5f,
                    (Game.getHeight() - tmp.getHeight()) * 0.25f
            );
        }
        return this;
    }

    @Override
    public void dispose() {
        for (Texture icon : icons) {
            icon.dispose();
        }
    }

    public boolean isOn() {
        return this.time != 0;
    }

    public IcoSound toggle() {
        this.time = 1;
        return this;
    }

    public float getTime() {
        return this.time;
    }

    public IcoSound setTime(float time) {
        this.time = MathUtils.clamp(time, 0, 1);
        return this;
    }
}
