package jmm.com.videoplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.activity.HomeActivity;
import jmm.com.videoplayer.activity.PlayerActivity;
import jmm.com.videoplayer.model.Favrt;
import jmm.com.videoplayer.model.ShowVideo;
import jmm.com.videoplayer.utils.DatabaseHelper;

import static android.content.Context.MODE_PRIVATE;

public class FavrtAdapter extends RecyclerView.Adapter<FavrtAdapter.FavrtHolder> implements Filterable {

    ArrayList<Favrt> favrtArrayList = new ArrayList<>();
    public ArrayList<Favrt> filteredListtttf = new ArrayList<>();
    Activity activity;
    DatabaseHelper databaseHelper;

    public FavrtAdapter(ArrayList<Favrt> favrtArrayList, Activity activity) {
        this.favrtArrayList = favrtArrayList;
        this.filteredListtttf = favrtArrayList;
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
    }

    @NonNull
    @Override
    public FavrtHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_favrt, viewGroup, false);
        return new FavrtHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavrtHolder favrtHolder, final int i) {

        final Favrt favrt = filteredListtttf.get(i);
        favrtHolder.txt_title.setText(favrt.getName());
        favrtHolder.txt_duration.setText(favrt.getTime());
        favrtHolder.txt_resolutionfavrt.setText(favrt.getResolution());
        Glide.with(activity).load("file://" + favrt.getThumb())
                .into(favrtHolder.img_thumb);




        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("size", "" + favrtArrayList.size());
        editor.apply();


        favrtHolder.img_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = favrt.getFolder();
                String ss = favrt.getName();
                Intent intent = new Intent(activity, PlayerActivity.class);
                intent.putExtra("source", s);
                intent.putExtra("name", ss);
                intent.putExtra("current", "" +i);
                intent.putExtra("type", "favrt");
                activity.startActivity(intent);
            }
        });
        favrtHolder.ll_favrtplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = favrt.getFolder();
                String ss = favrt.getName();
                Intent intent = new Intent(activity, PlayerActivity.class);
                intent.putExtra("source", s);
                intent.putExtra("name", ss);
                intent.putExtra("current", "" + i);
                intent.putExtra("type", "favrt");
                activity.startActivity(intent);
            }
        });

        favrtHolder.img_favrtfavrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                favrt.setIsfavrt(true);

                if (favrt.isIsfavrt()) {
//                    favrtHolder.img_favrtfavrt.setImageResource(R.drawable.empty_m);
//                    favrt.setIsfavrt(false);
//
//                    Integer deletedRows = databaseHelper.deletedata(favrt.getName());
//                    if (deletedRows > 0) {


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                    builder1.setMessage("Are you sure to remove from favourites ?");
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(activity, "Favourite removed", Toast.LENGTH_SHORT).show();
                                    Integer deletedRows = databaseHelper.deletedata(favrt.getName());

                                    favrtArrayList.remove(i);
                                    notifyItemRemoved(i);
                                    notifyItemRangeChanged(i, favrtArrayList.size());

                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();


//                    } else {
//                        Toast.makeText(activity, "Data not Deleted", Toast.LENGTH_LONG).show();
//                    }
                } else {
                    /*favrtHolder.img_favrtfavrt.setImageResource(R.drawable.fill_m);
                    favrt.setIsfavrt(true);

                    boolean insert = databaseHelper.insertdata(showVideo.getName(), showVideo.getThumb(), showVideo.getFolder(), showVideo.getTime(), showVideo.getResolution());


                    if (insert == true) {
                        Toast.makeText(activity, "Data Inserted", Toast.LENGTH_LONG).show();

                        showdata();
                    } else
                        Toast.makeText(activity, "Data not Inserted", Toast.LENGTH_LONG).show();*/
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        if (filteredListtttf == null)
            return 0;
        return filteredListtttf.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredListtttf = favrtArrayList;
                } else {
                    ArrayList<Favrt> filteredList = new ArrayList<>();
                    for (Favrt row : favrtArrayList) {

                        //condition to search for
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            Toast toast = Toast.makeText(activity, "No Video Found", Toast.LENGTH_SHORT);
                            toast.cancel();

                            filteredList.add(row);
                        }
                    }
                    filteredListtttf = filteredList;
                }

                int size = filteredListtttf.size();
                if (size == 0) {
                    Toast toast = Toast.makeText(activity, "No Video Found", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredListtttf;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredListtttf = (ArrayList<Favrt>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FavrtHolder extends RecyclerView.ViewHolder {

        ImageView img_thumb, img_favrtfavrt;
        TextView txt_title, txt_duration, txt_resolutionfavrt;
        LinearLayout ll_favrtplay;

        public FavrtHolder(@NonNull View itemView) {
            super(itemView);

            img_thumb = itemView.findViewById(R.id.img_thumbfavrt);
            txt_title = itemView.findViewById(R.id.txt_titlefavrt);
            txt_duration = itemView.findViewById(R.id.txt_durationfavrt);
            txt_resolutionfavrt = itemView.findViewById(R.id.txt_resolutionfavrt);
            img_favrtfavrt = itemView.findViewById(R.id.img_favrtfavrt);
            ll_favrtplay = itemView.findViewById(R.id.ll_favrtplay);

            Typeface font = Typeface.createFromAsset(activity.getAssets(), "PoetsenOne-Regular.ttf");
            txt_title.setTypeface(font);
            txt_duration.setTypeface(font);
            txt_resolutionfavrt.setTypeface(font);


        }
    }


}
