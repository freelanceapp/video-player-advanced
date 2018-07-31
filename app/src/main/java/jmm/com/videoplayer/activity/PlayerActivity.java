package jmm.com.videoplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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


public class PlayerActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView videoSurface1;
    MediaPlayer player1;
    String viewSource,viewName /*= "/storage/emulated/0/Download/wtgdggd fjgdhhdg fdufsgdfjjh vudgkjx gdujhfjiugbuf.mp4"*/;
    ImageView btnPlay,btnPause,btnRewind,btnForward;
    SurfaceHolder videoHolder1;
    ImageView img_back_player;
    TextView txt_playername,textvieww;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay =  findViewById(R.id.btn1);
        btnPause =  findViewById(R.id.btn2);
        btnRewind =  findViewById(R.id.btn3);
        btnForward =  findViewById(R.id.btn4);
        seekBar =  findViewById(R.id.seekBar);
        img_back_player =  findViewById(R.id.img_back_player);
        txt_playername =  findViewById(R.id.txt_playername);
        textvieww =  findViewById(R.id.textvieww);
        viewSource = getIntent().getStringExtra("prerna");
        viewName = getIntent().getStringExtra("prernaa");

        txt_playername.setText(viewName);

        videoSurface1 =  findViewById(R.id.surfaceView);
        videoHolder1 = videoSurface1.getHolder();
        videoHolder1.addCallback(this);
        videoHolder1.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        player1 = new MediaPlayer();

        img_back_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlayerActivity.this,HomeActivity.class));
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Toast.makeText(PlayerActivity.this, "prog", Toast.LENGTH_SHORT).show();
                if(player1!=null && b){

                    player1.seekTo(i*1000);
                }}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(PlayerActivity.this, "strt", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(PlayerActivity.this, "stp", Toast.LENGTH_SHORT).show();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                try {
                    player1.setDataSource(viewSource);
                    player1.setDisplay(videoHolder1);
                    player1.prepare();
                    player1.setAudioStreamType(AudioManager.STREAM_MUSIC);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player1.start();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                player1.pause();
            }
        });

        btnRewind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (player1 == null) {
                    return;
                }
                int pos = player1.getCurrentPosition();
                pos -= 5000;
                player1.seekTo(pos);

            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (player1 == null) {
                    return;
                }
                int pos = player1.getCurrentPosition();
                pos += 15000;
                player1.seekTo(pos);
            }
        });

    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }


}