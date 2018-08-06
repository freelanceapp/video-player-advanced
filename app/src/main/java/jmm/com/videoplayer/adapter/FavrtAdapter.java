package jmm.com.videoplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.activity.PlayerActivity;
import jmm.com.videoplayer.model.Favrt;
import jmm.com.videoplayer.model.ShowVideo;

import static android.content.Context.MODE_PRIVATE;

public class FavrtAdapter extends RecyclerView.Adapter<FavrtAdapter.ShowVideoHolder> /*implements Filterable*/ {

    ArrayList<ShowVideo> showVideoArrayList = new ArrayList<>();
//    ArrayList<ShowVideo> filteredListttt = new ArrayList<>();
    Activity activity;
    Context context;
    int flag = 0;

    public static ArrayList<ShowVideo> listWithoutDuplicates;

    public FavrtAdapter(ArrayList<ShowVideo> showVideoArrayList, Activity activity) {
        this.showVideoArrayList = showVideoArrayList;
//        this.filteredListttt = showVideoArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ShowVideoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_favrt, viewGroup, false);
        return new ShowVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowVideoHolder showVideoHolder, int i) {

        final ShowVideo showVideo = showVideoArrayList.get(i);
        showVideoHolder.txt_title.setText(showVideo.getName());
        showVideoHolder.txt_duration.setText(showVideo.getTime());
        showVideoHolder.txt_resolutionfavrt.setText(showVideo.getDate());
        Glide.with(activity).load("file://" + showVideo.getThumb())
                .into(showVideoHolder.img_thumb);


        showVideoHolder.img_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = showVideo.getFolder();
                String ss = showVideo.getName();
                Intent intent = new Intent(activity, PlayerActivity.class);
                intent.putExtra("prerna", s);
                intent.putExtra("prernaa", ss);
                activity.startActivity(intent);
            }
        });

        showVideoHolder.img_favrtfavrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showVideo.setFavrt(true);



            }
        });


    }

    @Override
    public int getItemCount() {
        if (showVideoArrayList == null)
            return 0;
        return showVideoArrayList.size();
    }

  /*  @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    favrtArrayList = favrtArrayList;
                } else {
                    ArrayList<ShowVideo> filteredList = new ArrayList<>();
                    for (ShowVideo row : showVideoArrayList) {

                        //condition to search for
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filteredListttt = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredListttt;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredListttt = (ArrayList<ShowVideo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }*/


    public class ShowVideoHolder extends RecyclerView.ViewHolder {

        ImageView img_thumb, img_favrtfavrt;
        TextView txt_title, txt_duration, txt_resolutionfavrt;

        public ShowVideoHolder(@NonNull View itemView) {
            super(itemView);

            img_thumb = itemView.findViewById(R.id.img_thumbfavrt);
            txt_title = itemView.findViewById(R.id.txt_titlefavrt);
            txt_duration = itemView.findViewById(R.id.txt_durationfavrt);
            txt_resolutionfavrt = itemView.findViewById(R.id.txt_resolutionfavrt);
            img_favrtfavrt = itemView.findViewById(R.id.img_favrtfavrt);

        }
    }
}
