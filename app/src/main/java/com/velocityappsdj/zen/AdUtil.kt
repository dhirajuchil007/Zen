package com.velocityappsdj.zen

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.WindowManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.velocityappsdj.zen.activities.MainActivity

object AdUtil {

    fun getAdSize(
        windowManager: WindowManager,
        adContainerView: View,
        resources: Resources,
        context: Context
    ): AdSize {
        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds

        var adWidthPixels = adContainerView.width.toFloat()

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0f) {
            adWidthPixels = bounds.width().toFloat()
        }

        val density = resources.displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    fun loadBanner(adView: AdView, adSize: AdSize) {
        adView.adUnitId = "ca-app-pub-9540086841520699/1209412069"

        adView.setAdSize(adSize)
        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val adRequest = AdRequest
            .Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }
}