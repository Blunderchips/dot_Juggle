package dot.empire.juggle.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dot.empire.juggle.BaseEngine;
import dot.empire.juggle.Scene;
import dot.empire.juggle.gfx.IcoTap;
import dot.empire.juggle.gfx.PipeManager;

import static com.badlogic.gdx.graphics.Color.BLACK;
import static com.badlogic.gdx.graphics.Color.RED;
import static dot.empire.juggle.Defines.SFX_JUMP;

public final class SceneMenu extends Scene<SceneMenu> {

    private final ShapeRenderer renderer;
    private final SpriteBatch batch;
    private final BitmapFont fntC;
    private final BitmapFont fntT;
    private final GlyphLayout layout;
    private final PipeManager pipeBatch;
    private final IcoTap tap;
    private boolean transition;

    //    private final BtnMute btnMute;
//    private final BtnPlay btnPlay;
    public SceneMenu(BaseEngine parent) {
        super(parent);

        this.tap = new IcoTap();
        this.transition = false;
        this.batch = new SpriteBatch();
        this.batch.setProjectionMatrix(Game.getCamera().combined);
        this.layout = new GlyphLayout();
        this.pipeBatch = new PipeManager();

//        this.btnMute = new BtnMute();
//        this.btnPlay = new BtnPlay();
        this.renderer = new ShapeRenderer();
        this.renderer.setAutoShapeType(true);
        this.renderer.setProjectionMatrix(Game.getCamera().combined);

        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("data/fnt/atari full.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter paramC
                = new FreeTypeFontGenerator.FreeTypeFontParameter();
        final FreeTypeFontGenerator.FreeTypeFontParameter paramT
                = new FreeTypeFontGenerator.FreeTypeFontParameter();

        paramC.size = (int) (Game.getWidth() * 0.05f);
        paramC.color = BLACK;
        this.fntC = generator.generateFont(paramC);

        paramT.size = (int) (Game.getWidth() * 0.125f);
        paramT.color = BLACK;
        this.fntT = generator.generateFont(paramT);

        generator.dispose();
    }

    @Override
    public SceneMenu update(float dt) {
        if (!transition && Game.getAlphaVal() > 0) {
            Game.incAplha(-dt);
        }

        this.tap.update(dt);
        this.pipeBatch.update(dt, null);
        if (transition) {
            if (Game.getAlphaVal() == 1) {
                super.next(new SceneGame(getParent()));
            } else {
                Game.incAplha(dt);
            }
        } else if (Gdx.input.justTouched()) {
//            final boolean isRight = Gdx.input.getX() < Gdx.graphics.getWidth() / 2;

//            if (isRight) {
            Game.getAudioManager().play(SFX_JUMP, false);
            transition = true;
//            } else {
//                Game.getPrefs().putBoolean("isMuted", !Game.getPrefs().getBoolean("isMuted"));
//            }
        }
        Game.icoSound().update(dt);
        return this;
    }

    @Override
    public SceneMenu render() {
        this.batch.begin();
        {
            float xPos, yPos;

            this.layout.setText(fntT, Game.getName());

            xPos = (Game.getWidth() - layout.width) * 0.5f;
            yPos = (Game.getHeight() * 0.75f) - (layout.height * 0.5f);
            this.fntT.draw(batch, layout, xPos, yPos);

            this.layout.setText(fntC, "(c) .Empire 2017");

            xPos = (Game.getWidth() - layout.width) * 0.5f;
            yPos = (Game.getHeight() * 0.15f) - (layout.height * 0.5f);
            this.fntC.draw(batch, layout, xPos, yPos);

            this.layout.setText(fntC, ">>> tap <<<");

            xPos = (Game.getWidth() - layout.width) * 0.5f;
            yPos = (Game.getHeight() * 0.6f) - (layout.height * 0.5f);
            this.fntC.draw(batch, layout, xPos, yPos);

            this.tap.render(batch);
        }
        this.batch.end();

        this.renderer.begin();
        {
            this.pipeBatch.render(renderer);

            //this.renderer.setColor(RED);
            //this.renderer.line(0, 1, Game.getWidth(), 1);
            //this.renderer.line(0, Game.getHeight() - 1, Game.getWidth(), Game.getHeight() - 1);
        }
        this.renderer.end();

        this.batch.begin();
        {
            Game.icoSound().render(batch);
            Game.renderApha(batch);
        }
        this.batch.end();
        return this;
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.renderer.dispose();

        this.fntC.dispose();
        this.fntT.dispose();
        this.tap.dispose();
    }
}
