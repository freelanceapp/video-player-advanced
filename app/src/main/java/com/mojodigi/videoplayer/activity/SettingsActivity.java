package com.mojodigi.videoplayer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.videoplayer.R;
import com.mojodigi.videoplayer.utils.Helper;
import com.mojodigi.videoplayer.utils.MyPreference;

import javax.sql.StatementEvent;

public class SettingsActivity extends AppCompatActivity {

    TextView txt_playall, txt_random, txt_loop, txt_settinghead;
    Switch switch_playall, switch_random, switch_loop;
    ImageView img_back_settings;
    RadioButton radio_playall, radio_random, radio_loop;
    RadioGroup radioGroup;
    Button btn_settingsave;
    MyPreference myPreference=new MyPreference(this);
    String type1;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        txt_settinghead = findViewById(R.id.txt_settinghead);
        img_back_settings = findViewById(R.id.img_back_settings);
        radio_playall = findViewById(R.id.radio_playall);
        radio_random = findViewById(R.id.radio_random);
        radio_loop = findViewById(R.id.radio_loop);
        radioGroup = findViewById(R.id.radioGroup);
        btn_settingsave = findViewById(R.id.btn_settingsave);

        txt_settinghead.setTypeface(Helper.typeFace_adobe_caslonpro_Regular(this));
        radio_playall.setTypeface(Helper.typeFace_adobe_caslonpro_Regular(this));
        radio_random.setTypeface(Helper.typeFace_adobe_caslonpro_Regular(this));
        radio_loop.setTypeface(Helper.typeFace_adobe_caslonpro_Regular(this));
/*

        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor = preferences.edit();
        editor.remove("type");
        editor.commit();
*/

        final String type = myPreference.getPlaytype(MyPreference.PREFS_NAME);
        if (type.equalsIgnoreCase("Serial")) {
            radio_playall.setChecked(true);
            type1=type;

        } else if (type.equalsIgnoreCase("Random")) {
            radio_random.setChecked(true);
            type1=type;

        } else if (type.equalsIgnoreCase("Loop")) {
            radio_loop.setChecked(true);
            type1=type;

        }else if (type.equalsIgnoreCase("")){
            radio_playall.setChecked(true);
            type1=type;

        }

        img_back_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_playall:
                        type1= "Serial";
                        break;
                    case R.id.radio_random:
                        type1= "Random";
                        break;
                    case R.id.radio_loop:
                        type1= "Loop";
                        break;
                }
            }
        });

        btn_settingsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPreference.setplaytype(MyPreference.PREFS_NAME, type1);
                finish();
            }
        });

    }
}
