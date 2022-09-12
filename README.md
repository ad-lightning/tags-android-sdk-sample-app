# Boltive Android SDK

Boltive Android SDK is a native Android library for intercepting malicious ad creatives in mobile banner ads.

**Quick facts:**

- SDK currently supports banner ad format.

- We assume that the app integrates Google Mobile Ads SDK and works with Google Ad Manager, however the SDK is not limited by this assumption, see [this section](https://github.com/ad-lightning/android-sdk-sample-app#other-ad-networks-and-sdks)

- The current SDK version is 0.1 (private beta).

## Integration

1. Download and unzip the SDK archive.
2. Create `libs` directory inside your `app` module. 
3. Copy `boltive-android-sdk.aar` library from archive to `libs`.
4. Add Boltive SDK dependency in app's `build.gradle` file and sync Gradle.

```groovy
dependencies {
    implementation files("libs/boltive-android-sdk.aar")
}
```

## User Guide

To connect Boltive SDK with the GAM banner you should call `BoltiveMonitor.capture()` static method within the `AdListener`'s `onAdLoaded()` method, passing it an ad banner, clientId and `BoltiveListener` instance as parameters.  `BoltiveListener` is called in the event when `BoltiveMonitor` detects an ad that is supposed to be blocked.

**Note**: Unlike web, on mobile `BoltiveMonitor` does not actually block or prevent any ads from rendering - it only reports them and signals to the app native code.  It is your responsibility as the app developer to take appropriate action in the callback closure: i.e. to reload and refresh the banner, render a different ad unit, remove the banner alltogether etc.  The most common action to take would be to reload the banner.

Create Boltive monitor instance.

```java
    private final BoltiveMonitor boltiveMonitor = new BoltiveMonitor(
        new BoltiveConfiguration("<your client id>", "<your ad platform name>")
    );
```

Add `capture` call inside AdListener's `onAdLoaded` with listener.

```java
    AdRequest adRequest = new AdRequest.Builder().build();
    gamBannerView.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
            AdViewConfiguration adViewConfiguration = new AdViewConfiguration(
                320, 50,
                "<your ad unit id>"
            );
            boltiveMonitor.capture(gamBannerView, adViewConfiguration, () -> {
                gamBannerView.loadAd(adRequest);
            });
        }
    });
    gamBannerView.loadAd(adRequest);
```

Add `destroy` call when ad view destroyed or inside `onDestroy` of Activity (or Fragment).

```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        boltiveMonitor.terminate();
    }
```

## Other Ad Networks and SDKs

`Boltive SDK` was tested against GAM and Google Mobile Ads SDK integration.  However `BoltiveMonitor` API is designed to be SDK-agnostic.  The only assumption it makes is that the ad is rendered in the `WebView` object contained somewhere within a `View`-based banner view hierarchy.  Most ad SDKs provide callback mechanisms similar to the listener provided by Google Mobile Ads SDK in which you can use `BoltiveMonitor` to capture the banner - as described above for the GAM scenario.

## Google Ad Manager

Google Ad Manager assumes integration of Google Mobile Ads SDK into the app.

References:

- [GMA SDK Get Started](https://developers.google.com/ad-manager/mobile-ads-sdk/android/quick-start)
- [Banner Ads](https://developers.google.com/ad-manager/mobile-ads-sdk/android/banner)
