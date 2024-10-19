package com.albe.Video2Audio.ADS;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdsManager {

    AdRequest adRequest;

    public String INTERSTITIAL_ADS_ID, BANNER_ADS_ID;

    public InterstitialAd currentAd;

    public AdsManager(String INTERSTITIAL_ADS_ID, String BANNER_ADS_ID) {
        this.adRequest = new AdRequest.Builder().build();
        this.INTERSTITIAL_ADS_ID = INTERSTITIAL_ADS_ID;
        this.BANNER_ADS_ID = BANNER_ADS_ID;
    }

    public void loadInterstitial(Activity activity) {
        InterstitialAd.load(activity, INTERSTITIAL_ADS_ID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("APP ADS", loadAdError.toString());
                currentAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                currentAd = interstitialAd;
                currentAd.show(activity);
                Log.d("APP ADS", "AD LOADED SUCCESSFULLY");
            }
        });
    }

    public void loadBanner(Activity activity, AdView currentAdView) {
        AdView adView = new AdView(activity);
        adView.setAdUnitId(BANNER_ADS_ID);
        adView.setAdSize(bannerSize(activity));

        currentAdView.removeAllViews();
        currentAdView.addView(adView);

        adView.loadAd(adRequest);
    }

    AdSize bannerSize(Activity activity) {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int adWidthPixels = metrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = metrics.density;

        int adWidth = (int) (adWidthPixels/density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
