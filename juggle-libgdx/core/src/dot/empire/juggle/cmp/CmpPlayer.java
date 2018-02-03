package dot.empire.juggle.cmp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import dot.empire.juggle.Defines;

import static dot.empire.juggle.Core.Game;
import static dot.empire.juggle.Defines.COLOURS;
import static dot.empire.juggle.Defines.GRAVITY;
import static dot.empire.juggle.Defines.Messages.DIE;
import static dot.empire.juggle.Defines.SFX_JUMP;

public final class CmpPlayer implements Disposable, Telegraph {

    public static int COLOUR = -1;

    private final Vector2 position;
    private final Vector2 velocity;
    private int colour;

    @SuppressWarnings("LeakingThisInConstructor")
    public CmpPlayer() {
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(-1, 0);
        this.colour = -1;

        MessageManager.getInstance().addListener(this, DIE);

        this.reset();
    }

    public CmpPlayer update(float dt) {
        final int width = Game.getWidth();

        if (Gdx.input.justTouched()
                || position.x <= 0 || position.x + Defines.PLAYER_SIZE >= width) {
            this.velocity.y = width;

            if (velocity.x != 0) {
                velocity.x *= -1;
            } else {
                this.velocity.x = Gdx.input.getX() <= width / 2 ? -1 : 1;
            }

            Game.getAudioManager().play(SFX_JUMP, true);
        }
        if (velocity.x != 0) {
            this.velocity.add(0, GRAVITY);

            this.velocity.y *= dt;
            this.position.add((width * 0.5f) * dt * velocity.x, velocity.y);

            if (position.y < 0) {
                this.position.y = 0;
            }

            this.velocity.y *= 1 / dt;

            if (position.y <= 0 || position.y >= Game.getHeight() - Defines.PLAYER_SIZE) {
                this.reset();
                MessageManager.getInstance().dispatchMessage(DIE);
            }
        }

        return this;
    }

    public CmpPlayer render(ShapeRenderer renderer) {
        renderer.setColor(COLOURS[colour]);
        renderer.rect(position.x, position.y, Defines.PLAYER_SIZE, Defines.PLAYER_SIZE);
        return this;
    }

    private void reset() {
        this.velocity.setZero();
        this.position.set(
                (Game.getWidth() - Defines.PLAYER_SIZE) * 0.5f,
                (Game.getHeight() * 0.175f) - (Defines.PLAYER_SIZE * 0.5f)
        );

        do {
            this.colour = MathUtils.random(COLOURS.length - 1);
        } while (colour == COLOUR);
        CmpPlayer.COLOUR = colour;
    }

    @Override
    public void dispose() {
        MessageManager.getInstance().removeListener(this, DIE);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case DIE:
                this.reset();
                break;
            default:
                return false;
        }
        return true;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public Vector2 getVelocity() {
        return this.velocity;
    }
}
