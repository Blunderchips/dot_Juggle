package dot.empire.juggle.cmp;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import static dot.empire.juggle.Core.Game;
import static dot.empire.juggle.Defines.COIN_SIZE;
import static dot.empire.juggle.Defines.Messages.COLLECT;
import static dot.empire.juggle.Defines.Messages.DIE;
import static dot.empire.juggle.Defines.Messages.START;
import static dot.empire.juggle.Defines.PIPE_SIZE;
import static dot.empire.juggle.Defines.PLAYER_SIZE;

public final class CmpCoin implements Disposable, Telegraph {

    private static final int SPEED = 100;
    private final Vector2 position;
    private int quad;
    private boolean dir;
    private boolean first;
    private float rotation;

    @SuppressWarnings("LeakingThisInConstructor")
    public CmpCoin() {
        this.quad = 0;
        this.first = true;
        this.position = new Vector2();

        MessageManager.getInstance().addListener(this, DIE);
        MessageManager.getInstance().addListener(this, COLLECT);

        this.reset(true);
    }

    public CmpCoin update(float dt, CmpPlayer player) {
        if ((rotation += dt * SPEED * (dir ? 1 : -1)) > 360) {
            rotation = 0;
        }

        if (coll(player)) {
            MessageManager.getInstance().dispatchMessage(START);
            MessageManager.getInstance().dispatchMessage(COLLECT);
        }
        return this;
    }

    public CmpCoin render(ShapeRenderer renderer) {
        renderer.setColor(Color.GOLD);
        renderer.rect(position.x, position.y,
                COIN_SIZE / 2, COIN_SIZE / 2,
                COIN_SIZE, COIN_SIZE, 1, 1, rotation);
        return this;
    }

    private boolean coll(CmpPlayer player) {
        return position.x < player.getPosition().x + PLAYER_SIZE
                && position.x + COIN_SIZE > player.getPosition().x
                && position.y < player.getPosition().y + PLAYER_SIZE
                && COIN_SIZE + position.y > player.getPosition().y;
    }

    @Override
    public void dispose() {
        MessageManager.getInstance().addListener(this, DIE);
        MessageManager.getInstance().addListener(this, COLLECT);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case DIE: {
                this.reset(true);
                this.first = true;
            }
            break;
            case COLLECT: {
                this.reset(false);
                this.first = false;
            }
            break;
            default:
                return false;
        }
        return true;
    }

    private void reset(boolean isDead) {
        final int width = Game.getWidth();
        final int height = Game.getHeight();

        int _quad = -2;
        if (isDead) {
            this.position.set(
                    (width - COIN_SIZE) * 0.5f,
                    (height * 0.4f) - (COIN_SIZE * 0.5f)
            );
        } else {
            do {
                this.position.set(
                        MathUtils.random(PIPE_SIZE, width - PIPE_SIZE),
                        MathUtils.random(PIPE_SIZE, height * 0.8f)
                );
                _quad = (position.x > width * 0.5f ? 1 : 2) * (position.y > height * 0.5f ? 1 : -1);
            } while (this.quad == _quad);
        }

        this.quad = _quad;
        if (!first) {
            this.dir = MathUtils.randomBoolean();
            this.rotation = MathUtils.random(90);
        }
    }
}
