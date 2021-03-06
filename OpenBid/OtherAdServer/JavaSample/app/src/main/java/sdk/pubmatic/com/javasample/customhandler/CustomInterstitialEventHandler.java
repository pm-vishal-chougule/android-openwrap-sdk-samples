package sdk.pubmatic.com.javasample.customhandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.pubmatic.sdk.common.POBError;
import com.pubmatic.sdk.common.ui.POBInterstitialRendering;
import com.pubmatic.sdk.openbid.core.POBBid;
import com.pubmatic.sdk.openbid.interstitial.POBInterstitialEvent;
import com.pubmatic.sdk.openbid.interstitial.POBInterstitialEventListener;

import sdk.pubmatic.com.javasample.dummyadserver.DummyAdServerSDK;

/**
 * This class is responsible for communication between OpenBid interstitial and interstitial ad from your ad server SDK(in this case DummyAdServerSDK).
 * It implements the POBInterstitialEvent protocol. it notifies event back to OpenBid SDK using POBInterstitialEventListener methods
 */
@SuppressLint("LongLogTag")
public class CustomInterstitialEventHandler extends DummyAdServerSDK.DummyAdServerEventListener implements POBInterstitialEvent {

    private static final String TAG = "CustomInterstitialEventHandler";

    private POBInterstitialEventListener eventListener;

    private DummyAdServerSDK adServerSDK;

    /**
     * Constructor
     *
     * @param context  value of context
     * @param adUnitId ad server ad unit ID
     */
    public CustomInterstitialEventHandler(Context context, String adUnitId) {
        adServerSDK = new DummyAdServerSDK(context, adUnitId);
        adServerSDK.setAdServerEventListener(this);
    }

    /**
     * OpenBid SDK passes its bids through this method. You should request an ad from your ad server here.
     *
     * @param bid POBBid for targetting parameter
     */
    @Override
    public void requestAd(POBBid bid) {
        // If bid is valid, add bid related custom targeting on the ad request
        if (null != bid) {
            Log.d(TAG, bid.toString());
            adServerSDK.setCustomTargetting(bid.getTargetingInfo().toString());
        }
        // Load ad from the ad server
        adServerSDK.loadInterstitialAd();
    }

    /**
     * Setter method
     *
     * @param pobInterstitialEventListener reference of POBInterstitialEventListener
     */
    @Override
    public void setEventListener(POBInterstitialEventListener pobInterstitialEventListener) {
        eventListener = pobInterstitialEventListener;
    }

    @Override
    public POBInterstitialRendering getRenderer(String s) {
        return null;
    }

    /**
     * Called when the Interstitial ad is about to show.
     */
    @Override
    public void show() {
        adServerSDK.showInterstitialAd();
    }

    /**
     * A dummy custom event triggered based on targeting information sent in the request.
     * This sample uses this event to determine if the partner ad should be served.
     *
     * @param event event value
     */
    @Override
    public void onCustomEventReceived(String event) {
        // Identify if the ad from OpenBid partner is to be served and, if so, call 'openBidPartnerDidWin'
        if ("SomeCustomEvent".equals(event) && null != eventListener) {
            eventListener.onOpenBidPartnerWin();
        }
    }

    /**
     * Called when the interstitial ad is received.
     */
    @Override
    public void onInterstitialReceived() {
        if (null != eventListener) {
            eventListener.onAdServerWin();
        }
    }


    /**
     * Tells the listener that an ad request failed. The failure is normally due to
     * network connectivity or ad availability (i.e., no fill).
     *
     * @param dummyError value of DummyError
     */
    @Override
    public void onAdFailed(DummyAdServerSDK.DummyError dummyError) {
        if (null != eventListener) {
            eventListener.onFailed(new POBError(dummyError.getErrorCode(), dummyError.getErrorMsg()));
        }
    }

    /**
     * Similarly you can implement all the other ad flow events
     * Method to do clean up
     */
    @Override
    public void destroy() {
        adServerSDK.destroy();
        eventListener = null;
    }

}
