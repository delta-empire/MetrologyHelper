package ru.sergeipavlov.metrologyhelper;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

/** Activity-scoped adaptive sticky banner, shared across all fragment screens. */
public final class StickyBannerController {
    private static final String AD_UNIT_ID = "R-M-20373985-1";
    private final Activity activity;
    private final FrameLayout container;
    @Nullable private BannerAdView banner;
    private boolean started;

    public StickyBannerController(Activity activity, FrameLayout container) {
        this.activity = activity;
        this.container = container;
    }

    public void load() {
        if (started) return;
        started = true;
        container.post(() -> {
            if (activity.isDestroyed() || activity.isFinishing()) return;
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            int widthPx = container.getWidth() > 0 ? container.getWidth() : dm.widthPixels;
            int widthDp = Math.round(widthPx / dm.density);
            if (widthDp <= 0) return;
            BannerAdView view = new BannerAdView(activity);
            banner = view;
            container.addView(view, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setAdSize(BannerAdSize.sticky(activity, widthDp));
            view.setBannerAdEventListener(new BannerAdEventListener() {
                @Override public void onAdLoaded() {
                    if (activity.isDestroyed()) destroy();
                }
                @Override public void onAdFailedToLoad(@NonNull AdRequestError error) {
                    // No immediate retry to avoid repeated unsuccessful network requests.
                }
                @Override public void onAdClicked() {}
                @Override public void onImpression(@Nullable ImpressionData impressionData) {}
            });
            view.loadAd(new AdRequest.Builder(AD_UNIT_ID).build());
        });
    }

    public void destroy() {
        if (banner != null) {
            banner.destroy();
            container.removeView(banner);
            banner = null;
        }
    }
}
