package dot.empire.juggle.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dot.empire.juggle.BaseEngine;
import dot.empire.juggle.Defines;
import dot.empire.juggle.Scene;
import dot.empire.juggle.cmp.CmpCoin;
import dot.empire.juggle.cmp.CmpPlayer;
import dot.empire.juggle.cmp.CmpScore;
import dot.empire.juggle.gfx.IcoTap;
import dot.empire.juggle.gfx.PipeManager;

import static com.badlogic.gdx.graphics.Color.BLACK;
import static com.badlogic.gdx.graphics.Color.RED;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line;
import static dot.empire.juggle.Core.Game;
import static dot.empire.juggle.Defines.Messages.AD_HIDE;
import static dot.empire.juggle.Defines.Messages.AD_SHOW;
import static dot.empire.juggle.Defines.Messages.COLLECT;
import static dot.empire.juggle.Defines.Messages.DIE;
import static dot.empire.juggle.Defines.SFX_INTRO;

public final class SceneGame extends Scene<SceneGame> implements Telegraph {

    private final PipeManager pipeBatch;
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;

    private final BitmapFont fntT;
    private final BitmapFont fntC;
    private final GlyphLayout layout;

    private final CmpScore score;
    private final CmpPlayer player;
    private final CmpCoin coin;
    private final IcoTap tap;
    private String deathMsg;

    @SuppressWarnings("LeakingThisInConstructor")
    public SceneGame(BaseEngine parent) {
        super(parent);

        this.deathMsg = null;
        this.score = new CmpScore();
        this.player = new CmpPlayer();
        this.coin = new CmpCoin();
        this.pipeBatch = new PipeManager();
        this.tap = new IcoTap();

        this.batch = new SpriteBatch();
        this.batch.setProjectionMatrix(Game.getCamera().combined);
        this.batch.enableBlending();
        this.layout = new GlyphLayout();

        this.renderer = new ShapeRenderer();
        this.renderer.setAutoShapeType(true);
        this.renderer.setProjectionMatrix(Game.getCamera().combined);

        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("data/fnt/atari full.ttf"));
        final FreeTypeFontParameter paramT = new FreeTypeFontParameter();
        final FreeTypeFontParameter paramC = new FreeTypeFontParameter();

        paramC.size = (int) (Game.getWidth() * 0.05f);
        paramC.color = BLACK;
        this.fntC = generator.generateFont(paramC);

        paramT.size = (int) (Game.getWidth() * 0.125f);
        paramT.color = BLACK;
        this.fntT = generator.generateFont(paramT);

        generator.dispose();

        MessageManager.getInstance().addListener(this, DIE);
        MessageManager.getInstance().addListener(this, COLLECT);

        Game.getAudioManager().dispose(SFX_INTRO);
    }

    @Override
    public SceneGame update(float dt) {
        if (Game.getAlphaVal() > 0) {
            Game.incAplha(-dt);
        }

        this.player.update(dt);
        this.coin.update(dt, player);

        if (deathMsg == null && !score.isNull()) {
            this.pipeBatch.update(dt, this);
        } else {
            this.tap.update(dt);
        }
        Game.icoSound().update(dt);

        return this;
    }

    @Override
    public SceneGame render() {
        this.renderer.begin(Line);
        {
            if (deathMsg == null && !score.isNull()) {
                this.pipeBatch.render(renderer);
            }
        }
        this.renderer.end();

        this.batch.begin();
        {
            Game.icoSound().render(batch);

            float xPos, yPos;

            if (deathMsg != null) {
                this.layout.setText(fntC, deathMsg);

                xPos = (Game.getWidth() - layout.width) * 0.5f;
                yPos = (Game.getHeight() * 0.8f) - (layout.height * 0.5f);
                this.fntC.draw(batch, layout, xPos, yPos);

                try {
                    long best = Game.getPrefs().getLong("best");
                    if (score.getValue() > best) {
                        best = score.getValue();
                        Game.getPrefs().putLong("best", best).flush();
                        this.deathMsg = "new best";
                    }

                    this.layout.setText(fntC, "Best - " + Long.toString(best, 16).toUpperCase());

                    xPos = (Game.getWidth() - layout.width) * 0.5f;
                    yPos = (Game.getHeight() * 0.6f) - (layout.height * 0.5f);
                    this.fntC.draw(batch, layout, xPos, yPos);
                } catch (NullPointerException ignore) {
                }
            }

            this.layout.setText(fntT, score.isNull() ? "Jump!" : score.toString());

            xPos = (Game.getWidth() - layout.width) * 0.5f;
            yPos = (Game.getHeight() * 0.75f) - (layout.height * 0.5f);
            this.fntT.draw(batch, layout, xPos, yPos);

            if (score.isNull()) {
                this.tap.render(batch);
            }

            //Game.renderApha(batch);
        }
        this.batch.end();

        this.renderer.begin(Filled);
        {
            this.coin.render(renderer);
            this.player.render(renderer);

            //this.renderer.setColor(RED);
            //this.renderer.line(0, 0.5f, Game.getWidth(), 0.5f);
            //this.renderer.line(0, Game.getHeight() - 1, Game.getWidth(), Game.getHeight() - 1);
        }
        this.renderer.end();

        return this;
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.renderer.dispose();

        this.fntT.dispose();
        this.fntC.dispose();
        this.coin.dispose();
        this.tap.dispose();
        this.pipeBatch.dispose();

        MessageManager.getInstance().removeListener(this, DIE);
        MessageManager.getInstance().removeListener(this, COLLECT);
    }

    public CmpPlayer getPlayer() {
        return this.player;
    }

    public long getScore() {
        return this.score.getValue();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case DIE: {
                if (deathMsg == null && !score.isNull()) {
                    this.deathMsg = Defines.getRandDeathMsg();
                    MessageManager.getInstance().dispatchMessage(AD_SHOW);
                }
            }
            break;
            case COLLECT: {
                if (deathMsg != null) {
                    this.deathMsg = null;
                    this.score.setZero();
                }
                MessageManager.getInstance().dispatchMessage(AD_HIDE);
            }
            break;
            default:
                return false;
        }
        return true;
    }
}
