package dot.empire.juggle;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Disposable;

/**
 * @param <T> <code>this</code> for chaining
 * @author Ashley
 */
public abstract class Scene<T extends Scene> extends ScreenAdapter
        implements Disposable, Core {

    private final BaseEngine parent;

    public Scene(BaseEngine parent) {
        this.parent = parent;
    }

    /**
     * @param dt delta time
     */
    @Override
    public final void render(final float dt) {
        this.update(dt);
        this.render();
    }

    public T update(final float dt) {
        return (T) this;
    }

    public T render() {
        return (T) this;
    }

    @Override
    public void hide() {
        this.dispose();
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName();
    }

    public BaseEngine getParent() {
        return this.parent;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == null || !(obj instanceof Scene)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    public final T next(Scene next) {
        this.getParent().setScreen(next);
        super.hide();
        return (T) this;
    }
}
