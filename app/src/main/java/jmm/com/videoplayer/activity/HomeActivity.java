package jmm.com.videoplayer.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.adapter.ShowVideoAdapter;
import jmm.com.videoplayer.model.ShowVideo;
import jmm.com.videoplayer.utils.AlertDialogHelper;
import jmm.com.videoplayer.utils.CustomeSpinner;
import jmm.com.videoplayer.utils.Helper;
import jmm.com.videoplayer.utils.RecyclerItemClickListener;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlertDialogHelper.AlertDialogListener {
    private static final int REQUEST_PERMISSIONS = 100;
    List<String> videoFolderNamearray = new ArrayList<>();
    ArrayList<String> videoPatharray = new ArrayList<>();
    ArrayList<ShowVideo> arrayList = new ArrayList<>();
    RecyclerView rv_showvideo, rv_showfavrt;
    ShowVideoAdapter showVideoAdapter;
    Spinner navigationSpinner;
    String url;
    String foldername;
    String thumb;
    String duration;
    String date;
    String name;
    String resolution;
    String size;
    Toolbar toolbar;
    String a;
    List<String> listWithoutDuplicates;
    ProgressDialog progressDialog;
    LinearLayout linearLayout;
    SearchView searchView;
    Button show;
    String MBKB;
    ArrayList<ShowVideo> multiselect_list = new ArrayList<>();
    Menu context_menu;
    AppBarLayout appbar;
    AlertDialogHelper alertDialogHelper;
    boolean isMultiSelect = false;
    ActionMode mActionMode;
    TabWidget tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        appbar = findViewById(R.id.appbar);
        tabs = findViewById(android.R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rv_showvideo = findViewById(R.id.rv_showvideo);
        rv_showvideo.setLayoutManager(new LinearLayoutManager(this));
        showVideoAdapter = new ShowVideoAdapter(arrayList, multiselect_list, this);
        rv_showvideo.setAdapter(showVideoAdapter);

        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Wait...");

        alertDialogHelper = new AlertDialogHelper(this);

        TabHost host = findViewById(R.id.tabHost);
        host.setup();
        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Device");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Device");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("favourite");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Favourite");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Cloud");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Cloud");
        host.addTab(spec);


        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            getfolders();
        }
        navigationSpinner = new Spinner(getSupportActionBar().getThemedContext());
        if (listWithoutDuplicates.isEmpty()) {

            Toast.makeText(this, "No Video Available", Toast.LENGTH_SHORT).show();
        } else {
            navigationSpinner.setAdapter(new CustomeSpinner(this, R.layout.custom_spinner, listWithoutDuplicates));
            toolbar.addView(navigationSpinner, 0);

        }

        navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
//                a = String.valueOf(navigationSpinner.getSelectedItemPosition());


                a = navigationSpinner.getSelectedItem().toString();

                if (a.equals("All")) {
                    arrayList.clear();
                    getfolders();
                } else {
                    progressDialog.show();
                    getVideoCatWise(a);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        show = findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getfavrt();
            }
        });


        rv_showvideo.addOnItemTouchListener(new RecyclerItemClickListener(this, rv_showvideo, new RecyclerItemClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               /* String s = arrayList.get(position).getFolder();
                Log.e("prerna", s);

                Toast.makeText(HomeActivity.this, s, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, PlayerActivity.class);
                intent.putExtra("prerna", s);
                startActivity(intent);*/
                if (isMultiSelect)
                    multi_select(position);

                else {

                    // openDocument(ApkList.get(position).getFilePath());
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (!isMultiSelect) {
                    isMultiSelect = true;

                    if (mActionMode == null) {
//                        mActionMode = startActionMode(mActionModeCallback);
                        mActionMode = startSupportActionMode(mActionModeCallback);

                    }
                }

                multi_select(position);
            }
        }));


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.home, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                showVideoAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                showVideoAdapter.getFilter().filter(query);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so lon,g
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Toast.makeText(HomeActivity.this, "home", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(HomeActivity.this, ShareActivity.class));

        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getfolders() {

        String[] projection = {MediaStore.Video.Media.SIZE, MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Media.DURATION, MediaStore.Video.Thumbnails.DATA, MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        try {
            cursor.moveToLast();

            do {
                videoFolderNamearray.add("All");
                videoFolderNamearray.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))));
                videoPatharray.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA))));

                HashSet<String> listToSet = new HashSet<String>(videoFolderNamearray);
                listWithoutDuplicates = new ArrayList<>(listToSet);
                Collections.sort(listWithoutDuplicates);


                foldername = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                thumb = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED));
                resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
                size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                long fileSizeInBytes = Long.parseLong(size);
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;

                if (fileSizeInMB == 0) {
                    MBKB = String.valueOf(fileSizeInKB);

                } else {
                    MBKB = String.valueOf(fileSizeInMB);
                }

                String da = Helper.LongToDate(date);
                String tt = Helper.Time(duration);
                Log.e("prerna", String.valueOf(fileSizeInMB));

                ShowVideo showVideo = new ShowVideo();
                showVideo.setThumb(thumb);
                showVideo.setDate(da);
                showVideo.setTime(tt);
                showVideo.setFolder(url);
                showVideo.setName(name);


                arrayList.add(new ShowVideo(thumb, resolution, "1", tt, da, url, name, MBKB));
            } while (cursor.moveToPrevious());

            cursor.close();
            showVideoAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
//        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
    }

    public void getVideoCatWise(String s) {
        arrayList.clear();
        String[] projection = {MediaStore.Video.Media.DURATION, MediaStore.Video.Thumbnails.DATA, MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        try {
            cursor.moveToLast();

            do {
                foldername = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));

                if (foldername.contains(s)) {
                    url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    thumb = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED));

                    String da = Helper.LongToDate(date);
                    String tt = Helper.Time(duration);

                    Log.e("aaaaaa", thumb);
                    arrayList.add(new ShowVideo(thumb, da, "1", tt, da, "5454", name, s));

                }

            } while (cursor.moveToPrevious());
            progressDialog.dismiss();

            cursor.close();

            showVideoAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getfavrt() {

        ArrayList<ShowVideo> sssss = new ArrayList<>();
        sssss = ShowVideoAdapter.listWithoutDuplicates;

        SharedPreferences prefs = getSharedPreferences("favrt", MODE_PRIVATE);
        String ssss = prefs.getString("name", null);
        Log.i("restoredText", sssss + "");
        rv_showfavrt = findViewById(R.id.rv_showfavrt);
        rv_showfavrt.setLayoutManager(new LinearLayoutManager(this));
        showVideoAdapter = new ShowVideoAdapter(sssss, multiselect_list, this);
        rv_showfavrt.setAdapter(showVideoAdapter);

    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(arrayList.get(position)))
                multiselect_list.remove(arrayList.get(position));
            else
                multiselect_list.add(arrayList.get(position));

            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
            } else {
                mActionMode.setTitle("");
            }

            refreshAdapter();

        }
    }

    public void refreshAdapter() {
        showVideoAdapter.selected_ApkList = multiselect_list;
        showVideoAdapter.showVideoArrayList = arrayList;
        showVideoAdapter.notifyDataSetChanged();
    }

    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("", "Delete Video", "DELETE", "CANCEL", 1, false);

//                    alertDialogHelper.showAlertDialog("","Delete Video","DELETE","CANCEL",1,false);
                    return true;
                case R.id.action_select:
                    selectAll();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<ShowVideo>();
            refreshAdapter();
        }
    };

    private void selectAll() {
        if (mActionMode != null) {
            multiselect_list.clear();

            for (int i = 0; i < arrayList.size(); i++) {
                if (!multiselect_list.contains(multiselect_list.contains(arrayList.get(i)))) {
                    multiselect_list.add(arrayList.get(i));
                }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    @Override
    public void onPositiveClick(int from) {
        if (from == 1) {
            if (multiselect_list.size() > 0) {
                new DeleteFileTask(multiselect_list).execute();
                for (int i = 0; i < multiselect_list.size(); i++)
                    arrayList.remove(multiselect_list.get(i));

                showVideoAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }

            }
        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }

            //this yet to be implemented
//            Grid_Model mImg = new Grid_Model();
//            mImg.setImgPath("");
//            ApkList.add(mImg);
            showVideoAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    private class DeleteFileTask extends AsyncTask<Void, Void, Integer> {
        ArrayList<ShowVideo> multiselect_list;

        DeleteFileTask(ArrayList<ShowVideo> multiselect_list) {
            this.multiselect_list = multiselect_list;
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            return deleteFile(multiselect_list);
        }

        @Override
        protected void onPostExecute(Integer FileCount) {
            super.onPostExecute(FileCount);

            Toast.makeText(HomeActivity.this, FileCount + " file deleted", Toast.LENGTH_SHORT).show();


        }
    }

    private int deleteFile(ArrayList<ShowVideo> delete_list) {
        int count = 0;

        for (int i = 0; i < delete_list.size(); i++) {
            File f = new File(String.valueOf(delete_list.get(i).getFolder()));
            if (f.exists())
                if (f.delete()) {
                    count++;
                    sendBroadcast(f);
                }

        }


        // clear  existing cache of glide library
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Glide.get(mcontext).clearDiskCache();
                // Glide.getPhotoCacheDir(mcontext).delete();
            }
        }).start();

        return count;
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
            sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
        }


    }
}