# Boltive Android SDK Integration Guide

## Google Ad Manager

Google Ad Manager assumes integration of Google Mobile Ads SDK into the app.

References:

- [GMA SDK Get Started](https://developers.google.com/ad-manager/mobile-ads-sdk/android/quick-start)
- [Banner Ads](https://developers.google.com/ad-manager/mobile-ads-sdk/android/banner)


## Integration guide

Create folder `libs` in your `app` module. Copy `boltive-android-sdk.aar` file from archive
to `libs`.

Add Boltive SDK dependency in app's `build.gradle` file and sync Gradle.

```groovy
dependencies {
    implementation files("libs/boltive-android-sdk.aar")
}
```

### BoltiveMonitor API

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
