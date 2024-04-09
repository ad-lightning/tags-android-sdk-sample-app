package com.boltive.integration.implementation.gam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.boltive.api.AdViewConfiguration
import com.boltive.api.BoltiveConfiguration
import com.boltive.api.BoltiveListener
import com.boltive.api.BoltiveMonitor
import com.boltive.integration.R
import com.boltive.integration.databinding.ActivityBannerBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdView

class GamBannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannerBinding
    private lateinit var boltiveMonitor: BoltiveMonitor
    private lateinit var adView: AdManagerAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_banner)

        initViews()
        initBoltiveMonitor()
        initAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        boltiveMonitor.terminate()
    }

    private fun initViews() {
        binding.apply {
            btnReload.setOnClickListener {
                adView.loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun initBoltiveMonitor() {
        val boltiveConfiguration = BoltiveConfiguration("adl-test")
        boltiveMonitor = BoltiveMonitor(boltiveConfiguration)
    }

    private fun initAd() {
        val adRequest = AdRequest.Builder().build()
        val bannerWidth = 300
        val bannerHeight = 250
        val adUnitId = "/21808260008/btest_banner_random"

        adView = AdManagerAdView(this)
        adView.setAdSize(AdSize(bannerWidth, bannerHeight))
        adView.adUnitId = adUnitId

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                val boltiveListener = BoltiveListener { adView.loadAd(adRequest) }
                val adViewConfiguration = AdViewConfiguration(320, 50, adUnitId)
                boltiveMonitor.capture(adView, adViewConfiguration, boltiveListener)
            }
        }

        binding.adViewWrapper.addView(adView)
        adView.loadAd(adRequest)
    }

}