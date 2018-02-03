package dot.empire.juggle.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

import dot.empire.juggle.BaseEngine;
import dot.empire.juggle.Scene;

import static com.badlogic.gdx.graphics.Color.BLACK;
import static dot.empire.juggle.Defines.SFX_INTRO;

public final class SceneSplash extends Scene<SceneSplash> {

    private final SpriteBatch batch;
    private final BitmapFont fntT;
    private final GlyphLayout layout;
    private final Vector2 position;
    private final Music jingle;
    private boolean hasPlayed;

    private boolean transition;

    public SceneSplash(BaseEngine parent) {
        super(parent);

        this.hasPlayed = false;
        this.transition = false;
        this.batch = new SpriteBatch();

        this.layout = new GlyphLayout();

        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("data/fnt/atari full.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter paramT
                = new FreeTypeFontGenerator.FreeTypeFontParameter();

        paramT.size = (int) (Game.getWidth() * 0.125f);
        paramT.color = BLACK;
        this.fntT = generator.generateFont(paramT);

        generator.dispose();

        this.layout.setText(fntT, ".Empire");
        this.position = new Vector2(
                (Game.getWidth() - layout.width) * 0.5f,
                (Game.getHeight() * 0.65f) - (layout.height * 0.5f)
        );

        this.jingle = Gdx.audio.newMusic(Gdx.files.internal("data/sfx/" + SFX_INTRO));
        this.jingle.play();
        this.jingle.pause();
    }

    @Override
    public SceneSplash update(float dt) {
        if (!hasPlayed) {
            this.jingle.play();
            this.hasPlayed = true;
            return this;
        }
        if (Gdx.input.justTouched()
                || !jingle.isPlaying()) {
            this.transition = true;
        }

        if (transition) {
            //Game.incAplha(dt);
            //if (Game.getAlphaVal() == 1) {
            super.next(new SceneMenu(getParent()));
//            }
        }

        Game.icoSound().update(dt);
        return this;
    }

    @Override
    public SceneSplash render() {
        this.batch.setProjectionMatrix(Game.getCamera().combined);
        this.batch.begin();
        {
            this.fntT.draw(batch, layout, position.x, position.y);

            Game.icoSound().render(batch);
            Game.renderApha(batch);
        }
        this.batch.end();
        return this;
    }

    @Override
    public void dispose() {
        this.fntT.dispose();
        this.batch.dispose();
        this.jingle.dispose();
    }
}
