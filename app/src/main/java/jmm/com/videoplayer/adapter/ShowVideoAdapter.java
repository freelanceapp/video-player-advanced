package jmm.com.videoplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jmm.com.videoplayer.R;
import jmm.com.videoplayer.model.ShowVideo;

public class ShowVideoAdapter extends RecyclerView.Adapter<ShowVideoAdapter.ShowVideoHolder>  {

    ArrayList<ShowVideo> showVideoArrayList = new ArrayList<>();
    ArrayList<ShowVideo> filteredList = new ArrayList<>();
    Activity activity;
    Context context;
    int flag=0;

    public ShowVideoAdapter(ArrayList<ShowVideo> showVideoArrayList, Activity activity) {
        this.showVideoArrayList = showVideoArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ShowVideoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_showvideo, viewGroup, false);
        return new ShowVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowVideoHolder showVideoHolder, int i) {

        final ShowVideo showVideo = showVideoArrayList.get(i);
        showVideoHolder.txt_title.setText(showVideo.getName());
        showVideoHolder.txt_duration.setText(showVideo.getTime());
        showVideoHolder.txt_date.setText(showVideo.getDate());
        Glide.with(activity).load("file://" + showVideo.getThumb())
                .into(showVideoHolder.img_thumb);

        showVideoHolder.img_favrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag==0){
                    showVideoHolder.img_favrt.setImageResource(R.drawable.starefull);
                    flag=1;
                }else {
                    showVideoHolder.img_favrt.setImageResource(R.drawable.starempty);
                    flag=0;

                }

            }
        });


    }

    @Override
    public int getItemCount() {
        if (showVideoArrayList == null)
            return 0;
        return showVideoArrayList.size();
    }



    public class ShowVideoHolder extends RecyclerView.ViewHolder {

        ImageView img_thumb,img_favrt;
        TextView txt_title, txt_duration, txt_date;

        public ShowVideoHolder(@NonNull View itemView) {
            super(itemView);

            img_thumb = itemView.findViewById(R.id.img_thumb);
            img_favrt = itemView.findViewById(R.id.img_favrt);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_duration = itemView.findViewById(R.id.txt_duration);
            txt_date = itemView.findViewById(R.id.txt_date);

        }
    }


}
