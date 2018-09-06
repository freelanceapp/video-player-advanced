package jmm.com.videoplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.viethoa.RecyclerViewFastScroller;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.activity.HomeActivity;
import jmm.com.videoplayer.activity.PlayerActivity;
import jmm.com.videoplayer.model.Favrt;
import jmm.com.videoplayer.model.ShowVideo;
import jmm.com.videoplayer.utils.DatabaseHelper;
import jmm.com.videoplayer.utils.DetailDialog;
import jmm.com.videoplayer.utils.Helper;

public class ShowVideoAdapter extends RecyclerView.Adapter<ShowVideoAdapter.ShowVideoHolder> implements Filterable,  RecyclerViewFastScroller.BubbleTextGetter {

    public ArrayList<ShowVideo> showVideoArrayList = new ArrayList<>();
    public ArrayList<ShowVideo> filteredListttt = new ArrayList<>();
    public static ArrayList<Favrt> favrtArraylist = new ArrayList<>();
    public ArrayList<?> selected_ApkList = new ArrayList<>();
    Activity activity;
    DatabaseHelper databaseHelper;
    public static List<String> nameList = new ArrayList<>();


    public ShowVideoAdapter(ArrayList<ShowVideo> showVideoArrayList, ArrayList<?> selectedApkList, Activity activity) {
        this.showVideoArrayList = showVideoArrayList;
        this.filteredListttt = showVideoArrayList;
        this.selected_ApkList = selectedApkList;
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);

        //refresh data on start
        showdata();
        for (int i = 0; i < favrtArraylist.size(); i++) {
            nameList.add(favrtArraylist.get(i).getName());
        }


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
//        Glide.with(activity).load(Uri.fromFile(new File(showVideo.getThumb()))).into(showVideoHolder.img_thumb);

        showVideo.setId(String.valueOf(i));

        //set tag
        showVideoHolder.img_favrt.setTag(i);

        Integer pos = (Integer) showVideoHolder.img_favrt.getTag();

        if (showVideoArrayList.get(pos).isFavrt()) {
            //showVideoArrayList.get(pos).setFavrt(false);
            showVideoHolder.img_favrt.setImageResource(R.drawable.fill_m);
        } else {
            //showVideoArrayList.get(pos).setFavrt(true);
            showVideoHolder.img_favrt.setImageResource(R.drawable.empty_m);
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("size", "" + showVideoArrayList.size());
        editor.apply();

        //setting favourite
        if (nameList.contains(showVideo.getName())) {
            showVideoHolder.img_favrt.setImageResource(R.drawable.fill_m);
//            showVideoHolder.img_favrt.setTag(activity);
        }

        //check the database
        if (databaseHelper.getalldata().getCount() == 0) {
            showVideoHolder.img_favrt.setImageResource(R.drawable.empty_m);

        }

        showVideoHolder.img_favrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer position = (Integer) showVideoHolder.img_favrt.getTag();

                if (showVideoArrayList.get(position).isFavrt()) {
                  //  showVideoArrayList.get(pos).setFavrt(false);

                    showVideoHolder.img_favrt.setImageResource(R.drawable.fill_m);

                } else {
                  //  showVideoArrayList.get(pos).setFavrt(true);
                    showVideoHolder.img_favrt.setImageResource(R.drawable.empty_m);


                }


                if (nameList.contains(showVideo.getName())) {
                    databaseHelper.deletedata(showVideo.getName());
                } else {

                }
                if (showVideo.isFavrt()) {
                    showVideoHolder.img_favrt.setImageResource(R.drawable.empty_m);
                    showVideo.setFavrt(false);

                    Integer deletedRows = databaseHelper.deletedata(showVideo.getName());
                    if (deletedRows > 0) {
                        Toast.makeText(activity, "Removed from Favourites", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(activity, "Removed from Favourites", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showVideoHolder.img_favrt.setImageResource(R.drawable.fill_m);
                    showVideo.setFavrt(true);
                    boolean insert = databaseHelper.insertdata(showVideo.getName(), showVideo.getThumb(), showVideo.getFolder(), showVideo.getTime(), showVideo.getResolution(), showVideo.getData(), showVideo.getSize());


                    if (insert == true) {
                        Toast.makeText(activity, "Marked as Favourite", Toast.LENGTH_LONG).show();
                        favrtArraylist.clear();
                        showdata();

                    } else
                        Toast.makeText(activity, "Try Again", Toast.LENGTH_LONG).show();
                }

            }
        });
        showVideoHolder.ll_favrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer position = (Integer) showVideoHolder.img_favrt.getTag();

                if (showVideoArrayList.get(position).isFavrt()) {
                    //  showVideoArrayList.get(pos).setFavrt(false);

                    showVideoHolder.img_favrt.setImageResource(R.drawable.fill_m);

                } else {
                    //  showVideoArrayList.get(pos).setFavrt(true);
                    showVideoHolder.img_favrt.setImageResource(R.drawable.empty_m);


                }

                if (showVideo.isFavrt()) {
                    showVideoHolder.img_favrt.setImageResource(R.drawable.empty_m);
                    showVideo.setFavrt(false);

                    Integer deletedRows = databaseHelper.deletedata(showVideo.getName());
                    if (deletedRows > 0) {
                        Toast.makeText(activity, "Removed from Favourites", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(activity, "Maarked as Favourite", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showVideoHolder.img_favrt.setImageResource(R.drawable.fill_m);
                    showVideo.setFavrt(true);

                    boolean insert = databaseHelper.insertdata(showVideo.getName(), showVideo.getThumb(), showVideo.getFolder(), showVideo.getTime(), showVideo.getResolution(), showVideo.getData(), showVideo.getSize());

                    if (insert == true) {
                        Toast.makeText(activity, "Marked as Favourite", Toast.LENGTH_LONG).show();
                        favrtArraylist.clear();
                        showdata();

                    } else
                        Toast.makeText(activity, "Try Again", Toast.LENGTH_LONG).show();
                }


            }
        });
        showVideoHolder.img_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = showVideo.getFolder();
                String ss = showVideo.getName();
                Intent intent = new Intent(activity, PlayerActivity.class);
                intent.putExtra("source", s);
                intent.putExtra("name", ss);
                intent.putExtra("current", "" + showVideo.getId());
                intent.putExtra("type", "all");
                activity.startActivity(intent);
            }
        });
        showVideoHolder.ll_playvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = showVideo.getFolder();
                String ss = showVideo.getName();
                Intent intent = new Intent(activity, PlayerActivity.class);
                intent.putExtra("source", s);
                intent.putExtra("name", ss);
                intent.putExtra("current", "" + showVideo.getId());
                intent.putExtra("type", "all" );

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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                                builder1.setMessage("Are you sure delete this video ?");
                                builder1.setCancelable(false);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();

                                                File file = new File(showVideo.getFolder());
                                                if (file.exists()) {
                                                    boolean deleted = file.delete();
                                                    if (deleted) {
                                                        sendBroadcast(file);
                                                        databaseHelper.deletedata(showVideo.getName());
                                                    }
                                                }

                                                removeItem(Integer.valueOf(showVideo.getId()));
                                                dialog.cancel();
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(activity, "No", Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                                return true;
                            case R.id.nav_dot_share:
                                Helper.ShareSingleFile(showVideo.getFolder(), activity, activity.getResources().getString(R.string.file_provider_authority));
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


        //multiple select checkbox visibililty
        if (selected_ApkList.contains(showVideoArrayList.get(i))) {
            showVideoHolder.chbx.setVisibility(View.VISIBLE);  // for time being checkbox not shown   layout backgroud being changed
//            showVideoHolder.ll_select.setBackgroundColor(activity.getResources().getColor(R.color.orange));
        } else {
            showVideoHolder.chbx.setVisibility(View.INVISIBLE); // for time being checkbox not shown   layout backgroud being changed
//            showVideoHolder.ll_select.setBackgroundColor(activity.getResources().getColor(R.color.grey));
        }


    }

    @Override
    public int getItemCount() {
        if (filteredListttt == null)
            return 0;
        return filteredListttt.size();
    }

    //search
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


    //connect recyclerview with scroll view data
    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= showVideoArrayList.size())
            return null;

        String name = showVideoArrayList.get(pos).getName();

        if (name == null || name.length() < 1)
            return null;

        return showVideoArrayList.get(pos).getName().substring(0, 1);
    }


    public class ShowVideoHolder extends RecyclerView.ViewHolder {

        ImageView img_thumb, img_favrt, img_options;
        TextView txt_title, txt_duration, txt_resolution;
        CheckBox chbx;
        LinearLayout ll_select, ll_playvideo, ll_favrt;

        public ShowVideoHolder(@NonNull View itemView) {
            super(itemView);

            img_thumb = itemView.findViewById(R.id.img_thumb);
            img_options = itemView.findViewById(R.id.img_options);
            img_favrt = itemView.findViewById(R.id.img_favrt);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_duration = itemView.findViewById(R.id.txt_duration);
            txt_resolution = itemView.findViewById(R.id.txt_resolution);
            chbx = itemView.findViewById(R.id.chbx);
            ll_select = itemView.findViewById(R.id.ll_select);
            ll_playvideo = itemView.findViewById(R.id.ll_playvideo);
            ll_favrt = itemView.findViewById(R.id.ll_favrt);
            //font style
            Typeface font = Typeface.createFromAsset(activity.getAssets(), "PoetsenOne-Regular.ttf");
            txt_title.setTypeface(font);
            txt_duration.setTypeface(font);
            txt_resolution.setTypeface(font);

        }
    }


    //item remove from recyclerview
    private void removeItem(int position) {
        showVideoArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, showVideoArrayList.size());
    }


    //delete data from device
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


    //get data from database
    public void showdata() {
        Cursor res = databaseHelper.getalldata();
        if (res.getCount() == 0) {
            // show message
//            Toast.makeText(activity, "No FAV", Toast.LENGTH_LONG).show();
            return;
        }

        Favrt favrt = new Favrt();

        nameList.clear();
        favrtArraylist.clear();

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :" + res.getString(0) + "\n");
            buffer.append("Name :" + res.getString(1) + "\n");
            buffer.append("Thumb :" + res.getString(2) + "\n");
            buffer.append("Folder :" + res.getString(3) + "\n");
            buffer.append("Time :" + res.getString(4) + "\n");
            buffer.append("Resolution :" + res.getString(5) + "\n");
            buffer.append("Date :" + res.getString(6) + "\n");
            buffer.append("Size :" + res.getString(7) + "\n");
            nameList.add(res.getString(1));

            favrt.setId(res.getString(0));
            favrt.setName(res.getString(1));
            favrt.setThumb(res.getString(2));
            favrt.setFolder(res.getString(3));
            favrt.setTime(res.getString(4));
            favrt.setResolution(res.getString(5));
            favrt.setDate(res.getString(6));
            favrt.setSize(res.getString(7));
            favrtArraylist.add(favrt);


        }
        // Show all data
//        Toast.makeText(activity, buffer.toString(), Toast.LENGTH_LONG).show();

    }

}
