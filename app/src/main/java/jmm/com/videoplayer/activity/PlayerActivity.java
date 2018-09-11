package jmm.com.videoplayer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
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

import java.io.IOException;
import java.util.ArrayList;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.model.ShowVideo;
import jmm.com.videoplayer.utils.Helper;
import jmm.com.videoplayer.utils.OnSwipeTouchListener;
import jmm.com.videoplayer.utils.Utilities;


public class PlayerActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView videoSurface1;
    MediaPlayer mediaPlayer;
    String viewSource, viewName, viewSize;
    ImageView btnPlay, btn_previous, btn_next, img_screenorientation, img_mute;
    SurfaceHolder videoHolder1;
    ImageView img_back_player;
    TextView txt_playername, txt_starttime, txt_endtime, txt_brightness, txt_volume;
    SeekBar seekBar, sb_brightness, sb_volume;
    int flag = 0, flag1 = 0;
    Utilities utils;
    boolean touch = true;
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
    boolean pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        //get values from intent
        viewSource = getIntent().getStringExtra("source");
        viewName = getIntent().getStringExtra("name");
        current = getIntent().getStringExtra("current");
        type = getIntent().getStringExtra("type");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        viewSize = preferences.getString("size", "");
        curVolume = preferences.getString("volume", "");
        txt_volume.setText(curVolume);

        //covnerting values
        size = Integer.parseInt(viewSize);
        currentindex = Integer.parseInt(current);

        //audio
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

        txt_playername.setText(viewName);

        videoSurface1 = findViewById(R.id.surfaceView);
        videoHolder1 = videoSurface1.getHolder();
        videoHolder1.addCallback(this);
        videoHolder1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //get current brightness
        int brightness = getScreenBrightness();
        sb_brightness.setProgress(brightness);
        txt_brightness.setText(brightness + "%");

        //audia seekbar
        volumeControl();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sb_volume.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        Log.i("volume", "" + curVolume);
        sb_volume.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));

        //change video on swipe
        videoSurface1.setOnTouchListener(new OnSwipeTouchListener(this) {
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


        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        utils = new Utilities();
        updateProgressBar();


        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txt_brightness.setText(i + "%");
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
                startActivity(new Intent(PlayerActivity.this, HomeActivity.class));
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
                    pause = false;

                    play();

                } else {
                    btnPlay.setImageResource(R.drawable.play_h);
                    flag = 1;
                    mediaPlayer.pause();
                    pause = true;
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
        pause = false;
        play();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();


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
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //function to play video
    public void play() {

        try {
            mediaPlayer.setDataSource(viewSource);
            mediaPlayer.setDisplay(videoHolder1);
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
        txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration - 1));
        String end = txt_endtime.getText().toString();
        Log.i("time", "" + end);
        mediaPlayer.start();

    }

    //set duration of seekbar
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
//            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
//            txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));

            // Displaying time completed playing
            if (txt_starttime.getText().toString().equals(txt_endtime.getText().toString())) {
                //autoplay next video
                next();
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

    public void next() {


        currentindex++;
        btn_previous.setEnabled(true);
        //condition for next video...

        if (currentindex < size) {

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
            //change video url
            if (type.equals("all")) {
                viewSource = HomeActivity.arrayList.get(currentindex).getFolder();
                viewName = HomeActivity.arrayList.get(currentindex).getName();
                txt_playername.setText(viewName);

            } else if (type.equals("favrt")) {
                viewSource = HomeActivity.favrtArrayList.get(currentindex).getFolder();
                viewName = HomeActivity.favrtArrayList.get(currentindex).getName();
                txt_playername.setText(viewName);
            }
            
            play();

        } else if (currentindex == size) {
            btn_next.setEnabled(false);
            Toast.makeText(PlayerActivity.this, "No Video Available", Toast.LENGTH_LONG).show();
        }



    }

    public void previous() {
        currentindex--;
        btn_next.setEnabled(true);
        if (currentindex > -1) {

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

            //change video url
            if (type.equals("all")) {
                viewSource = HomeActivity.arrayList.get(currentindex).getFolder();
                viewName = HomeActivity.arrayList.get(currentindex).getName();
                txt_playername.setText(viewName);
                pause = false;
                play();
            } else {
                viewSource = HomeActivity.favrtArrayList.get(currentindex).getFolder();
                viewName = HomeActivity.favrtArrayList.get(currentindex).getName();
                txt_playername.setText(viewName);
                pause = false;
                play();
            }

        } else if (currentindex == -1) {
            btn_previous.setEnabled(false);
            Toast.makeText(PlayerActivity.this, "No Video Available", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.pause();
        pause = true;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        pause = true;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(PlayerActivity.this, HomeActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//        play();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            img_mute.setImageResource(R.drawable.unmute);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setScreenBrightness(int brightnessValue) {

        if (brightnessValue >= 0 && brightnessValue <= 100) {
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
//            sb_volume.setMax(100);
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

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PlayerActivity.this);
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