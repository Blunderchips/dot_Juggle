package dot.empire.juggle.gfx;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

import dot.empire.juggle.cmp.CmpPipe;
import dot.empire.juggle.scenes.SceneGame;

import static dot.empire.juggle.Defines.Messages.DIE;

public final class PipeManager extends ArrayList<CmpPipe>
        implements Disposable, Telegraph {

    private int lastColour;

    @SuppressWarnings("LeakingThisInConstructor")
    public PipeManager() {
        super(6);
        this.lastColour = -1;

        for (int i = 0; i < 6 * 2; i += 2) {
            this.add(new CmpPipe(i).reset(i));
        }

        MessageManager.getInstance().addListener(this, DIE);
    }

    public PipeManager append(CmpPipe obj) {
        this.add(obj);
        return this;
    }

    public PipeManager update(float dt, SceneGame parent) {
        for (CmpPipe obj : this) {
            obj.update(dt, parent);
        }
        return this;
    }

    public PipeManager render(ShapeRenderer renderer) {
        //int colour = -1;
        for (CmpPipe obj : this) {
            //if (colour != obj.getColour()) {
            //    renderer.setColor(Defines.COLOURS[colour = obj.getColour()]);
            //}
            obj.render(renderer);
        }
        return this;
    }

    @Override
    public void dispose() {
        MessageManager.getInstance().removeListener(this, DIE);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case DIE: {
                for (int i = 0, j = 0; i < this.size(); i++, j += 2) {
                    super.get(i).reset(j);
                }
            }
            break;
            default:
                return false;
        }
        return true;
    }
}
