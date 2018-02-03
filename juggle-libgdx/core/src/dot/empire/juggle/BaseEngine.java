package dot.empire.juggle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import dot.empire.juggle.gfx.IcoSound;
import dot.empire.juggle.scenes.SceneSplash;
import it.unimi.dsi.util.XorShift1024StarRandom;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static dot.empire.juggle.Defines.Messages.COLLECT;
import static dot.empire.juggle.Defines.Messages.DIE;
import static dot.empire.juggle.Defines.Messages.START;
import static dot.empire.juggle.Defines.SFX_COLLECT;
import static dot.empire.juggle.Defines.SFX_DEATH;
import static dot.empire.juggle.Defines.SFX_JUMP;
import static dot.empire.juggle.Defines.SFX_MUSIC;

public final class BaseEngine extends Gdx
        implements Disposable, ApplicationListener, Core, Telegraph {

    private final int width, height;
    private Screen next;
    private Screen screen;
    private AudioManager audioManager;
    private Preferences preferences;
    private float alphaVal;
    private Sprite alphaImg;
    private Music bgMusic;
    private IcoSound icoSound;
    private Viewport viewport;
    private OrthographicCamera camera;
    private boolean isMuted;

    public BaseEngine() {
        this.next = null;
        this.alphaVal = 0;
        this.isMuted = false;

        this.width = 360;
        this.height = 640;

        Defines.PLAYER_SIZE = 32;
        Defines.COIN_SIZE = Defines.PLAYER_SIZE / 2;
        Defines.GRAVITY = -(36/ 2);
        Defines.PIPE_SIZE = 64;
    }

    @Override
    public void create() {
        Gdx.gl.glLineWidth(4 * (Gdx.graphics.getWidth() / getWidth()));

        Gdx.gl.glClearColor(
                255 / 255f,
                255 / 255f,
                255 / 255f,
                1
        );

        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.camera = new OrthographicCamera(getWidth(), getHeight());
        this.viewport = new StretchViewport(getWidth(), getHeight(), camera);

        this.camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        MathUtils.random = new XorShift1024StarRandom();

        this.alphaImg = new Sprite(new Texture(Gdx.files.internal("data/gfx/alpha.png")));
        this.alphaImg.setScale(getWidth(), getHeight());
        this.alphaImg.setCenter(getWidth() / 2, getHeight() / 2);

        this.icoSound = new IcoSound();

        this.audioManager = new AudioManager(3, this);
        // load sfx into engine
        this.audioManager.get(SFX_DEATH);
        this.audioManager.get(SFX_COLLECT);
        this.audioManager.get(SFX_JUMP);

        MessageManager.getInstance().addListener(this, DIE);
        MessageManager.getInstance().addListener(this, COLLECT);
        MessageManager.getInstance().addListener(this, START);

        this.bgMusic = Gdx.audio.newMusic(Gdx.files.internal("data/sfx/" + SFX_MUSIC));
        this.bgMusic.setLooping(true);
        //this.bgMusic.setVolume(0.3f);
        this.bgMusic.play();
        this.bgMusic.pause();

        this.screen = new SceneSplash(this);
    }

    @Override
    public void render() {
        this.camera.update();
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        this.screen.render(Gdx.graphics.getDeltaTime());

        if (next != null) {
            this.screen.hide();
            this.screen = next;
            this.screen.show();
            this.screen.resize(
                    getWidth(),
                    getHeight()
            );
            this.next = null;
        }

    }

    @Override
    public void dispose() {
        Gdx.input.cancelVibrate();

        if (next != null) {
            this.next.dispose();
            this.next = null;
        }
        this.screen.dispose();
        this.audioManager.dispose();
        this.bgMusic.dispose();
        this.alphaImg.getTexture().dispose();

        MessageManager.getInstance().removeListener(this, DIE);
        MessageManager.getInstance().removeListener(this, COLLECT);
        MessageManager.getInstance().removeListener(this, START);
    }

    public void setScreen(Scene screen) throws UnsupportedOperationException {
        if (screen == null) {
            throw new IllegalArgumentException("NULL Scene.");
        }
        this.next = screen;
    }

    @Override
    public void pause() {
        this.screen.pause();
    }

    @Override
    public void resume() {
        this.screen.resume();
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height);
        this.camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        this.screen.resize(width, height);
    }

    public AudioManager getAudioManager() {
        return this.audioManager;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case START: {
                if (!isMuted) {
                    this.bgMusic.play();
                }
            }
            break;
            case DIE: {
                this.bgMusic.pause();
                this.audioManager.play(SFX_DEATH, false);

                if (!isMuted()) {
                    Gdx.input.vibrate(50);
                }
            }
            break;
            case COLLECT: {
                if (!isMuted()) {
                    this.getBgMusic().play();
                }
                this.audioManager.play(SFX_COLLECT, false);
            }
            break;
            default:
                return false;
        }
        return true;
    }

    public Preferences getPrefs() {
        if (preferences == null) {
            this.preferences = Gdx.app.getPreferences(getName());
        }
        return this.preferences;
    }

    public Music getBgMusic() {
        return this.bgMusic;
    }

    public String getName() {
        return "juggle";
    }

    public float getAlphaVal() {
        return this.alphaVal;
    }

    public BaseEngine setAlphaVal(float alpha) {
        this.alphaVal = MathUtils.clamp(alpha, 0, 1);
        return this;
    }

    public BaseEngine incAplha(float inc) {
        this.setAlphaVal(alphaVal + inc * 3);
        return this;
    }

    public BaseEngine renderApha(SpriteBatch batch) {
        if (alphaVal != 0) {
            this.alphaImg.draw(batch, alphaVal);
        }
        return this;
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isMuted() {
        return this.isMuted;
    }

    public BaseEngine setMuted(boolean isMuted) {
        if (isMuted) {
            this.bgMusic.pause();
        }
        this.isMuted = isMuted;
        this.icoSound.toggle();
        return this;
    }

    public IcoSound icoSound() {
        return this.icoSound;
    }
}
