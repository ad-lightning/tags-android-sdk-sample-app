package com.boltive.integration.implementation.applovin

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.boltive.api.AdNetwork
import com.boltive.api.AdViewConfiguration
import com.boltive.api.BoltiveConfiguration
import com.boltive.api.BoltiveMonitor
import com.boltive.integration.R
import com.boltive.integration.databinding.ActivityBannerBinding
import kotlin.random.Random

class ApplovinBannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannerBinding
    private lateinit var boltiveMonitor: BoltiveMonitor
    private var adView: MaxAdView? = null

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
                adView?.stopAutoRefresh()
                initAd()
            }
        }
    }

    private fun initBoltiveMonitor() {
        val boltiveConfiguration = BoltiveConfiguration("adl-test", AdNetwork.APPLOVIN_MAX)
        boltiveMonitor = BoltiveMonitor(boltiveConfiguration)
    }

    private fun initAd() {
        binding.adViewWrapper.removeAllViews()
        val adUnitId = if (Random.nextBoolean()) "c91cf5ec359f01da" else "757687e43c5651d9"
        adView = MaxAdView(adUnitId, this)
        adView?.setListener(object : MaxAdViewAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                val adViewConfiguration = AdViewConfiguration(300, 250, adUnitId)
                boltiveMonitor.capture(adView ?: return, adViewConfiguration) {
                    adView?.stopAutoRefresh()
                    (adView?.parent as ViewGroup?)?.removeView(adView)
                }
            }

            override fun onAdDisplayed(ad: MaxAd?) {}
            override fun onAdHidden(ad: MaxAd?) {}
            override fun onAdClicked(ad: MaxAd?) {}
            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {}
            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {}
            override fun onAdExpanded(ad: MaxAd?) {}
            override fun onAdCollapsed(ad: MaxAd?) {}
        })
        binding.adViewWrapper.addView(adView)
        adView?.loadAd()
    }

}