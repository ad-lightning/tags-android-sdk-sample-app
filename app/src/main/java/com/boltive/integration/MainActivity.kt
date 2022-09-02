package com.boltive.integration

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.boltive.api.AdViewConfiguration
import com.boltive.api.BoltiveConfiguration
import com.boltive.api.BoltiveListener
import com.boltive.api.BoltiveMonitor
import com.boltive.integration.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var boltiveMonitor: BoltiveMonitor
    private lateinit var adView: AdManagerAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
        initBoltiveMonitor()
        initAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        boltiveMonitor.terminate()
    }

    private fun initViews() {
        supportActionBar?.title = "Boltive SDK v${BoltiveMonitor.SDK_VERSION} App"
        binding.btnToJava.setOnClickListener {
            startActivity(
                Intent(this, JavaMainActivity::class.java)
            )
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
        val adRequest = AdRequest.Builder().build()
        val bannerWidth = 320
        val bannerHeight = 50
        val adUnitId = "/6499/example/banner"

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