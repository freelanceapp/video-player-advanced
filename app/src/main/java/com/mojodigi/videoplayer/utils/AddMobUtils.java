package com.mojodigi.videoplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


import com.mojodigi.videoplayer.R;

public class AddMobUtils extends Activity
{
    private String ADDLOGTAG="BANNER_ADD_LOGTAG";



  public  void displayBannerAdd(final AdView mAdView)
  {

      AdRequest adRequest = new AdRequest.Builder()
              .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
              // Check the LogCat to get your test device ID
              .addTestDevice("33BE2250B43518CCDA7DE426D04EE231")
              .build();

      mAdView.setAdListener(new AdListener() {
          @Override
          public void onAdLoaded() {
              // Toast.makeText(getApplicationContext(), "Ad loaded!", Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,"Add is Loaded");
              mAdView.setVisibility(View.VISIBLE);

          }

          @Override
          public void onAdClosed() {
              // Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,"Ad is closed!");
          }

          @Override
          public void onAdFailedToLoad(int errorCode) {
              //Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,""+"Ad failed to load! error code: " + errorCode);
          }

          @Override
          public void onAdLeftApplication() {
              // Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
              Log.d(ADDLOGTAG,"Ad left application!");
          }

          @Override
          public void onAdOpened() {
              super.onAdOpened();
          }
      });

      mAdView.loadAd(adRequest);

      //return  mAdView;
  }









}
