package jmm.com.videoplayer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.adapter.FavrtAdapter;
import jmm.com.videoplayer.adapter.ShowVideoAdapter;
import jmm.com.videoplayer.model.ShowVideo;
import jmm.com.videoplayer.utils.AddMobUtils;
import jmm.com.videoplayer.utils.Helper;
import jmm.com.videoplayer.utils.OnSwipeTouchListener;
import jmm.com.videoplayer.utils.Utilities;


public class PlayerActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    MediaPlayer mediaPlayer;
    String viewSource, viewName, viewSize;
    ImageView btnPlay, btn_previous, btn_next, img_screenorientation, img_mute;
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
    LinearLayout ll_brightness, ll_volume, ll_controls, ll_touch, ll_touch1, ll_videoname;
    int brightnessValue;
    private AudioManager audioManager = null;
    long totalDuration;
    boolean hasActiveHolder;
    SharedPreferences preferences;
    int outsideAppFlag = 0;
    String durationoutside;
    private int fromOutside;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mAdView = (AdView) findViewById(R.id.adView);
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        utils = new Utilities();

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


        //set font style
        Typeface font = Typeface.createFromAsset(getAssets(), "PoetsenOne-Regular.ttf");
        txt_playername.setTypeface(font);

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

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//            viewSize = preferences.getString("size", "");
            curVolume = preferences.getString("volume", "");
            txt_volume.setText(curVolume);

            //size of arraylist for forward video
            if (type.equals("all")) {
                viewSize = String.valueOf(ShowVideoAdapter.size);
            } else {
                viewSize = String.valueOf(FavrtAdapter.size);

            }
        }


        //
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
        int brightness = getScreenBrightness();
        sb_brightness.setProgress(brightness);
        txt_brightness.setText(brightness / 16 + "%");

        //audia seekbar
        volumeControl();

        //change video on swipe
        surfaceView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
            }

            public void onSwipeRight() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
                previous();
            }

            public void onSwipeLeft() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
                next();
            }

            public void onSwipeBottom() {
                sb_brightness.setVisibility(View.INVISIBLE);
                sb_volume.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
                txt_volume.setVisibility(View.INVISIBLE);
            }
        });

        ll_brightness.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb_brightness.setVisibility(View.VISIBLE);
                txt_brightness.setVisibility(View.VISIBLE);
                return false;
            }
        });

        ll_volume.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sb_volume.setVisibility(View.VISIBLE);
                txt_volume.setVisibility(View.VISIBLE);
                return false;
            }
        });

        ll_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (touch) {
                    ll_controls.setVisibility(View.VISIBLE);
                    ll_videoname.setVisibility(View.VISIBLE);
                    touch = false;
                } else {
                    ll_controls.setVisibility(View.GONE);
                    ll_videoname.setVisibility(View.GONE);
                    touch = true;
                }
            }
        });
        ll_touch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (touch) {
                    ll_controls.setVisibility(View.VISIBLE);
                    ll_videoname.setVisibility(View.VISIBLE);
                    touch = false;
                } else {
                    ll_controls.setVisibility(View.GONE);
                    ll_videoname.setVisibility(View.GONE);
                    touch = true;

                }
            }
        });


        updateProgressBar();

        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txt_brightness.setText(i / 16 + "%");
                setScreenBrightness(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_brightness.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_brightness.setVisibility(View.INVISIBLE);
                txt_brightness.setVisibility(View.INVISIBLE);
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
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (outsideAppFlag == 1) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_DEFAULT);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);

                } else {
                    startActivity(new Intent(PlayerActivity.this, HomeActivity.class));

                }
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
                handler.removeCallbacks(mUpdateTimeTask);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);

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
                    btnPlay.setImageResource(R.drawable.pause_hw);
                    flag = 0;


                    play();

                } else {
                    btnPlay.setImageResource(R.drawable.play_h);
                    flag = 1;
                    mediaPlayer.pause();
                }

            }
        });

        img_screenorientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = PlayerActivity.this;
                if (Helper.getScreenOrientation(activity) == "Landscape") {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ll_controls.setVisibility(View.VISIBLE);
                    ll_videoname.setVisibility(View.VISIBLE);

                } else if (Helper.getScreenOrientation(activity) == "Portrait") {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    touch = false;
                    ll_controls.setVisibility(View.GONE);
                    ll_videoname.setVisibility(View.GONE);
                }
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
/*        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        PrintMSg("surfaceChanged executed");
        //  play();    //T

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

//        try {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//            }
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mediaPlayer.setDisplay(surfaceHolder);
        play();

        PrintMSg("surface created executed");
        System.out.print("in surface created");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        mediaPlayer.pause();
//        try {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//            }
//            mediaPlayer = new MediaPlayer();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        PrintMSg("surface destroyed executed");
        System.out.print("in surface destroyed");
    }


    //function to play video
    public void play() {

        try {
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


        if (outsideAppFlag == 1) {
            totalDuration = Long.parseLong(durationoutside);

        } else {
            totalDuration = mediaPlayer.getDuration();

        }
        txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));
        mediaPlayer.start();

    }

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

    //set duration of seekbar
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
//            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = (mediaPlayer.getCurrentPosition());
            Log.i("time", "" + currentDuration);

            // Displaying Total Duration time
//            txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));

            // Displaying time completed playing
            if (txt_starttime.getText().toString().equals(txt_endtime.getText().toString())) {
                //autoplay next video
                if (currentindex == (size - 1)) {
                    startActivity(new Intent(PlayerActivity.this, HomeActivity.class));
                    finish();
                    return;
                } else {
                    next();
                }
            } else {
                txt_starttime.setText("" + utils.milliSecondsToTimer(currentDuration + 1));
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

    public void next() {
        currentindex++;
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
                Log.i("prerna", "" + utils.milliSecondsToTimer(totalDuration));

                playonPuase();
            }

        } else if (currentindex == size) {
            btn_next.setEnabled(false);
//            Toast.makeText(PlayerActivity.this, "No Video Available", Toast.LENGTH_SHORT).show();
        } else if (currentindex > size) {
//            Toast.makeText(PlayerActivity.this, "No Video Available", Toast.LENGTH_SHORT).show();
            currentindex--;
        }
    }

    public void previous() {
        currentindex--;
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
            btn_previous.setEnabled(false);
//            Toast.makeText(PlayerActivity.this, "No Video Available", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PrintMSg("Onstop executed");

    }

    @Override
    protected void onStart() {
        super.onStart();

        PrintMSg("onstart executed");
    }

    private void PrintMSg(String msg) {
        System.out.print("" + msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PrintMSg("ondestroy");
    }

    @Override
    protected void onPause() {

        super.onPause();

        mediaPlayer.pause();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        if (outsideAppFlag == 1) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_DEFAULT);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

        } else {
            startActivity(new Intent(PlayerActivity.this, HomeActivity.class));

        }
    }


    @Override
    protected void onRestart() {
        // play();
        super.onRestart();
//         mediaPlayer.setDisplay(surfaceHolder);  T

        fromOutside = 1;
        PrintMSg("OnRestatt executed");
    }

    @Override
    protected void onResume() {

        super.onResume();
        AddMobUtils adutil = new AddMobUtils();
        adutil.displayBannerAdd(mAdView);

        //  play();
        PrintMSg("OnResume executed");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music
                    mediaPlayer.pause();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    play();

                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    mediaPlayer.pause();

                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        //T
        else {
            play();
        }
        //T

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

        if (brightnessValue >= 0 && brightnessValue <= 255) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
        }
    }

    protected int getScreenBrightness() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else {
            brightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        }
        return brightnessValue;
    }

    private void volumeControl() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            sb_volume.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            sb_volume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            sb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                    sb_volume.setVisibility(View.INVISIBLE);
                    txt_volume.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                    txt_volume.setText(progress + "%");

                    preferences = PreferenceManager.getDefaultSharedPreferences(PlayerActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("volume", "" + txt_volume.getText().toString());
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