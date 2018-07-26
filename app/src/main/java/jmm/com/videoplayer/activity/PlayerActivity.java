package jmm.com.videoplayer.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import jmm.com.videoplayer.R;

public class PlayerActivity extends AppCompatActivity {

    ImageView img_play;
    SeekBar seekbar;
    int flag = 0;
    Handler seekHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        img_play = findViewById(R.id.img_play);
        seekbar = findViewById(R.id.seekbar);
        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    img_play.setImageResource(R.drawable.pause);
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seekbar.setProgress(50);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flag = 1;

                } else {
                    img_play.setImageResource(R.drawable.play);
                    flag = 0;
                }
            }
        });


    }




}