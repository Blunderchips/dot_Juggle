package dot.empire.juggle.cmp;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Disposable;

import java.math.BigInteger;

import static dot.empire.juggle.Defines.Messages.COLLECT;
import static dot.empire.juggle.Defines.Messages.DIE;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public final class CmpScore implements Comparable<BigInteger>, Telegraph,
        Disposable {

    /**
     *
     */
    private static final BigInteger B00B5 = BigInteger.valueOf(0xB00B5);

    private boolean first;
    private BigInteger score;

    @SuppressWarnings("LeakingThisInConstructor")
    public CmpScore() {
        this.score = null;
        this.first = true;

        MessageManager.getInstance().addListener(this, DIE);
        MessageManager.getInstance().addListener(this, COLLECT);
    }

    public CmpScore inc() {
        this.score = score.add(ONE);
        return this;
    }

    @Override
    public String toString() {
        return (this.score == null ? "0" : (this.score.equals(B00B5)
                ? "(.)(.)" : score.toString(10))).toUpperCase();
    }

    @Override
    public int compareTo(BigInteger other) {
        return this.score.compareTo(other);
    }

    public boolean equals(BigInteger other) {
        return this.score.equals(other);
    }

    public CmpScore setZero() {
        this.score = ZERO;
        return this;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case DIE:
                //this.score = ZERO;
                break;
            case COLLECT:
                if (score == null) {
                    this.score = ZERO;
                }
                if (first) {
                    this.first = false;
                    break;
                }
                this.score = score.add(ONE);
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean isNull() {
        return this.score == null;
    }

    public CmpScore setNull() {
        this.score = null;
        return this;
    }

    @Override
    public void dispose() {
        MessageManager.getInstance().removeListener(this, DIE);
        MessageManager.getInstance().removeListener(this, COLLECT);
    }

    public long getValue() {
        return this.score.longValue();
    }
}
