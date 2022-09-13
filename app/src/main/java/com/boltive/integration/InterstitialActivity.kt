package com.boltive.integration

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.boltive.api.AdViewConfiguration
import com.boltive.api.BoltiveConfiguration
import com.boltive.api.BoltiveMonitor
import com.boltive.integration.databinding.ActivityMainBinding
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import kotlin.random.Random

class InterstitialActivity : AppCompatActivity() {

    private var interstitialAd: AdManagerInterstitialAd? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var boltiveMonitor: BoltiveMonitor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
        initBoltiveMonitor()
    }

    override fun onDestroy() {
        super.onDestroy()
        boltiveMonitor.terminate()
    }

    private fun initViews() {
        supportActionBar?.title = "Boltive SDK v${BoltiveMonitor.SDK_VERSION} App"
        binding.apply {
            btnToInterstitial.visibility = View.GONE
            btnReload.setOnClickListener {
                initAd()
            }
        }
    }

    private fun initBoltiveMonitor() {
        val boltiveConfiguration = BoltiveConfiguration(
            "adl-test",
            "GAM"
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
                    interstitialAd?.show(this@InterstitialActivity)
                }
            })
    }

}