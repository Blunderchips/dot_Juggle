package dot.empire.juggle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

import static dot.empire.juggle.Core.Game;

/**
 * @author Matthew 'siD' Van der Bijl
 */
public final class AudioManager extends HashMap<String, Sound>
        implements Disposable {

    @SuppressWarnings("LeakingThisInConstructor")
    protected AudioManager(int initialCapacity, BaseEngine parent) {
        super(initialCapacity);
    }

    @Override
    public Sound get(Object key) throws IllegalArgumentException {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("The key must be a string "
                    + "(the name of the audio source).");
        }
        return this.get((String) key);
    }

    /**
     * @param name the name of the track
     * @return the received the <code>Sound</code>
     */
    public Sound get(String name) {
        final Sound as = super.get(name);
        if (as == null) {
            super.put(name, Gdx.audio.newSound(Gdx.files.internal("data/sfx/" + name)));
            return this.get(name);
        } else {
            return as;
        }
    }

    public AudioManager play(String name, boolean fluctuatePitch) {
        if (!Game.isMuted()) {
            final Sound tmp = this.get(name);
            tmp.stop();
            final long id = tmp.play();
            if (fluctuatePitch) {
                tmp.setPitch(id, MathUtils.random(0.9f, 1f));
            }
        }
        return this;
    }

    /**
     * @param name the name of the track to be paused
     * @return <code>this</code> for chaining for chaining
     */
    public AudioManager pause(String name) {
        this.get(name).pause();
        return this;
    }

    /**
     * @param name the name of the track to be stopped
     * @return <code>this</code> for chaining for chaining
     */
    public AudioManager stop(String name) {
        this.get(name).stop();
        return this;
    }

    @Override
    public void dispose() {
        for (Sound sound : values()) {
            sound.dispose();
        }
    }

    public AudioManager dispose(String name) {
        final Sound tmp = this.get(name);
        tmp.stop();
        tmp.dispose();
        super.remove(name);
        return this;
    }
}
