package com.boltive.integration.example

import com.boltive.integration.implementation.applovin.ApplovinBannerActivity
import com.boltive.integration.implementation.applovin.ApplovinInterstitialActivity
import com.boltive.integration.implementation.gam.GamBannerActivity
import com.boltive.integration.implementation.gam.GamBannerJavaActivity
import com.boltive.integration.implementation.gam.GamInterstitialActivity

object ExamplesRepository {

    fun getAll() = arrayListOf(
        Example(
            "GAM Banner",
            GamBannerActivity::class.java
        ),
        Example(
            "GAM Banner Java",
            GamBannerJavaActivity::class.java
        ),
        Example(
            "GAM Interstitial",
            GamInterstitialActivity::class.java
        ),
        Example(
            "Applovin Banner",
            ApplovinBannerActivity::class.java
        ),
        Example(
            "Applovin Interstitial",
            ApplovinInterstitialActivity::class.java
        ),
    )

}