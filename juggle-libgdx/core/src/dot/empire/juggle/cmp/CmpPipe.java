package dot.empire.juggle.cmp;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import dot.empire.juggle.RNGesus;
import dot.empire.juggle.scenes.SceneGame;

import static dot.empire.juggle.Core.Game;
import static dot.empire.juggle.Defines.COLOURS;
import static dot.empire.juggle.Defines.Messages.DIE;
import static dot.empire.juggle.Defines.Messages.REALIGN;
import static dot.empire.juggle.Defines.PIPE_SIZE;
import static dot.empire.juggle.Defines.PLAYER_SIZE;

public final class CmpPipe implements Comparable<CmpPipe>, Telegraph, Disposable {

    /**
     *
     */
    private static final RNGesus P_RNG = new RNGesus(3);
    /**
     *
     */
    private static int LAST_COLOUR = -1;

    private final int ID;
    private final Vector2 position;
    private final Vector2 velocity;

    private int colour;

    @SuppressWarnings("LeakingThisInConstructor")
    public CmpPipe(int ID) {
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.colour = 0;
        this.ID = ID;

        MessageManager.getInstance().addListener(this, REALIGN);
    }

    public CmpPipe update(float dt, SceneGame parent) {
        this.position.y -= PLAYER_SIZE * 2 * 3 * dt;
        if (parent != null) {
            if (coll(parent.getPlayer())) {
                MessageManager.getInstance().dispatchMessage(DIE);
            }

        }
        if (position.y < -PIPE_SIZE) {
            this.reset(1);

            if (this.ID == 10) {
                MessageManager.getInstance().dispatchMessage(REALIGN);
            }
        }

        return this;
    }

    public CmpPipe render(ShapeRenderer renderer) {
        renderer.setColor(COLOURS[colour]);
        renderer.rect(position.x, position.y, PIPE_SIZE, PIPE_SIZE);
        return this;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (msg.message == REALIGN) {
            this.position.y = Game.getHeight() + (PIPE_SIZE * -(9 - ID));
            return true;
        }
        return false;
    }

    public CmpPipe reset(int index) {
        float num;
        switch (P_RNG.next()) {
            case 1:
                num = 0.125f;
                break;
            case 2:
                num = 0.5f;
                break;
            default:
                num = 0.875f;
        }
        this.position.set(
                (Game.getWidth() * num) - (PIPE_SIZE * 0.5f),
                Game.getHeight() + (PIPE_SIZE * index)
        );
        this.velocity.setZero();

        do {
            this.colour = MathUtils.random(COLOURS.length - 1);
        } while (this.colour == LAST_COLOUR || colour == CmpPlayer.COLOUR);
        CmpPipe.LAST_COLOUR = this.colour;

        return this;
    }

    private boolean coll(CmpPlayer player) {
        return position.x < player.getPosition().x + PLAYER_SIZE
                && position.x + PIPE_SIZE > player.getPosition().x
                && position.y < player.getPosition().y + PLAYER_SIZE
                && PIPE_SIZE + position.y > player.getPosition().y;
    }

    @Override
    public int compareTo(CmpPipe other) {
        return Integer.compare(this.colour, other.colour);
    }

    public int getColour() {
        return this.colour;
    }

    @Override
    public void dispose() {
        MessageManager.getInstance().removeListener(this, REALIGN);
    }
}
