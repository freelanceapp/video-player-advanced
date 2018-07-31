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

    ArrayList<Favrt> favrtArrayList = new ArrayList<>();
//    ArrayList<ShowVideo> filteredListttt = new ArrayList<>();
    Activity activity;
    Context context;
    int flag = 0;

    public static ArrayList<ShowVideo> listWithoutDuplicates;

    public FavrtAdapter(ArrayList<Favrt> favrtArrayList, Activity activity) {
        this.favrtArrayList = favrtArrayList;
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

        final Favrt favrt = favrtArrayList.get(i);
        showVideoHolder.txt_title.setText(favrt.getName());
        showVideoHolder.txt_duration.setText(favrt.getTime());
        showVideoHolder.txt_date.setText(favrt.getDate());
        Glide.with(activity).load("file://" + favrt.getThumb())
                .into(showVideoHolder.img_thumb);


        showVideoHolder.img_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = favrt.getFolder();
                String ss = favrt.getName();
                Intent intent = new Intent(activity, PlayerActivity.class);
                intent.putExtra("prerna", s);
                intent.putExtra("prernaa", ss);
                activity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (favrtArrayList == null)
            return 0;
        return favrtArrayList.size();
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

        ImageView img_thumb, img_favrt;
        TextView txt_title, txt_duration, txt_date;

        public ShowVideoHolder(@NonNull View itemView) {
            super(itemView);

            img_thumb = itemView.findViewById(R.id.img_thumbfavrt);
            txt_title = itemView.findViewById(R.id.txt_titlefavrt);
            txt_duration = itemView.findViewById(R.id.txt_durationfavrt);
            txt_date = itemView.findViewById(R.id.txt_datefavrt);

        }
    }
}
