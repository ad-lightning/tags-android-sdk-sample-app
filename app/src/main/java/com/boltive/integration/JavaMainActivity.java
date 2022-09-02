package com.boltive.integration;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.boltive.api.AdViewConfiguration;
import com.boltive.api.BoltiveConfiguration;
import com.boltive.api.BoltiveMonitor;
import com.boltive.integration.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdView;

public class JavaMainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BoltiveMonitor boltiveMonitor;
    private AdManagerAdView adView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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
        getSupportActionBar().setTitle("Boltive SDK v" + BoltiveMonitor.SDK_VERSION + " App");
        binding.btnToJava.setVisibility(View.GONE);
    }

    private void initBoltiveMonitor() {
        BoltiveConfiguration configuration = new BoltiveConfiguration(
                "adl-test", "GAM"
        );
        boltiveMonitor = new BoltiveMonitor(configuration);
    }

    private void initAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        int bannerWidth = 320;
        int bannerHeight = 50;
        String adUnitId = "/6499/example/banner";

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
