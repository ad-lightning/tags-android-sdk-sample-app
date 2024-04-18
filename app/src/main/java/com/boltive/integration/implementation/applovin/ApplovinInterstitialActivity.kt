package com.boltive.integration.implementation.applovin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.boltive.api.AdNetwork
import com.boltive.api.AdViewConfiguration
import com.boltive.api.BoltiveConfiguration
import com.boltive.api.BoltiveMonitor
import com.boltive.integration.R
import com.boltive.integration.databinding.ActivityBannerBinding
import kotlin.random.Random

class ApplovinInterstitialActivity : AppCompatActivity() {

    private var interstitial: MaxInterstitialAd? = null

    private lateinit var binding: ActivityBannerBinding
    private lateinit var boltiveMonitor: BoltiveMonitor

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
                initAd()
            }
        }
    }

    private fun initBoltiveMonitor() {
        val boltiveConfiguration = BoltiveConfiguration("adl-test", AdNetwork.APPLOVIN)
        boltiveMonitor = BoltiveMonitor(boltiveConfiguration)
    }

    private fun initAd() {
        val adUnitId = if (Random.nextBoolean()) "b3ef8c3fb9d723fa" else "d6ef21fac158270c"
        interstitial = MaxInterstitialAd(adUnitId, this).apply {
            setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    val adViewConfiguration = AdViewConfiguration(
                        320, 480, adUnitId
                    )
                    boltiveMonitor.captureInterstitial(application, adViewConfiguration) {}
                    interstitial?.showAd()
                }

                override fun onAdHidden(ad: MaxAd?) {
                    boltiveMonitor.stopCapturingInterstitial()
                }

                override fun onAdDisplayed(ad: MaxAd?) {}
                override fun onAdClicked(ad: MaxAd?) {}
                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {}
                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {}
            })
            loadAd()
        }
    }

}