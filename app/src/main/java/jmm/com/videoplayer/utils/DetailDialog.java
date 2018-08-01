package jmm.com.videoplayer.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import jmm.com.videoplayer.R;

public class DetailDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button btn_dialog_ok;
    public TextView txt_dialog_folder, txt_dialog_size, txt_dialog_resolution, txt_dialog_location, txt_dialog_date, txt_dialog_duration;
    public  String folder, size, resolution, location, date, duration;

    public DetailDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    public DetailDialog(Activity a, String folder, String size, String resolution, String location, String date, String duration) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.folder = folder;
        this.size = size;
        this.resolution = resolution;
        this.location = location;
        this.date = date;
        this.duration = duration;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_detail);
        btn_dialog_ok = findViewById(R.id.btn_dialog_ok);
        txt_dialog_folder = findViewById(R.id.txt_dialog_folder);
        txt_dialog_size = findViewById(R.id.txt_dialog_size);
        txt_dialog_resolution = findViewById(R.id.txt_dialog_resolution);
        txt_dialog_location = findViewById(R.id.txt_dialog_location);
        txt_dialog_date = findViewById(R.id.txt_dialog_date);
        txt_dialog_duration = findViewById(R.id.txt_dialog_duration);


        txt_dialog_folder.setText(folder);
        txt_dialog_size.setText(size + " MB");
        txt_dialog_resolution.setText(resolution);
        txt_dialog_location.setText(location);
        txt_dialog_date.setText(date);
        txt_dialog_duration.setText(duration);
        btn_dialog_ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_ok:
                this.dismiss();
                break;

            default:
                break;
        }
        dismiss();
    }
}