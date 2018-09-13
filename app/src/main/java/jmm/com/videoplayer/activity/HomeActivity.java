package jmm.com.videoplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import jmm.com.videoplayer.R;
import jmm.com.videoplayer.adapter.FavrtAdapter;
import jmm.com.videoplayer.adapter.ShowVideoAdapter;
import jmm.com.videoplayer.model.Favrt;
import jmm.com.videoplayer.model.ShowVideo;
import jmm.com.videoplayer.utils.AlertDialogHelper;
import jmm.com.videoplayer.utils.CustomeSpinner;
import jmm.com.videoplayer.utils.DataHelper;
import jmm.com.videoplayer.utils.DatabaseHelper;
import jmm.com.videoplayer.utils.Helper;
import jmm.com.videoplayer.utils.RecyclerItemClickListener;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlertDialogHelper.AlertDialogListener, TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    private static final int REQUEST_PERMISSIONS = 100;
    List<String> videoFolderNamearray = new ArrayList<>();
    ArrayList<String> videoPatharray = new ArrayList<>();
    static ArrayList<ShowVideo> arrayList = new ArrayList<>();
    static ArrayList<Favrt> favrtArrayList = new ArrayList<>();
    ArrayList<Favrt> duplicatefavrtarray = new ArrayList<>();
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
    String selecteditem;
    List<String> listWithoutDuplicates = new ArrayList<>();
    ProgressDialog progressDialog;
    SearchView searchView;
    Button show;
    String MBKB;
    ArrayList<ShowVideo> multiselect_list = new ArrayList<>();
    Menu context_menu;
    AlertDialogHelper alertDialogHelper;
    boolean isMultiSelect = false;
    ActionMode mActionMode;
    TabWidget tabs;
    FavrtAdapter favrtAdapter;
    DatabaseHelper databaseHelper;
    ViewPager pager;
    TabHost host;
    boolean isUnseleAllEnabled = false;
    RecyclerViewFastScroller fastScroller;
    List<AlphabetItem> mAlphabetItems;
    List<String> mDataArray;
    int tabnumber;
    String type = "all";
    String checkscreen;
    SharedPreferences preferences;
    int lastFirstVisiblePosition;

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        databaseHelper = new DatabaseHelper(this);
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(android.R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rv_showvideo = findViewById(R.id.rv_showvideo);
        fastScroller = findViewById(R.id.fast_scroller);

        // set data on recyclerview
        rv_showvideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        showVideoAdapter = new ShowVideoAdapter(arrayList, multiselect_list, HomeActivity.this);
        rv_showvideo.setAdapter(showVideoAdapter);


        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Wait...");

        alertDialogHelper = new AlertDialogHelper(this);

        //tab host
        host = findViewById(R.id.tabHost);
        pager = findViewById(R.id.viewpager);

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


//        pager.setAdapter(new MyPagerAdapter(this));
        pager.setOnPageChangeListener(this);
        host.setOnTabChangedListener(this);


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

            /*
            Refrence for scrolling by alphabets with recyclerview
            https://github.com/viethoa/recyclerview-alphabet-fast-scroller-android
             */

            initialiseData();
            //scroll with recylerview
            fastScroller.setRecyclerView(rv_showvideo);
            fastScroller.setUpAlphabet(mAlphabetItems);
        }


        navigationSpinner = new Spinner(getSupportActionBar().getThemedContext());

        navigationSpinner.setAdapter(new CustomeSpinner(this, R.layout.custom_spinner, listWithoutDuplicates));
        toolbar.addView(navigationSpinner, 0);


        navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selecteditem = navigationSpinner.getSelectedItem().toString();


                //condition for which tab open in what senario
                if (!selecteditem.equals("...@#$%")) {
                    host.setCurrentTab(0);
                    if (type.equals("favrt")) {
                        type = "favrt";

                    } else {
                        type = "";
                    }
                }
                if (type.equals("all")) {
                    host.setCurrentTab(0);
                } else if (type.equals("favrt")) {
                    host.setCurrentTab(1);
                    type = "";
                } else {
                    host.setCurrentTab(0);

                }

                if (selecteditem.equals(" All")) {
                    arrayList.clear();
                    getfolders();
                } else {
                    progressDialog.show();
                    arrayList.clear();
                    getVideoCatWise(selecteditem);

                }

                initialiseData();
                //scroll with recylerview
                fastScroller.setRecyclerView(rv_showvideo);
                fastScroller.setUpAlphabet(mAlphabetItems);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        rv_showvideo.addOnItemTouchListener(new RecyclerItemClickListener(this, rv_showvideo, new RecyclerItemClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                if (isMultiSelect) {
                    multi_select(position);
                } else {
                    // openDocument(ApkList.get(position).getFilePath());
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (!isMultiSelect) {
                    isMultiSelect = true;

                    if (mActionMode == null) {
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

        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);


        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences1.edit();
        editor.putString("type", "all");
        editor.apply();

    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences1.edit();
        editor.putString("type", "all");
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //search view
        getMenuInflater().inflate(R.menu.home, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        //serchview textcolor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
        } else {
            // Implement this feature without material design
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = (TextView) searchView.findViewById(id);
            textView.setTextColor(Color.BLACK);
        }
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                int tab = host.getCurrentTab();
                if (tab == 0) {
                    showVideoAdapter.getFilter().filter(query);
                } else if (tab == 1) {
                    favrtAdapter.getFilter().filter(query);
                } else if (tab == 2) {
                    searchView.setIconified(true);

                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                int tabb = host.getCurrentTab();

                if (tabb == 0) {
                    showVideoAdapter.getFilter().filter(query);
                } else if (tabb == 1) {
                    favrtAdapter.getFilter().filter(query);
                } else if (tabb == 2) {
                    searchView.setIconified(true);

                }
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

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Google Play Store Link");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //get all folders contain video
    public void getfolders() {

        getfavrt();

        String[] projection = {MediaStore.Video.Media.SIZE, MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Media.DURATION, MediaStore.Video.Thumbnails.DATA, MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        try {
            cursor.moveToLast();

            do {
                videoFolderNamearray.add(" All");
                videoFolderNamearray.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))));
                videoPatharray.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA))));

                HashSet<String> listToSet = new HashSet<String>(videoFolderNamearray);
                listWithoutDuplicates = new ArrayList<>(listToSet);
//                listWithoutDuplicates.addAll(listToSet);
                Collections.sort(listWithoutDuplicates);


                foldername = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                thumb = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED));
                resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
                size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                //convert size in bytes
                long fileSizeInBytes = Long.parseLong(size);

                //change bytes in MBKB
                MBKB = Helper.humanReadableByteCount(fileSizeInBytes, true);
                String da = Helper.LongToDate(date);
                String tt = Helper.convertDuration(Long.parseLong(duration));
                Log.i("duration", duration);

                ShowVideo showVideo = new ShowVideo();
                showVideo.setThumb(thumb);
                showVideo.setDate(da);
                showVideo.setTime(tt);
                showVideo.setFolder(url);
                showVideo.setName(name);

                arrayList.add(new ShowVideo(thumb, resolution, tt, da, url, name, MBKB));


            } while (cursor.moveToPrevious());
            Collections.sort(arrayList, new Comparator<ShowVideo>() {
                public int compare(ShowVideo o1, ShowVideo o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());

                }
            });
            cursor.close();
            showVideoAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //get video according to folder name
    public void getVideoCatWise(String s) {
        String[] projection = {MediaStore.Video.Media.DURATION, MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Thumbnails.DATA, MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
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
                    resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));

                    String da = Helper.LongToDate(date);
                    String tt = Helper.convertDuration(Long.parseLong(duration));
                    arrayList.add(new ShowVideo(thumb, resolution, tt, da, url, name, s));

                }

            } while (cursor.moveToPrevious());
            showVideoAdapter.notifyDataSetChanged();
            initialiseData();
            //scroll with recylerview
            fastScroller.setRecyclerView(rv_showvideo);
            fastScroller.setUpAlphabet(mAlphabetItems);

            progressDialog.dismiss();
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //get all data from database and add in list
    public void getfavrt() {

        favrtArrayList.clear();
        Cursor res = databaseHelper.getalldata();

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
            Favrt favrt = new Favrt();
            favrt.setId(res.getString(0));
            favrt.setName(res.getString(1));
            favrt.setThumb(res.getString(2));
            favrt.setFolder(res.getString(3));
            favrt.setTime(res.getString(4));
            favrt.setResolution(res.getString(5));
            favrt.setDate(res.getString(6));
            favrt.setSize(res.getString(7));
            favrtArrayList.add(favrt);


        }


        //add data in recyclerview
        rv_showfavrt = findViewById(R.id.rv_showfavrt);
        rv_showfavrt.setLayoutManager(new LinearLayoutManager(this));
        favrtAdapter = new FavrtAdapter(favrtArrayList, this);
        rv_showfavrt.setAdapter(favrtAdapter);
        favrtAdapter.notifyDataSetChanged();


    }


    //multi selete item for delete
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
                mActionMode.finish();
            }

            refreshAdapter();

        }
    }

    public void refreshAdapter() {
        showVideoAdapter.selected_ApkList = multiselect_list;
        showVideoAdapter.showVideoArrayList = arrayList;
        showVideoAdapter.notifyDataSetChanged();
        selectMenuChnage();
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
                    if (arrayList.size() == multiselect_list.size() || isUnseleAllEnabled == true)
                        unSelectAll();
                    else
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

            showVideoAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        host.setCurrentTab(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    //tab click funtionality
    @Override
    public void onTabChanged(String s) {
        tabnumber = 0;

        if (s.equals("Device")) {
            tabnumber = 0;

            rv_showvideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            showVideoAdapter = new ShowVideoAdapter(arrayList, multiselect_list, HomeActivity.this);
            rv_showvideo.setAdapter(showVideoAdapter);

        } else if (s.equals("favourite")) {
            tabnumber = 1;

            favrtArrayList.clear();
            Cursor res = databaseHelper.getalldata();

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

                Favrt favrt = new Favrt();
                favrt.setId(res.getString(0));
                favrt.setName(res.getString(1));
                favrt.setThumb(res.getString(2));
                favrt.setFolder(res.getString(3));
                favrt.setTime(res.getString(4));
                favrt.setResolution(res.getString(5));
                favrt.setDate(res.getString(6));
                favrt.setSize(res.getString(7));
                favrtArrayList.add(favrt);

            }
            /*for (int j = 0; j < favrtArrayList.size(); j++) {


                  Favrt f =  favrtArrayList.get(j);

                  for(int i=0;i<arrayList.size();i++)
                  {
                      if (arrayList.get(i).getName().equalsIgnoreCase(f.getName())) {
                          System.out.print("found");
                      } else {
                          System.out.print("not found");

                          databaseHelper.deletedata(f.getName());
                          int count=databaseHelper.getalldatacount();
                          System.out.print(""+count);
                      }
                  }




            }*/

            rv_showfavrt = findViewById(R.id.rv_showfavrt);
            rv_showfavrt.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            favrtAdapter = new FavrtAdapter(favrtArrayList, HomeActivity.this);
            rv_showfavrt.setAdapter(favrtAdapter);
            favrtAdapter.notifyDataSetChanged();

        } else {
            tabnumber = 2;

        }
        pager.setCurrentItem(tabnumber);

    }


    //delete multiseleted item
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

    private void unSelectAll() {
        if (mActionMode != null) {
            multiselect_list.clear();

            if (multiselect_list.size() >= 1)
                mActionMode.setTitle("" + multiselect_list.size());
            else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }
            //to change  the unselectAll  menu  to  selectAll
            selectMenuChnage();
            //to change  the unselectAll  menu  to  selectAll
            refreshAdapter();

        }
    }

    private void selectMenuChnage() {
        if (context_menu != null) {
            if (arrayList.size() == multiselect_list.size()) {
                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_selectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_unselectAll));
                        isUnseleAllEnabled = true;
                    }
                }
            } else {

                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_unselectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_selectAll));

                        isUnseleAllEnabled = false;
                    }
                }

            }

        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((LinearLayoutManager) rv_showvideo.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);

        //back from play screen focus on recent tab
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        type = preferences.getString("type", "");

        if (type.equals("all")) {
            host.setCurrentTab(0);
        } else if (type.equals("favrt")) {
            host.setCurrentTab(1);
        } else {
            host.setCurrentTab(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastFirstVisiblePosition = ((LinearLayoutManager) rv_showvideo.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }

    //get scroll data from array list
    public void initialiseData() {
        //Recycler view data
//        mDataArray = DataHelper.getAlphabetData();

        //Alphabet fast scroller data
        mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            String name = arrayList.get(i).getName().toUpperCase();
//            if (name == null || name.trim().isEmpty())
//                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {

                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }
    }




}