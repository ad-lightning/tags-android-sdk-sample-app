# Boltive Android SDK

Boltive Android SDK is a native Android library for intercepting malicious ad creatives.

**Quick facts:**

- SDK currently supports banner and interstitial ad formats.

- SDK has been explicitly tested against GAM, AdMob, AppLovin MAX, however the SDK is not limited to these integration scenarios, please see [this section](https://github.com/ad-lightning/android-sdk-sample-app#other-ad-networks-and-sdks)

- The current SDK version is 1.3. See [release notes](CHANGELOG.md).

## Integration

### Maven repository

1. Add maven repository to your *.gradle file (settings.gradle for Gradle 7+)
```
repositories {
    maven {
        url "https://packagecloud.io/Boltive/Mobile-SDK/maven2"
    }
}
```
2. Add Boltive dependency
```
dependencies {
    implementation "com.boltive:boltive-sdk:1.3.19"
}
```

### Manual downloading AAR

1. Download the [SDK zip archive](http://d29z9h4vafd34g.cloudfront.net/android/boltive-android-sdk-1.3.19.zip) and unzip it: 
```
curl -OL http://d29z9h4vafd34g.cloudfront.net/android/boltive-android-sdk-1.3.19.zip
unzip boltive-android-sdk-1.3.19.zip
```
2. Create `libs` directory inside your `app` module. 
3. Copy `boltive-android-sdk.aar` library from archive into `libs`.
4. Add Boltive SDK dependency in app's `build.gradle` file and sync Gradle.

```groovy
    dependencies {
        implementation files("libs/boltive-android-sdk.aar")
    }
```

## User Guide

### GAM Banners

To connect Boltive SDK with the GAM banner you should call `BoltiveMonitor.capture()` static method
within the `AdListener`'s `onAdLoaded()` method, passing it an ad banner, clientId
and `BoltiveListener` instance as parameters.  `BoltiveListener` is called in the event
when `BoltiveMonitor` detects an ad that is supposed to be blocked.

**Note**: `BoltiveMonitor` blocker will prevent the malicious ad creative from rendering - so the ad would become blank, 
however it will not automatically remove the native banner view from the screen (so worst case the user will see a blank ad).  
It will send a signal to the app native code (via the callback mechanism).   
**Then it is the responsibility of the app developer to take the appropriate action in the callback function**: f.e. reload and refresh the
banner, render a different ad unit, remove the banner altogether etc. The most common action to
take would be to call the native method that would reload the banner.

Create Boltive monitor instance.

```java
    private final BoltiveMonitor boltiveMonitor = new BoltiveMonitor(
        new BoltiveConfiguration("<your client id>")
    );
```

Add `capture` call inside AdListener's `onAdLoaded` with listener. 
You should pass more information about your app using [BoltiveTagDetails](#tag-details) object inside AdViewConfiguration's constructor. 

```java
    AdRequest adRequest = new AdRequest.Builder().build();
    gamBannerView.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
            AdViewConfiguration adViewConfiguration = new AdViewConfiguration(
                "<banner width>", "<banner height>", "<your ad unit id>"
            );
            boltiveMonitor.capture(gamBannerView, adViewConfiguration, ()-> {
                gamBannerView.loadAd(adRequest);
            });
        }
    });
    gamBannerView.loadAd(adRequest);
```

Add `terminate` call when ad view destroyed or inside `onDestroy` of Activity (or Fragment).

```java
    @Override
    protected void onDestroy(){
        super.onDestroy();
        boltiveMonitor.terminate();
    }
```

#### Example removing banner from the view tree

If you don't want to show an ad unit after the ad has been blocked within it, you can remove the ad view from
the view tree:

```java
    boltiveMonitor.capture(gamBannerView, adViewConfiguration, () -> {
        ViewParent parent = gamBannerView.getParent();
        if (parent instanceof ViewGroup){
            ((ViewGroup) parent).removeView(gamBannerView);
        }
    }
```

### GAM Interstitial

Add `captureInterstitial` call before calling `interstitial.show`.  Also make sure you call `BoltiveMonitor.stopCapturingInterstitial` inside interstitial's `FullScreenContentCallback.onAdDismissedFullScreenContent`, see example below:

```java
    AdRequest adRequest = new AdRequest.Builder().build();
    AdManagerInterstitialAd.load(this, "<your ad unit id>", adRequest, new InterstitialAdLoadCallback() {
        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd){
            super.onAdLoaded(interstitialAd);
            
            AdViewConfiguration viewConfiguration = new AdViewConfiguration(
                320, 480, "<your ad unit id>"
            );
            boltiveMonitor.captureInterstitial(getApplication(), viewConfiguration, interstitialAd, ()-> {
                // Any Actions
            })
        
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    boltiveMonitor.stopCapturingInterstitial();
                }
            });
            interstitialAd.show(MyActivity.this);
        });
    };
```

You should pass more information about your app using [BoltiveTagDetails](#tag-details) object inside AdViewConfiguration's constructor. Don't forget to pass InterstitialAd parameter to `BoltiveMonitor.captureInterstitial`.

**Note:** When Boltive SDK detects an offensive interstitial - it automatically blocks and dismisses it (contrary to the behavior for banners, where it is a developer responsibility), providing you with a callback (you implement it as an anonymous function passed to `BoltiveMonitor.captureInterstitial`) where you can reload the interstitial and/or perform any other side effects as necessary.


### Applovin MAX: Create Boltive Monitor

Create Boltive monitor instance. <b>IMPORTANT</b> to set second parameter `AdNetwork.APPLOVIN_MAX`. You should pass more information about your app using [BoltiveTagDetails](#tag-details) object inside AdViewConfiguration's constructor.


```java
    private final BoltiveMonitor boltiveMonitor = new BoltiveMonitor(
        new BoltiveConfiguration("<your client id>", AdNetwork.APPLOVIN)
    );
```

### Applovin MAX Banner

Add `capture` call inside MaxAdViewAdListener's `onAdLoaded` with listener.

```java
    private void initAd() {
        adViewContainer.removeAllViews();

        maxAdView = new MaxAdView(adUnitId, this);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                AdViewConfiguration adViewConfiguration = new AdViewConfiguration(
                        "<banner width>", "<banner height>", "<your ad unit id>"
                );
                boltiveMonitor.capture(maxAdView, adViewConfiguration, () -> {
                    maxAdView.destroy();
                    initAd();
                });
            }
            ...
        });

        adViewContainer.addView(maxAdView);
        maxAdView.loadAd();
    }
```

Add `terminate` call when ad view is destroyed or inside the `onDestroy` method of the Activity (or Fragment).

```java
    @Override
    protected void onDestroy(){
        super.onDestroy();
        boltiveMonitor.terminate();
    }
```

### Applovin MAX Interstitial

Add `captureInterstitial` call before calling `interstitial.showAd()`, and `stopCapturingInterstitial` inside `onAdHidden`.
You should pass more information about your app using [BoltiveTagDetails](#tag-details) object inside AdViewConfiguration's constructor.


```java
    MaxInterstitialAd interstitial = new MaxInterstitialAd("<your ad unit id>", this);
    interstitial.setListener(new MaxAdListener() {
        @Override
        public void onAdLoaded(MaxAd ad) {
            AdViewConfiguration viewConfiguration = new AdViewConfiguration(
                    320, 480, "<your ad unit id>"
            );
            boltiveMonitor.captureInterstitial(getApplication(), viewConfiguration, () -> {});
            interstitial.showAd();
        }
    
        @Override
        public void onAdHidden(MaxAd ad) {
            boltiveMonitor.stopCapturingInterstitial();
        }
        ...
    });
```

## Memory leaks

Consider using `WeakReference` for the ad view reference that you use
inside the `BoltiveListener` callback that is passed into the `capture()` call to prevent memory leaks.

```    
    private static class SafeBoltiveListener implements BoltiveListener {

        private final WeakReference<AdView> adView;

        public SafeBoltiveListener(AdView adView) {
            this.adView = new WeakReference<>(adView);
        }

        @Override
        public void onAdBlocked() {
            AdView view = this.adView.get();
            if (view != null) {
                view.loadAd();
            }
        }
    }
```

Usage:
```                
    boltiveMonitor.capture(adView, adViewConfiguration, new SafeBoltiveListener(adView));
```


## Tag Details

As part of the `AdViewConfiguration` you can pass a `tagDetails` object, containing data handy for reporting and attribution of the ad blocking events.

```
    BoltiveTagDetails tagDetails = new BoltiveTagDetails("<your ad unit id>");
    
    tagDetails.setAppName("<you app name>");
    tagDetails.setAdvertiserId("<your advertiser id>");
    tagDetails.setCampaignId("<your campaign id>");
    tagDetails.setCreativeId("<your creative id>");
    tagDetails.setLineItemId("<your line item id>");
    tagDetails.setSspRefreshCode("<your ssp refresh code>");
    
    AdViewConfiguration viewConfig = new AdViewConfiguration(300, 250, tagDetails);
    monitor.capture(adView, viewConfig, () -> { ... })
```
Please note that if any of the ad server details properties are not set (`advertiserId`, `campaignId`, `creativeId`, `lineItemId`) - Boltive SDK will attempt to fill them from `ResponseInfo` object in case of Google Mobile Ads SDK. 

## Other Ad Networks and SDKs

`Boltive SDK` has been tested against GAM, AdMob (Google Mobile Ads SDK) and AppLovin MAX integration scenarios. However `BoltiveMonitor`
API is designed to be SDK-agnostic. The only assumption it makes is that the ad is rendered in
the `WebView` object contained somewhere within a `View`-based banner view hierarchy. Most ad SDKs 
use the same integration patterns as Google Mobile Ads SDK - thus the usage of `BoltiveMonitor` with them would be similar.

## Google Ad Manager and AdMob

Google Ad Manager and AdMob assume the integration of Google Mobile Ads SDK into the app.

- [GMA SDK Get Started](https://developers.google.com/ad-manager/mobile-ads-sdk/android/quick-start)

## Applovin MAX

Applovin MAX assumes integration of Applovin MAX Mobile Ads SDK into the app.

- [Applovin Integration](https://dash.applovin.com/documentation/mediation/android/getting-started/integration)
