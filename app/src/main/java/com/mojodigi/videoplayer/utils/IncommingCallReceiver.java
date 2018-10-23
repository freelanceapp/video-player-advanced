package com.mojodigi.videoplayer.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.mojodigi.videoplayer.activity.PlayerActivity;

public class IncommingCallReceiver extends BroadcastReceiver
{

    Context mContext;
    static PlayerActivity p;


    public static void ActivityInstance(PlayerActivity activity)
    {
        p=activity;
    }

    @Override
    public void onReceive(Context mContext, Intent intent)
    {
        try
        {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);



            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
//                Toast.makeText(mContext, "Phone Is Ringing", Toast.LENGTH_LONG).show();
                // Your Code
                p.pausee();
            }

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
//                Toast.makeText(mContext, "Call Recieved", Toast.LENGTH_LONG).show();
                // Your Code
                p.pausee();

            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {

//                Toast.makeText(mContext, "Phone Is Idle", Toast.LENGTH_LONG).show();
                // Your Code
                p.play();

            }
        }
        catch(Exception e)
        {
            //your custom message
            e.printStackTrace();
        }

    }

}
