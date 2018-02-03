package dot.empire.juggle;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import static dot.empire.juggle.Core.Game;
import static dot.empire.juggle.Defines.Messages.AD_HIDE;
import static dot.empire.juggle.Defines.Messages.AD_SHOW;

public final class AndroidLauncher extends AndroidApplication
        implements Telegraph {

    protected AdView adView;
    private boolean showing;

    public AndroidLauncher() {
        this.showing = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MessageManager.getInstance().addListener(this, AD_SHOW);
        MessageManager.getInstance().addListener(this, AD_HIDE);

        final AndroidApplicationConfiguration cfg
                = new AndroidApplicationConfiguration();

        cfg.numSamples = 16;

        //cfg.r = 24;
        //cfg.g = 24;
        //cfg.b = 24;
        //cfg.a = 24;
        cfg.useImmersiveMode = true;
        cfg.useCompass = false;
        cfg.useAccelerometer = false;

        RelativeLayout layout = new RelativeLayout(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View gameView = super.initializeForView(Game, config);
        layout.addView(gameView);

        this.adView = new AdView(this);
        this.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                int visiblity = adView.getVisibility();
                adView.setVisibility(AdView.GONE);
                adView.setVisibility(visiblity);
            }
        });
        this.adView.setAdSize(AdSize.SMART_BANNER);
        // http://www.google.com/admob
        //this.adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        this.adView.setAdUnitId("ca-app-pub-7186830154970754/1032198629");

        AdRequest.Builder builder = new AdRequest.Builder();
        //run once before uncommenting the following line. Get TEST device ID from the logcat logs.
//        builder.addTestDevice("INSERT TEST DEVICE ID HERE");
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        layout.addView(adView, adParams);
        this.adView.loadAd(builder.build());
        this.adView.setVisibility(View.GONE);

        setContentView(layout);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case AD_SHOW:
                if (showing) {
                    return false;
                }
                this.showing = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adView.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case AD_HIDE:
                if (!showing) {
                    return false;
                }
                this.showing = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adView.setVisibility(View.GONE);
                    }
                });
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP: {
                if (Game.isMuted()) {
                    Game.setMuted(false);
                    return true;
                }
            }
            break;
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                if (!Game.isMuted()) {
                    Game.setMuted(true);
                    return true;
                }
            }
            break;
        }
        return super.dispatchKeyEvent(event);
    }
}
