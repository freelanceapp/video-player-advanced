package jmm.com.videoplayer.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.activity.PlayerActivity;
import jmm.com.videoplayer.model.ShowVideo;
import jmm.com.videoplayer.utils.DetailDialog;
import jmm.com.videoplayer.utils.Helper;

import static android.content.Context.MODE_PRIVATE;

public class ShowVideoAdapter extends RecyclerView.Adapter<ShowVideoAdapter.ShowVideoHolder> implements Filterable {

    ArrayList<ShowVideo> showVideoArrayList = new ArrayList<>();
    ArrayList<ShowVideo> filteredListttt = new ArrayList<>();
    ArrayList<ShowVideo> favrtArraylist = new ArrayList<>();
    Activity activity;
    Context context;
    int flag = 0;
    int postion;

    public static ArrayList<ShowVideo> listWithoutDuplicates;

    public ShowVideoAdapter(ArrayList<ShowVideo> showVideoArrayList, Activity activity) {
        this.showVideoArrayList = showVideoArrayList;
        this.filteredListttt = showVideoArrayList;
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


        final ShowVideo showVideo = filteredListttt.get(i);
        showVideoHolder.txt_title.setText(showVideo.getName());
        showVideoHolder.txt_duration.setText(showVideo.getTime());
        showVideoHolder.txt_resolution.setText(showVideo.getResolution());
        Glide.with(activity).load("file://" + showVideo.getThumb())
                .into(showVideoHolder.img_thumb);
        showVideo.setId(String.valueOf(i));

        showVideoHolder.img_favrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    showVideoHolder.img_favrt.setImageResource(R.drawable.fill_m);
                    favrtArraylist.add(new ShowVideo(showVideo.getThumb(), showVideo.getDate(), "1", showVideo.getTime(), showVideo.getDate(), showVideo.getFolder(), showVideo.getName(), showVideo.getSize()));
                    flag = 1;

                } else {
                    showVideoHolder.img_favrt.setImageResource(R.drawable.empty_m);
                    favrtArraylist.remove(new ShowVideo(showVideo.getThumb(), showVideo.getDate(), "1", showVideo.getTime(), showVideo.getDate(), showVideo.getFolder(), showVideo.getName(), showVideo.getSize()));
                    flag = 0;

                }
                // favrt video
                HashSet<ShowVideo> listToSet = new HashSet<>(favrtArraylist);
                listWithoutDuplicates = new ArrayList<>(listToSet);
//                Collections.sort(listWithoutDuplicates);
                Log.i("favrtshared", listWithoutDuplicates + "");
                SharedPreferences.Editor editor = activity.getSharedPreferences("favrt", MODE_PRIVATE).edit();
                editor.putString("name", "" + listWithoutDuplicates);
                editor.apply();

            }
        });
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

        showVideoHolder.img_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(activity, showVideoHolder.img_options);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_dot_detail:
                                DetailDialog cdd = new DetailDialog(activity, showVideo.getName(), showVideo.getSize(), showVideo.getResolution(), showVideo.getFolder(), showVideo.getData(), showVideo.getTime());
                                cdd.show();
                                return true;
                            case R.id.nav_dot_delete:


                                File dir = activity.getFilesDir();
                                File file = new File(showVideo.getFolder());
                                if (file.exists()) {
                                    boolean deleted = file.delete();
                                    if (deleted) {
                                        sendBroadcast(file);
                                    }
                                    Log.i("asfd", "" + deleted);

                                }


                                Log.i("asfd", "" + file);

                                removeItem(Integer.valueOf(showVideo.getId()));

                                return true;
                            case R.id.nav_dot_share:
                                Helper.ShareSingleFile(showVideo.getFolder(), activity, activity.getResources().getString(R.string.file_provider_authority));
                                //handle menu3 click
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (filteredListttt == null)
            return 0;
        return filteredListttt.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredListttt = showVideoArrayList;
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
    }


    public class ShowVideoHolder extends RecyclerView.ViewHolder {

        ImageView img_thumb, img_favrt, img_options;
        TextView txt_title, txt_duration, txt_resolution;

        public ShowVideoHolder(@NonNull View itemView) {
            super(itemView);

            img_thumb = itemView.findViewById(R.id.img_thumb);
            img_options = itemView.findViewById(R.id.img_options);
            img_favrt = itemView.findViewById(R.id.img_favrt);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_duration = itemView.findViewById(R.id.txt_duration);
            txt_resolution = itemView.findViewById(R.id.txt_resolution);

            Typeface font = Typeface.createFromAsset(activity.getAssets(), "PoetsenOne-Regular.ttf");
            txt_title.setTypeface(font);
            txt_duration.setTypeface(font);
            txt_resolution.setTypeface(font);

        }
    }

    private void removeItem(int position) {
        showVideoArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, showVideoArrayList.size());
    }

    private void sendBroadcast(File outputFile) {
        //  https://stackoverflow.com/questions/4430888/android-file-delete-leaves-empty-placeholder-in-gallery
        //this broadcast clear the deleted images from  android file system
        //it makes the MediaScanner service run again that keep  track of files in android
        // to  run it a permission  in manifest file has been given
        // <protected-broadcast android:name="android.intent.action.MEDIA_MOUNTED" />


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(outputFile);
            scanIntent.setData(contentUri);
            activity.sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            activity.sendBroadcast(intent);
        }


    }

}
