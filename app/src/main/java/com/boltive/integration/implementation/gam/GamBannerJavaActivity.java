package com.boltive.integration.implementation.gam;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.boltive.api.AdViewConfiguration;
import com.boltive.api.BoltiveConfiguration;
import com.boltive.api.BoltiveMonitor;
import com.boltive.integration.R;
import com.boltive.integration.databinding.ActivityBannerBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdView;

public class GamBannerJavaActivity extends AppCompatActivity {

    private ActivityBannerBinding binding;
    private BoltiveMonitor boltiveMonitor;
    private AdManagerAdView adView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_banner);

        initViews();
        initBoltiveMonitor();
        initAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boltiveMonitor.terminate();
    }


    private void initViews() {
        binding.btnReload.setOnClickListener(v -> {
            adView.loadAd(new AdRequest.Builder().build());
        });
    }

    private void initBoltiveMonitor() {
        BoltiveConfiguration configuration = new BoltiveConfiguration("adl-test");
        boltiveMonitor = new BoltiveMonitor(configuration);
    }

    private void initAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        int bannerWidth = 300;
        int bannerHeight = 250;
        String adUnitId = "/21808260008/boltive-banner-with-ok-and-bad-url";

        adView = new AdManagerAdView(this);
        adView.setAdSize(new AdSize(bannerWidth, bannerHeight));
        adView.setAdUnitId(adUnitId);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                AdViewConfiguration adViewConfiguration = new AdViewConfiguration(
                        bannerWidth, bannerHeight, adUnitId
                );
                boltiveMonitor.capture(adView, adViewConfiguration, () -> {
                    adView.loadAd(adRequest);
                });
            }
        });

        binding.adViewWrapper.addView(adView);
        adView.loadAd(adRequest);
    }

}
