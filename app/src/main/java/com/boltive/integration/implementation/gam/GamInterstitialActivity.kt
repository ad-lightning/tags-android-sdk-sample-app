package com.boltive.integration.implementation.gam

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.boltive.api.AdViewConfiguration
import com.boltive.api.BoltiveConfiguration
import com.boltive.api.BoltiveMonitor
import com.boltive.integration.R
import com.boltive.integration.databinding.ActivityBannerBinding
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import kotlin.random.Random

class GamInterstitialActivity : AppCompatActivity() {

    private var interstitialAd: AdManagerInterstitialAd? = null

    private lateinit var binding: ActivityBannerBinding
    private lateinit var boltiveMonitor: BoltiveMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_banner)

        initBoltiveMonitor()
        initAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        boltiveMonitor.terminate()
    }

    private fun initBoltiveMonitor() {
        val boltiveConfiguration = BoltiveConfiguration(
            "adl-test"
        )
        boltiveMonitor = BoltiveMonitor(boltiveConfiguration)
    }

    private fun initAd() {
        val adUnitId = if (Random.nextBoolean()) "/6499/example/interstitial" else "/21808260008/boltive-interstial-with-bad-url"
        val adRequest = AdManagerAdRequest.Builder().build()
        val viewConfiguration = AdViewConfiguration(320, 480, adUnitId)

        AdManagerInterstitialAd.load(
            this,
            adUnitId,
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: AdManagerInterstitialAd) {
                    super.onAdLoaded(ad)

                    interstitialAd = ad
                    boltiveMonitor.captureInterstitial(application, viewConfiguration) {
                        Log.d("Interstitial Activity", "Ad blocked!")
                    }
                    interstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                boltiveMonitor.stopCapturingInterstitial()
                            }
                        }
                    interstitialAd?.show(this@GamInterstitialActivity)
                }
            })
    }

}