Releases
========

1.3
---
Ad attribution improvements for GAM.

1.2
---
If user does not provide ad server details (`advertiserId`, `campaignId`, `creativeId` and `lineItemId`) as part of the `BoltiveTagDetails` object - BoltiveSDK will try to obtain these from the `ResponseInfo` object in the case of Google Mobile Ads SDK as a Primary SDK.  This is supported for both Banners and Interstitials.

The API for `BoltiveMonitor.captureInterstitial` and now requires an instance of the `InterstitialAd` as a parameter.


1.1
---
Introduced `BoltiveTagDetails.appName` for tracking purposes.  If not provided the value is set to `Bundle.main.bundleIdentifier`.  


1.0
---
Initial release.
