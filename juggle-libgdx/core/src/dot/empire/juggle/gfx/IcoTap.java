package dot.empire.juggle.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static dot.empire.juggle.Core.Game;

public final class IcoTap extends Texture {

    private float time;
    private boolean isOn;

    public IcoTap() {
        super(Gdx.files.internal("data/gfx/click.png"));

        this.isOn = true;
        this.time = 0f;
    }

    public IcoTap update(float dt) {
        if ((time += dt) > 1) {
            this.isOn = !isOn;
            this.time = 0;
        }
        return this;
    }

    public IcoTap render(SpriteBatch batch) {
        if (isOn) {
            batch.draw(this,
                    (Game.getWidth() - super.getWidth()) * 0.5f,
                    Game.getHeight() * 0.45f
            );
        }
        return this;
    }
}
