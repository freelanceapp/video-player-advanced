package com.mojodigi.videoplayer.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.Random;

import com.mojodigi.videoplayer.R;
import com.mojodigi.videoplayer.adapter.FavrtAdapter;
import com.mojodigi.videoplayer.adapter.ShowVideoAdapter;
import com.mojodigi.videoplayer.utils.AddMobUtils;
import com.mojodigi.videoplayer.utils.Helper;
import com.mojodigi.videoplayer.utils.IncommingCallReceiver;
import com.mojodigi.videoplayer.utils.MyPreference;
import com.mojodigi.videoplayer.utils.OnSwipeTouchListener;
import com.mojodigi.videoplayer.utils.Utilities;


public class PlayerActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    MediaPlayer mediaPlayer;
    String viewSource, viewName, viewSize;
    ImageView img_pauseimage, btnPlay, btn_previous, btn_next, img_screenorientation,
            img_mute, img_bright_plus, img_bright_minus, img_volume_plus, img_volume_minus;
    SurfaceHolder surfaceHolder;
    ImageView img_back_player;
    TextView txt_playername, txt_starttime, txt_endtime, txt_brightness, txt_volume;
    SeekBar seekBar, sb_brightness, sb_volume;
    int flag = 0, flag1 = 0;
    Utilities utils;
    boolean touch = false;
    Handler handler;
    String current, type;
    int currentindex;
    int size;
    String curVolume;
    int volume;
    LinearLayout ll_brightness, ll_volume, ll_controls, ll_touch, ll_touch1, ll_videoname;
    int brightnessValue;
    private AudioManager audioManager = null;
    long totalDuration;
    boolean hasActiveHolder;
    SharedPreferences preferences;
    int outsideAppFlag = 0;
    String durationoutside;
    public int fromOutside;
    private AdView mAdView;
    int touchcount = 0;
    String playtype;
    Random rn = new Random();
    Activity activity;
    MyPreference myPreference = new MyPreference(this);
    int count = 0;
    GestureDetector mDetector1;
    int ii;
    int brightness;
    int mode = 0;
    LinearLayout mainview;
    private int postion;
    boolean ispaused;
    int volume_level;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mAdView = findViewById(R.id.adView);

        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        utils = new Utilities();
        activity = PlayerActivity.this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        btnPlay = findViewById(R.id.btn1);
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);
        seekBar = findViewById(R.id.seekBar);
        img_back_player = findViewById(R.id.img_back_player);
        txt_playername = findViewById(R.id.txt_playername);
        txt_starttime = findViewById(R.id.txt_starttime);
        txt_endtime = findViewById(R.id.txt_endtime);
        img_mute = findViewById(R.id.img_mute);
        img_screenorientation = findViewById(R.id.img_screenorientation);
        sb_brightness = findViewById(R.id.sb_brightness);
        txt_brightness = findViewById(R.id.txt_brightness);
        ll_brightness = findViewById(R.id.ll_brightness);
        ll_volume = findViewById(R.id.ll_volume);
        sb_volume = findViewById(R.id.sb_volume);
        txt_volume = findViewById(R.id.txt_volume);
        ll_controls = findViewById(R.id.ll_controls);

        ll_touch = findViewById(R.id.ll_touch);
        ll_touch1 = findViewById(R.id.ll_touch1);
        ll_videoname = findViewById(R.id.ll_videoname);
        img_bright_plus = findViewById(R.id.img_bright_plus);
        img_bright_minus = findViewById(R.id.img_bright_minus);
        img_volume_plus = findViewById(R.id.img_volume_plus);
        img_volume_minus = findViewById(R.id.img_volume_minus);
        img_pauseimage = findViewById(R.id.img_pauseimage);
        mainview = findViewById(R.id.mainview);


        txt_playername.setTypeface(Helper.typeFace_adobe_caslonpro_Regular(this));
        txt_volume.setTypeface(Helper.typeFace_FFF(this));
        txt_brightness.setTypeface(Helper.typeFace_FFF(this));

        //set font style
//        Typeface font = Typeface.createFromAsset(getAssets(), "PoetsenOne-Regular.ttf");
        txt_playername.setTypeface(Helper.typeFace_adobe_caslonpro_Regular(this));

//play type
        playtype = myPreference.getPlaytype(MyPreference.PREFS_NAME);


        //play video from outside the application
        Intent intent = getIntent();
        String action = intent.getAction();
        String typee = intent.getType();

        if (Intent.ACTION_VIEW.equals(action) && typee != null) {
            if (typee.contains("video")) {

                btn_next.setEnabled(false);
                btn_previous.setEnabled(false);
                outsideAppFlag = 1;
                Uri videoUri = intent.getData();
                String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DISPLAY_NAME};
                Cursor cursor = this.getContentResolver().query(videoUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int column_index1 = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int column_index2 = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                cursor.moveToNext();
                String path = cursor.getString(column_index);
                durationoutside = cursor.getString(column_index1);
                String name = cursor.getString(column_index2);


                viewSource = path;
                viewName = name;
                viewSize = "1";
                current = "0";

            }
        } else {
            //get values from intent
            outsideAppFlag = 0;
            viewSource = getIntent().getStringExtra("source");
            viewName = getIntent().getStringExtra("name");
            current = getIntent().getStringExtra("current");
            type = getIntent().getStringExtra("type");


            SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putString("type", type);
            editor.apply();

            //size of arraylist for forward video
            if (type.equals("all")) {
                viewSize = String.valueOf(ShowVideoAdapter.size);
            } else {
                viewSize = String.valueOf(FavrtAdapter.size);
            }
        }

        //covnerting values
        size = Integer.parseInt(viewSize);
        currentindex = Integer.parseInt(current);

        //audio
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

        txt_playername.setText(viewName);

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //get current brightness
        brightness = getScreenBrightness();
        sb_brightness.setProgress(brightness);
//        txt_brightness.setText((int) (brightness / (float) 2.5) + "%");
        txt_brightness.setText(brightness + "%");

        //get current volume
        volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume = volume_level;

        //audia seekbar
        volumeControl();

        //change video on swipe
        surfaceView.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeTop() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
                img_bright_plus.setVisibility(View.INVISIBLE);
                img_bright_minus.setVisibility(View.INVISIBLE);
                img_volume_plus.setVisibility(View.INVISIBLE);
                img_volume_minus.setVisibility(View.INVISIBLE);

            }

            public void onSwipeRight() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
                img_bright_plus.setVisibility(View.INVISIBLE);
                img_bright_minus.setVisibility(View.INVISIBLE);
                img_volume_plus.setVisibility(View.INVISIBLE);
                img_volume_minus.setVisibility(View.INVISIBLE);
                previous();
            }

            public void onSwipeLeft() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
                img_bright_plus.setVisibility(View.INVISIBLE);
                img_bright_minus.setVisibility(View.INVISIBLE);
                img_volume_plus.setVisibility(View.INVISIBLE);
                img_volume_minus.setVisibility(View.INVISIBLE);
                next();
            }

            public void onSwipeBottom() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
                img_bright_plus.setVisibility(View.INVISIBLE);
                img_bright_minus.setVisibility(View.INVISIBLE);
                img_volume_plus.setVisibility(View.INVISIBLE);
                img_volume_minus.setVisibility(View.INVISIBLE);
            }
        });


        ll_brightness.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb_brightness.setVisibility(View.VISIBLE);
                txt_brightness.setVisibility(View.VISIBLE);
                img_bright_plus.setVisibility(View.VISIBLE);
                img_bright_minus.setVisibility(View.VISIBLE);
                ll_controls.setVisibility(View.GONE);
                ll_videoname.setVisibility(View.GONE);

                sb_brightness.postDelayed(new Runnable() {
                    public void run() {
                        sb_brightness.setVisibility(View.INVISIBLE);
                        txt_brightness.setVisibility(View.INVISIBLE);
                        img_bright_plus.setVisibility(View.INVISIBLE);
                        img_bright_minus.setVisibility(View.INVISIBLE);
                    }
                }, 3000);

                return false;
            }


        });


        ll_volume.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb_volume.setVisibility(View.VISIBLE);
                txt_volume.setVisibility(View.VISIBLE);
                img_volume_plus.setVisibility(View.VISIBLE);
                img_volume_minus.setVisibility(View.VISIBLE);
                ll_controls.setVisibility(View.GONE);
                ll_videoname.setVisibility(View.GONE);


                sb_volume.postDelayed(new Runnable() {
                    public void run() {
                        sb_volume.setVisibility(View.INVISIBLE);
                        txt_volume.setVisibility(View.INVISIBLE);
                        img_volume_plus.setVisibility(View.INVISIBLE);
                        img_volume_minus.setVisibility(View.INVISIBLE);

                    }
                }, 3000);
                return false;
            }
        });


        ll_touch.setOnTouchListener(new View.OnTouchListener() {

            private GestureDetector gestureDetector = new GestureDetector(PlayerActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
/*
                    if (touchcount == 0) {

                        ll_controls.setVisibility(View.GONE);
                        ll_videoname.setVisibility(View.GONE);
                        touchcount = 1;

                    } else {
                        ll_controls.setVisibility(View.VISIBLE);
                        ll_videoname.setVisibility(View.VISIBLE);

                        touchcount = 0;
                    }*/


                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {

                    if (ll_controls.getVisibility() == View.VISIBLE) {

                        mediaPlayer.pause();
                        ispaused = true;
                        img_pauseimage.setVisibility(View.VISIBLE);
                        btnPlay.setImageResource(R.drawable.play);
                        ll_controls.setVisibility(View.GONE);
                        ll_videoname.setVisibility(View.GONE);
                        flag = 1;
                        handler.removeCallbacks(mUpdateTimeTask);


                    } else if (ll_controls.getVisibility() == View.GONE) {

                        if (img_pauseimage.getVisibility() == View.VISIBLE) {
                            ispaused = false;
                            flag = 0;

                            play();
                            play();
                            mediaPlayer.seekTo(postion);

                            img_pauseimage.setVisibility(View.GONE);
                            btnPlay.setImageResource(R.drawable.pause);
                            updateProgressBar();
                        } else {
                            ll_controls.setVisibility(View.VISIBLE);
                            ll_videoname.setVisibility(View.VISIBLE);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ll_controls.setVisibility(View.GONE);
                                ll_videoname.setVisibility(View.GONE);

                            }
                        }, 5000);

                    }

               /*     if (touch) {
                        btnPlay.setImageResource(R.drawable.pause);
                        img_pauseimage.setVisibility(View.INVISIBLE);
                        flag = 0;
                        play();
                        touch = false;
                    } else {
                        mediaPlayer.pause();
                        btnPlay.setImageResource(R.drawable.play);
                        img_pauseimage.setVisibility(View.VISIBLE);
                        flag = 1;
                        touch = true;

                    }*/

                    return super.onSingleTapConfirmed(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent e) {

                gestureDetector.onTouchEvent(e);
                return true;
            }
        });

        //ts

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {

                return false;
            }
        });
        //ts


        ll_touch1.setOnTouchListener(new View.OnTouchListener() {

            private GestureDetector gestureDetector = new GestureDetector(PlayerActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");

                  /*  if (touchcount == 0) {
                        ll_controls.setVisibility(View.GONE);
                        ll_videoname.setVisibility(View.GONE);
                        touchcount = 1;
                    } else {
                        ll_controls.setVisibility(View.VISIBLE);
                        ll_videoname.setVisibility(View.VISIBLE);
                        touchcount = 0;
                    }*/
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {

                    if (ll_controls.getVisibility() == View.VISIBLE) {

                        mediaPlayer.pause();
                        ispaused = true;

                        img_pauseimage.setVisibility(View.VISIBLE);
                        btnPlay.setImageResource(R.drawable.play);
                        ll_controls.setVisibility(View.GONE);

                        ll_videoname.setVisibility(View.GONE);
                        flag = 1;
                        handler.removeCallbacks(mUpdateTimeTask);


                    } else if (ll_controls.getVisibility() == View.GONE) {

                        if (img_pauseimage.getVisibility() == View.VISIBLE) {

                            ispaused = false;
                            flag = 0;
                            play();
                            play();
                            img_pauseimage.setVisibility(View.GONE);
                            btnPlay.setImageResource(R.drawable.pause);
                            mediaPlayer.seekTo(postion);
                            updateProgressBar();

                        } else {

                            ll_controls.setVisibility(View.VISIBLE);
                            ll_videoname.setVisibility(View.VISIBLE);

                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                ll_controls.setVisibility(View.GONE);
                                ll_videoname.setVisibility(View.GONE);

                            }
                        }, 5000);

                    }


                  /*  if (touch) {
                        btnPlay.setImageResource(R.drawable.pause);
                        img_pauseimage.setVisibility(View.INVISIBLE);

                        flag = 0;

                        play();
                        touch = false;
                    } else {
                        mediaPlayer.pause();
                        btnPlay.setImageResource(R.drawable.play);
                        img_pauseimage.setVisibility(View.VISIBLE);

                        flag = 1;
                        touch = true;

                    }*/
                    return super.onSingleTapConfirmed(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent e) {

                gestureDetector.onTouchEvent(e);
                return true;
            }
        });


        updateProgressBar();

        sb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                sb_volume.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
                img_volume_plus.setVisibility(View.INVISIBLE);
                img_volume_minus.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                sb_volume.setVisibility(View.VISIBLE);

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                volume = progress;
                sb_volume.setProgress(volume);
                txt_volume.setText((int) ((volume) * 6.7) + "%");

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("volume", "" + progress);
                editor.apply();

                img_mute.setImageResource(R.drawable.unmute);

                if (txt_volume.getText().toString().equals("0%")) {
                    img_mute.setImageResource(R.drawable.mute);
                    flag1 = 1;
                } else {
                    img_mute.setImageResource(R.drawable.unmute);
                    flag1 = 0;
                }
            }
        });


        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


                ii = (int) (i / (float) 2.5);
                sb_brightness.setProgress(i);
                brightness = i;
                txt_brightness.setText(i + "%");
                setScreenBrightness(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //mode for brightness
                try {
                    mode = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if (mode == 1) {
                    Snackbar snackbar = Snackbar
                            .make(mainview, "Auto Brightness Mode ON", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


                sb_brightness.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                sb_brightness.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                img_bright_plus.setVisibility(View.INVISIBLE);
                img_bright_minus.setVisibility(View.INVISIBLE);
            }
        });

        img_bright_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mode for brightness
                try {
                    mode = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if (mode == 1) {
                    Snackbar snackbar = Snackbar
                            .make(mainview, "Auto Brightness Mode ON", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                sb_brightness.setMax(250);
                sb_brightness.setProgress(brightness + 25);
                txt_brightness.setText((int) (brightness / (float) 2.5) + "%");
                setScreenBrightness(brightness);
            }
        });
        img_bright_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mode for brightness
                try {
                    mode = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if (mode == 1) {
                    Snackbar snackbar = Snackbar
                            .make(mainview, "Auto Brightness Mode ON", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


                sb_brightness.setProgress(brightness - 25);
                txt_brightness.setText((int) (brightness / (float) 2.5) + "%");
                setScreenBrightness(brightness);
            }
        });

        img_volume_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (volume <= 14)
                    volume = volume + 1;
                sb_volume.setProgress(volume);
                txt_volume.setText((int) ((volume) * 6.7) + "%");

            }
        });
        img_volume_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (volume >= 1)
                    volume = volume - 1;
                sb_volume.setProgress(volume);
                txt_volume.setText((int) (volume * 6.7) + "%");

            }
        });

        //for mute video
        img_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag1 == 1) {
                    //unmute
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    img_mute.setImageResource(R.drawable.unmute);
                    flag1 = 0;

                } else {
                    //mute
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    img_mute.setImageResource(R.drawable.mute);
                    flag1 = 1;
                }
            }
        });
        img_back_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(mUpdateTimeTask);

                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                finish();
              /*  if (outsideAppFlag == 1) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_DEFAULT);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);


                } else {
                    finish();
//                    startActivity(new Intent(PlayerActivity.this, HomeActivity.class));

                }*/
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i * 1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                ll_controls.setVisibility(View.VISIBLE);
                handler.removeCallbacks(mUpdateTimeTask);
               /* mediaPlayer.pause();
                img_pauseimage.setVisibility(View.VISIBLE);
                btnPlay.setImageResource(R.drawable.play);
                flag=1;
                touch=true;*/
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(mUpdateTimeTask);

                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);
                postion = currentPosition;

                // update timer progress again
                updateProgressBar();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (flag == 1) {
                    if (txt_starttime.getText().toString().equals(txt_endtime.getText().toString())) {
                        txt_starttime.setText("0.0");
                    }

                    btnPlay.setImageResource(R.drawable.pause);
                    img_pauseimage.setVisibility(View.INVISIBLE);
                    flag = 0;
                    ispaused = false;
                    play();   //pause button
                    mediaPlayer.seekTo(postion);
                    updateProgressBar();


                } else {

                    btnPlay.setImageResource(R.drawable.play);
                    img_pauseimage.setVisibility(View.VISIBLE);
                    flag = 1;
                    mediaPlayer.pause();
                    ispaused = true;
                    handler.removeCallbacks(mUpdateTimeTask);

                }

            }
        });

        img_screenorientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int _newConfig = activity.getResources().getConfiguration().orientation;
                if (_newConfig == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ll_controls.setVisibility(View.GONE);
                    ll_videoname.setVisibility(View.GONE);
                    touchcount = 1;
//                    setFullVideoSize();
                    setVideoSize();

                }

                if (_newConfig == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    ll_controls.setVisibility(View.GONE);
                    ll_videoname.setVisibility(View.GONE);
                    touchcount = 0;
                    setVideoSize();

                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);


                    }
                }, 3000);
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                previous();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                next();

            }
        });


    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mediaPlayer.setDisplay(surfaceHolder);
        img_pauseimage.setVisibility(View.INVISIBLE);
        btnPlay.setImageResource(R.drawable.pause);


//        https://www.programering.com/a/MDO3EjNwATY.html
//        for resume funtionality
        /*play();
        mediaPlayer.seekTo(postion);*/


        if (ispaused) {
            mediaPlayer.pause();
            btnPlay.setImageResource(R.drawable.play);
            img_pauseimage.setVisibility(View.VISIBLE);
//            ispaused = false;
            flag = 1;
        } else {

            play();
            mediaPlayer.seekTo(postion);
            btnPlay.setImageResource(R.drawable.pause);
            img_pauseimage.setVisibility(View.GONE);

            flag = 0;
        }


        int or = HomeActivity.orientation;
        if (or == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ll_controls.setVisibility(View.GONE);
            ll_videoname.setVisibility(View.GONE);
            touchcount = 1;
            setVideoSize();

        }
        setVideoSize();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            }
        }, 2000);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int _newConfig = activity.getResources().getConfiguration().orientation;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ll_videoname.setVisibility(View.GONE);
            ll_controls.setVisibility(View.GONE);
            touchcount = 1;
            setVideoSize();

        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ll_videoname.setVisibility(View.GONE);
            ll_controls.setVisibility(View.GONE);
            touchcount = 1;
//            setFullVideoSize();
            setVideoSize();


        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        mediaPlayer.pause();


    }


    //function to play video
    public void play() {

        try {
          /*  //ts
            Uri vdouri=Uri.parse(viewSource);
            mediaPlayer.create(PlayerActivity.this, vdouri);
           //ts*/
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setDataSource(viewSource);
            mediaPlayer.prepare();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (outsideAppFlag == 1) {
            totalDuration = Long.parseLong(durationoutside);

        } else {
            totalDuration = mediaPlayer.getDuration();

        }
        txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));
        mediaPlayer.start();


        if (ispaused) {
            pausee();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            ispaused=false;

        } else {


            mediaPlayer.start();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        }

    }

    //pause in case of call
    public void pausee() {
        try {
            //  mediaPlayer = new MediaPlayer();
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
//            postion = mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //next and previous in pause state
    public void playonPuase() {

        try {

            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(viewSource);
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepare();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalDuration = mediaPlayer.getDuration();
        txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));
        mediaPlayer.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 1s
                mediaPlayer.pause();
            }
        }, 200);

    }


    //for video without straching :original size
    private void setVideoSize() {

        // // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        surfaceView.setLayoutParams(lp);
    }


    //for full screen video
    public void setFullVideoSize() {
        // // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        lp.width = screenWidth;
        lp.height = screenHeight;

        // Commit the layout parameters
        surfaceView.setLayoutParams(lp);
    }

    //set duration of seekbar
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
//            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = (mediaPlayer.getCurrentPosition());
            postion = (int) currentDuration;
            Log.i("time", "" + currentDuration);

            // Displaying Total Duration time
//            txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));

            // Displaying time completed playing
            if (txt_starttime.getText().toString().equals(txt_endtime.getText().toString())) {
                //autoplay next video

                if (outsideAppFlag == 1) {
                    finish();
                    return;
                } else {
                    if (currentindex == (size - 1)) {
                        if (playtype.equalsIgnoreCase("Loop")) {
                            txt_starttime.setText("0.0");
                            mediaPlayer.setLooping(true);
                            txt_starttime.setText("" + utils.milliSecondsToTimer(currentDuration));
                        } else if (playtype.equalsIgnoreCase("Random")) {
                            if (size == 1) {
                                finish();
                            } else
                                next();
                        } else {
//                            startActivity(new Intent(PlayerActivity.this, HomeActivity.class));
                            finish();
                            return;

                        }
                    } else {
                        if (playtype.equalsIgnoreCase("Loop")) {
                            txt_starttime.setText("0.0");
                            mediaPlayer.setLooping(true);
                            txt_starttime.setText("" + utils.milliSecondsToTimer(currentDuration));

//                            txt_starttime.setText("" + utils.milliSecondsToTimer(0));

                        } else {
                            next();
                        }
                    }
                }

            } else {

                txt_starttime.setText("" + utils.milliSecondsToTimer(currentDuration));

            }

            // Updating progress bar
            int progress = (utils.getProgressPercentage(currentDuration, totalDuration));
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };


    //update time by 1 second
    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }


    //for next video
    public void next() {

        //playtype random
        if (playtype.equals("Random")) {
            currentindex = rn.nextInt(size);
        } else {
            currentindex++;
        }

        btn_previous.setEnabled(true);
        //condition for next video...

        if (currentindex < size) {
            //change video url

            if (type.equals("all") && outsideAppFlag == 0) {
                viewSource = HomeActivity.arrayList.get(currentindex).getFolder().trim();
                viewName = HomeActivity.arrayList.get(currentindex).getName();
                txt_playername.setText(viewName);


            } else if (type.equals("favrt") && outsideAppFlag == 0) {
                viewSource = HomeActivity.favrtArrayList.get(currentindex).getFolder().trim();
                viewName = HomeActivity.favrtArrayList.get(currentindex).getName();
                txt_playername.setText(viewName);

            }

            if (mediaPlayer.isPlaying()) {
                //reset Media Player
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                play();

            } else {
                viewSource = HomeActivity.arrayList.get(currentindex).getFolder().trim();
                viewName = HomeActivity.arrayList.get(currentindex).getName();
                txt_playername.setText(viewName);
                txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));

                playonPuase();
            }

        } else if (currentindex == size) {
            if (playtype.equals("Random")) {
                btn_next.setEnabled(true);
            } else {
                btn_next.setEnabled(false);
            }
        } else if (currentindex > size) {
//            Toast.makeText(PlayerActivity.this, "No Video Available", Toast.LENGTH_SHORT).show();
            currentindex--;
        }
    }


    //for previous video
    public void previous() {

        //playtype random
        if (playtype.equals("Random")) {
            currentindex = rn.nextInt(size);
        } else {
            currentindex--;

        }

//        currentindex--;
        btn_next.setEnabled(true);
        if (currentindex > -1) {

            //change video url
            if (type.equals("all")) {
                viewSource = HomeActivity.arrayList.get(currentindex).getFolder().trim();
                viewName = HomeActivity.arrayList.get(currentindex).getName();
                txt_playername.setText(viewName);

            } else {
                viewSource = HomeActivity.favrtArrayList.get(currentindex).getFolder().trim();
                viewName = HomeActivity.favrtArrayList.get(currentindex).getName();
                txt_playername.setText(viewName);

            }


            if (mediaPlayer.isPlaying()) {
                //reset Media Player
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                play();
            } else {
                viewSource = HomeActivity.arrayList.get(currentindex).getFolder().trim();
                viewName = HomeActivity.arrayList.get(currentindex).getName();
                txt_playername.setText(viewName);

                playonPuase();
            }
        } else if (currentindex == -1) {
            if (playtype.equals("Random")) {
                btn_previous.setEnabled(true);
            } else {
                btn_previous.setEnabled(false);
            }
//            Toast.makeText(PlayerActivity.this, "No Video Available", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onBackPressed() {
        handler.removeCallbacks(mUpdateTimeTask);

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();

      /*  if (outsideAppFlag == 1) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_DEFAULT);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

        } else {
            finish();

        }*/
        super.onBackPressed();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        fromOutside = 1;
    }

    @Override
    protected void onPause() {

        if (mediaPlayer.isPlaying()) {
            // The location to save the currently playing
            postion = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        } else
            postion = mediaPlayer.getCurrentPosition();

        super.onPause();


        // mediaPlayer.pause();


    }

    @Override
    protected void onStop() {

        if (mediaPlayer.isPlaying()) {
            // The location to save the currently playing
            postion = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        } else
            postion = mediaPlayer.getCurrentPosition();
        super.onStop();
//        mediaPlayer.pause();


    }

    @Override
    protected void onResume() {


        super.onResume();

        IncommingCallReceiver.ActivityInstance(this);

        //adds
        AddMobUtils adutil = new AddMobUtils();
        adutil.displayBannerAdd(mAdView);

        //for screen on when video playing
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //call state
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music

                    mediaPlayer.pause();
//                    if (mediaPlayer.isPlaying()) {
//                        // The location to save the currently playing
//                        postion = mediaPlayer.getCurrentPosition();
//                        mediaPlayer.stop();
//                    } else
                    postion = mediaPlayer.getCurrentPosition();


                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
//                    if (ispaused)
                    play();
//                    mediaPlayer.seekTo(postion);//ts
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    mediaPlayer.pause();
//                    if (mediaPlayer.isPlaying()) {
//                        // The location to save the currently playing
//                        postion = mediaPlayer.getCurrentPosition();
//                        mediaPlayer.stop();
//                    } else
                    postion = mediaPlayer.getCurrentPosition();

                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mediaPlayer.pause();
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            if (!ispaused) {
                play();
                img_pauseimage.setVisibility(View.INVISIBLE);
            }
        }
//        }

    }

    @TargetApi(28)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            //high volume by device button
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);


            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            img_mute.setImageResource(R.drawable.unmute);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            //low volume by device button
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setScreenBrightness(int brightnessValue) {

        if (brightnessValue >= 0 && brightnessValue <= 250) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessValue);

        }
    }

    protected int getScreenBrightness() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else {
            try {
                brightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        return brightnessValue;
    }

    private void volumeControl() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            //for 100% volume
            sb_volume.setMax(15);


//            viewSize = preferences.getString("size", "");
            curVolume = preferences.getString("volume", "");
            sb_volume.setProgress(Integer.parseInt(curVolume));
            if (curVolume.equals("")) {
                txt_volume.setText((volume_level * 6.7) + "%");

            } else {
                txt_volume.setText((int) ((Integer.parseInt(curVolume)) * 6.7) + "%");
            }

            //for volume as AudioManager 1
            /*sb_volume.setMax(audioManager
              .getStreamMaxVolume(AudioManager.STREAM_MUSIC));*/
//            sb_volume.setProgress((int) (volume*6.7));

            sb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                    sb_volume.setVisibility(View.INVISIBLE);
                    txt_volume.setVisibility(View.INVISIBLE);
                    img_volume_plus.setVisibility(View.INVISIBLE);
                    img_volume_minus.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                    sb_volume.setVisibility(View.VISIBLE);

                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {


                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                    volume = progress;
                    sb_volume.setProgress(volume);
                    txt_volume.setText((int) ((volume) * 6.7) + "%");

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("volume", "" + progress);
                    editor.apply();

                    img_mute.setImageResource(R.drawable.unmute);

                    if (txt_volume.getText().toString().equals("0%")) {
                        img_mute.setImageResource(R.drawable.mute);
                        flag1 = 1;
                    } else {
                        img_mute.setImageResource(R.drawable.unmute);
                        flag1 = 0;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
