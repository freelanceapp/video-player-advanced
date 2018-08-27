package jmm.com.videoplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.utils.Helper;
import jmm.com.videoplayer.utils.Utilities;


public class PlayerActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView videoSurface1;
    MediaPlayer mediaPlayer;
    String viewSource, viewName, viewSize/*= "/storage/emulated/0/Download/wtgdggd fjgdhhdg fdufsgdfjjh vudgkjx gdujhfjiugbuf.mp4"*/;
    ImageView btnPlay, btnRewind, btn_next, img_screenorientation;
    SurfaceHolder videoHolder1;
    ImageView img_back_player;
    TextView txt_playername, txt_starttime, txt_endtime;
    SeekBar seekBar;
    int flag = 0;
    Utilities utils;
    Handler handler;
    int click = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay = findViewById(R.id.btn1);
        btnRewind = findViewById(R.id.btn3);
        btn_next = findViewById(R.id.btn_next);
        seekBar = findViewById(R.id.seekBar);
        img_back_player = findViewById(R.id.img_back_player);
        txt_playername = findViewById(R.id.txt_playername);
        txt_starttime = findViewById(R.id.txt_starttime);
        txt_endtime = findViewById(R.id.txt_endtime);
        img_screenorientation = findViewById(R.id.img_screenorientation);
        viewSource = getIntent().getStringExtra("source");
        viewName = getIntent().getStringExtra("name");
        viewSize = getIntent().getStringExtra("size");

        txt_playername.setText(viewName);

        videoSurface1 = findViewById(R.id.surfaceView);
        videoHolder1 = videoSurface1.getHolder();
        videoHolder1.addCallback(this);
        videoHolder1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        utils = new Utilities();
        updateProgressBar();

        img_back_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    play();

                } else {
                    btnPlay.setImageResource(R.drawable.play_h);
                    flag = 1;
                    mediaPlayer.pause();


                }

            }
        });

    /*    btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                player1.pause();
            }
        });*/

        img_screenorientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = PlayerActivity.this;
                if(Helper.getScreenOrientation(activity) == "Landscape")
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else if (Helper.getScreenOrientation(activity) == "Portrait")
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (mediaPlayer == null) {
                    return;
                }
                int pos = mediaPlayer.getCurrentPosition();
                pos -= 5000;
                mediaPlayer.seekTo(pos);

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mediaPlayer == null) {
                    return;
                }
                int pos = mediaPlayer.getCurrentPosition();
                pos += 15000;
                mediaPlayer.seekTo(pos);
            }
        });

    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        play();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        mediaPlayer.pause();
    }

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
        mediaPlayer.start();
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            txt_endtime.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            if (txt_starttime.getText().toString().equals(txt_endtime.getText().toString())) {
                btnPlay.setImageResource(R.drawable.play_h);
                flag = 1;
            } else {
                txt_starttime.setText("" + utils.milliSecondsToTimer(currentDuration));

            }

            // Updating progress bar
            int progress = (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };


    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }


}