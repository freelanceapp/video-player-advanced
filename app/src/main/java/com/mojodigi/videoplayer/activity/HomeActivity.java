package com.mojodigi.videoplayer.activity;

import android.Manifest;
import android.app.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Person;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mojodigi.videoplayer.AddsUtility.AddConstants;
import com.mojodigi.videoplayer.AddsUtility.AddMobUtils;
import com.mojodigi.videoplayer.AddsUtility.JsonParser;
import com.mojodigi.videoplayer.AddsUtility.OkhttpMethods;
import com.mojodigi.videoplayer.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.videoplayer.adapter.VideoAdapter;
import com.mojodigi.videoplayer.interfaces.VideoListener;
import com.mojodigi.videoplayer.model.VideoDataModel;
import com.mojodigi.videoplayer.utils.MyPreference;
import com.mojodigi.videoplayer.utils.Utilities;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.BannerView;
import com.smaato.soma.ErrorCode;
import com.smaato.soma.ReceivedBannerInterface;
import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.mojodigi.videoplayer.R;
import com.mojodigi.videoplayer.adapter.FavrtAdapter;
import com.mojodigi.videoplayer.adapter.ShowVideoAdapter;
import com.mojodigi.videoplayer.model.Favrt;
import com.mojodigi.videoplayer.model.ShowVideo;

import com.mojodigi.videoplayer.utils.AlertDialogHelper;
import com.mojodigi.videoplayer.utils.CustomeSpinner;
import com.mojodigi.videoplayer.utils.DatabaseHelper;
import com.mojodigi.videoplayer.utils.Helper;
import com.mojodigi.videoplayer.utils.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlertDialogHelper.AlertDialogListener, TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener,AdListenerInterface,VideoListener {
    private static final int REQUEST_PERMISSIONS = 100;
    List<String> videoFolderNamearray = new ArrayList<>();
    ArrayList<String> videoPatharray = new ArrayList<>();
    static ArrayList<ShowVideo> arrayList = new ArrayList<>();
    static ArrayList<Favrt> favrtArrayList = new ArrayList<>();
    ArrayList<Favrt> duplicatefavrtarray = new ArrayList<>();
    public static RecyclerView rv_showvideo, rv_showfavrt,videoRecycler;
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
    String selecteditem, folderpos;
    int folderpostion;
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
    public static RecyclerViewFastScroller fastScroller;
    public static List<AlphabetItem> mAlphabetItems;
    List<String> mDataArray;
    int tabnumber;
    String type = "", folder, folderid;
    SharedPreferences preferences;
    int lastFirstVisiblePosition;
    public static LinearLayout ll_novideo;
    SharedPreferences.Editor editor;
    public static int orientation;



    //add push notification
    private String fcm_Token ="" ;
    public   String deviceID ="";
    public   String nameOfDevice ="";
    public   String appVersionName ="";
    int max_execute ;

    //for permission
    String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    boolean fisrttimeapp = true;


    // dynamicAddsVariables
    private AdView mAdView;
    //smaatoAddBanerView
    BannerView smaaTobannerView;
    SharedPreferenceUtil addprefs;
    BroadcastReceiver internetChangerReceiver;
    View adContainer;
    RelativeLayout smaaToAddContainer;
    private Context mContext;
    // dynamicAddsVariables


    private VideoAdapter videoAdapter;
    private ArrayList<VideoDataModel> videoList;
    private VideoListener videoListener ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext=HomeActivity.this;
       //


        //

        try {
            NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = mNavigationView.getHeaderView(0);
            TextView mNameTextView = (TextView) header.findViewById(R.id.textView);

            // get app version
            PackageInfo packageInfo=getPackageManager().getPackageInfo(getPackageName(), 0);
                String versionName=packageInfo.versionName;
            // get app version

            mNameTextView.setText(Utilities.getString(mContext, R.string.appversion)+versionName);




        }catch (Exception e)
        {

        }

        databaseHelper = new DatabaseHelper(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rv_showvideo = findViewById(R.id.rv_showvideo);
        ll_novideo = findViewById(R.id.ll_novideo);
        fastScroller = findViewById(R.id.fast_scroller);
        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        editor = preferences.edit();
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Wait...");

        alertDialogHelper = new AlertDialogHelper(this);

        //Latest video var


        getLatestVideos();


        //Latest video var

        //tab host
        host = findViewById(R.id.tabHost);
        pager = findViewById(R.id.viewpager);
        tabs = findViewById(android.R.id.tabs);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // set data on recyclerview
        rv_showvideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        showVideoAdapter = new ShowVideoAdapter(mContext,arrayList, multiselect_list, HomeActivity.this);
        rv_showvideo.setAdapter(showVideoAdapter);


        host.setup();
        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(Utilities.getString(mContext, R.string.device));
        spec.setContent(R.id.tab1);
        spec.setIndicator(Utilities.getString(mContext, R.string.device));
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec(Utilities.getString(mContext,R.string.favorite ));
        spec.setContent(R.id.tab2);
        spec.setIndicator(Utilities.getString(mContext, R.string.favorite));
        host.addTab(spec);


        //Tab 3
        spec = host.newTabSpec(Utilities.getString(mContext, R.string.latest_vdo));
        spec.setContent(R.id.tab3);
        spec.setIndicator(Utilities.getString(mContext, R.string.latest_vdo));
        host.addTab(spec);

        for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
//            Typeface tf = Typeface.createFromAsset(getAssets(), "corbel.ttf");
            final TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTypeface(Helper.typeFace_corbel(this));
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            tv.setGravity(Gravity.CENTER);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                host.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white));
            }*/
        }



        pager.setOnPageChangeListener(this);
        host.setOnTabChangedListener(this);

        getPermissions();
        navigationSpinner = new Spinner(getSupportActionBar().getThemedContext());
        navigationSpinner.setAdapter(new CustomeSpinner(this, R.layout.custom_spinner, listWithoutDuplicates));
        toolbar.addView(navigationSpinner, 0);


        navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                folder = preferences.getString("folder", "");
                folderid = preferences.getString("folderid", "");
                folderpos = preferences.getString("folderpos", "");

                if (folderid.equals("home")) {

                    editor.putString("folderid", "homeee");
                    editor.apply();
                    selecteditem = navigationSpinner.getSelectedItem().toString();
//                    selecteditem = folder;
                    folderpostion = Integer.parseInt(folderpos);
                    navigationSpinner.setSelection(position);
                    getVideoCatWise(selecteditem);

                } else {
                    selecteditem = navigationSpinner.getSelectedItem().toString();
                    folderpostion = position;

                }
                //condition for which tab open in what senario
                if (!selecteditem.equals("...@#$%")) {
                    host.setCurrentTab(0);
                    if (type.equals("favrt")) {
                        type = "favrt";

                    } else {
                        type = "";
                    }
                }

                //on spinnerchange device and favrt sinario
                if (type.equals("all")) {
                    host.setCurrentTab(0);

                } else if (type.equals("favrt")) {
                    host.setCurrentTab(1);
                    type = "";
                } else if (type.equals("favrttab")) {
                    host.setCurrentTab(1);
                }  else {
                    host.setCurrentTab(0);
                }

                if (selecteditem.equals(" All")) {
                    getfolders();

                } else {
                    progressDialog.show();
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

                //for smooth scrolling of recyclerview
                orientation = getResources().getConfiguration().orientation;

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


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);




        //add netwrk varibales
        addprefs=new SharedPreferenceUtil(mContext);
        mAdView = (AdView) findViewById(R.id.adView_home);
        adContainer = findViewById(R.id.adMobView);
        smaaToAddContainer = findViewById(R.id.smaaToAddContainer);
        smaaTobannerView = new BannerView((this).getApplication());
        smaaTobannerView.addAdListener(this);



        // this broadcast  will  listen the  internet state change for sendig request  when internet becomes available
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        internetChangerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean isNetworkAvailable = AddConstants.checkIsOnline(mContext);

                //  Toast.makeText(context, "isNetworkAvailable-->" + isNetworkAvailable, Toast.LENGTH_SHORT).show();

                Log.d("isNetworkAvailable", "" + isNetworkAvailable);
                if (isNetworkAvailable) {
                    new WebCall().execute();

                } else {
                    if (mAdView != null && addprefs != null) {
                        AddMobUtils util = new AddMobUtils();
                        util.displayLocalBannerAdd(mAdView);
                        //util.showInterstitial(addprefs,HomeActivity.this, null);
                        //util.displayRewaredVideoAdd(addprefs, mContext, null);
                    }
                }
            }

        };
        registerReceiver(internetChangerReceiver, intentFilter);
        // this broadcast  will  listen the  internet state change for sendig request  when internet becomes available

        if(addprefs!=null) {
            boolean st=addprefs.getBoolanValue(AddConstants.isFcmRegistered, false);
            System.out.print(""+st);
            if(!addprefs.getBoolanValue(AddConstants.isFcmRegistered, false)) {
                getPushToken();
            }
        }



    }


    private void getLatestVideos() {


        this.videoListener=this;
        videoRecycler = (RecyclerView) findViewById(R.id.videoRecycler);
        videoList = new ArrayList<VideoDataModel>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        videoRecycler.setLayoutManager(linearLayoutManager);


        new WebCall_GetVideo().execute();


    }

    @Override
    public void onVideoClicked(VideoDataModel videoDataModel) {

        String url=videoDataModel.getVideoUrl();


         if(url!=null) {
             //Intent mIntent = new Intent(getActivity() , MediaWebActivity.class);
             //startActivity(mIntent);
             if (!url.trim().isEmpty()) {
                 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                 startActivity(browserIntent);
             }
         }


    }

    public class WebCall_GetVideo extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //CustomProgressDialog.show(mContext, getResources().getString(R.string.loading_msg));
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return OkhttpMethods.CallApiGetNews(mContext, AddConstants.API_URL_VIDEO);
            } catch (Exception e) {
                e.printStackTrace();
                return ""+e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("JsonResponse", s);
            if (addprefs != null)
            {
                int responseCode = addprefs.getIntValue(AddConstants.API_RESPONSE_CODE_GET_NEWS, 0);
                if (s != null  && responseCode==200 ) {


                    try {
                        JSONObject mainJson = new JSONObject(s);

                        if (mainJson.has("status")) {

                            String status = JsonParser.getkeyValue_Str(mainJson, "status");
                            String data = JsonParser.getkeyValue_Str(mainJson, "data");

                            if (status.equalsIgnoreCase("200")) {
                                if (mainJson.has("data")) {

                                    JSONArray jsonarray = new JSONArray(JsonParser.getkeyValue_Str(mainJson, "data"));

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject dataJson = jsonarray.getJSONObject(i);
                                        //Log.e("dataJson ", dataJson+"");
                                        String author = JsonParser.getkeyValue_Str(dataJson, "author");
                                        String title = JsonParser.getkeyValue_Str(dataJson, "title");
                                        String content = JsonParser.getkeyValue_Str(dataJson, "content");
                                        String date = JsonParser.getkeyValue_Str(dataJson, "date");
                                        String thumbnail = JsonParser.getkeyValue_Str(dataJson, "thumbnail");
                                        String url = JsonParser.getkeyValue_Str(dataJson, "url");
                                        String slug=JsonParser.getkeyValue_Str(dataJson, "slug");

                                        url=Helper.appendUrl(slug);

                                        VideoDataModel model  =  new VideoDataModel();
                                        model.setVideoAuthor(author);
                                        model.setVideoTitle(title);
                                        model.setVideoContent(content);
                                        model.setVideoDate(date);
                                        model.setVideoThumbnail(thumbnail);
                                        model.setVideoUrl(url);
                                        videoList.add(model);


                                    }

                                    videoAdapter = new VideoAdapter(videoList, videoListener , mContext);
                                    videoRecycler.setAdapter(videoAdapter);

                                } else {
                                    String message = JsonParser.getkeyValue_Str(mainJson, "message");
                                    Log.d("message", "" + message);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.d("jsonParse", "error while parsing json -->" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Exception Code :", "" + responseCode);
                }
            }
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                getfolders();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                getfolders();

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                    ) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permissions");
                builder.setMessage(this.getString(R.string.app_name) + " app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(HomeActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //Toast.makeText(this, Utilities, Toast.LENGTH_LONG).show();
                Utilities.dispToast(mContext, R.string.unable_to_get_permission);
            }
        }


    }


    @Override
    protected void onDestroy() {
        MyPreference myPreference=new MyPreference(this);

        final String type = myPreference.getPlaytype(MyPreference.PREFS_NAME);
        Log.i("playtype-homedestroy",type);

        editor = preferences.edit();
        editor.remove("type");
        editor.remove("videopostion");
        editor.remove("folderid");
        //editor.remove(MyPreference.PREFS_NAME);
//        editor.clear();
        editor.commit();


        if(internetChangerReceiver!=null)
            unregisterReceiver(internetChangerReceiver);

        super.onDestroy();




      /*  SharedPreferences.Editor mEditor = preferences.edit();
        mEditor.putString("folderpos", "" + 0);
        mEditor.putString("type","all");
        mEditor.apply();*/


    }

    @Override
    protected void onStop() {
        super.onStop();

        lastFirstVisiblePosition = ((LinearLayoutManager) rv_showvideo.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

        //change cursor color
        final int textViewID = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        final AutoCompleteTextView searchTextView = searchView.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            searchTextView.setCursorVisible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }

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
                } /*else if (tab == 2) {
                    searchView.setIconified(true);
                }*/
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                int tabb = host.getCurrentTab();

                if (tabb == 0) {
                    showVideoAdapter.getFilter().filter(query);

                    if (arrayList.size() == 0) {
//                        Toast.makeText(HomeActivity.this, "wewe", Toast.LENGTH_SHORT).show();
                    }

                } else if (tabb == 1) {
                    favrtAdapter.getFilter().filter(query);
                }/* else if (tabb == 2) {
                    searchView.setIconified(true);

                }*/
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
        }
        else if (id == R.id.nav_privacy_policy) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AddConstants.privacyUrl));
            startActivity(browserIntent);
        }


        /* else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Google Play Store Link");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } *//*else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));

        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //get all folders contain video
    public void getfolders() {

        getfavrt();

        arrayList.clear();
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

            //for scrollbar data
            initialiseData();
            //scroll with recylerview
            fastScroller.setRecyclerView(rv_showvideo);
            fastScroller.setUpAlphabet(mAlphabetItems);

            if (fisrttimeapp) {
                navigationSpinner = new Spinner(getSupportActionBar().getThemedContext());
                navigationSpinner.setAdapter(new CustomeSpinner(this, R.layout.custom_spinner, listWithoutDuplicates));
                toolbar.addView(navigationSpinner, 0);
                fisrttimeapp = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //get video according to folder name
    public void getVideoCatWise(String s) {

        arrayList.clear();
        String[] projection = {MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Thumbnails.DATA, MediaStore.Video.VideoColumns.DATE_ADDED, MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
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
                    size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                    //convert size in bytes
                    long fileSizeInBytes = Long.parseLong(size);

                    //change bytes in MBKB
                    MBKB = Helper.humanReadableByteCount(fileSizeInBytes, true);
                    String da = Helper.LongToDate(date);
                    String tt = Helper.convertDuration(Long.parseLong(duration));
                    arrayList.add(new ShowVideo(thumb, resolution, tt, da, url, name, MBKB));

                }

            } while (cursor.moveToPrevious());
            Collections.sort(arrayList, new Comparator<ShowVideo>() {
                public int compare(ShowVideo o1, ShowVideo o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            cursor.close();

            showVideoAdapter.notifyDataSetChanged();
            initialiseData();
            //scroll with recylerview
            fastScroller.setRecyclerView(rv_showvideo);
            fastScroller.setUpAlphabet(mAlphabetItems);


            progressDialog.dismiss();

            progressDialog.dismiss();

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
            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
            } else
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
            editor = preferences.edit();
            editor.remove("type");
            editor.commit();
            type = preferences.getString("type", "all");

            rv_showvideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            showVideoAdapter = new ShowVideoAdapter(mContext,arrayList, multiselect_list, HomeActivity.this);
            rv_showvideo.setAdapter(showVideoAdapter);

        } else if (s.equals("favourite")) {
            tabnumber = 1;
            editor = preferences.edit();
            editor.remove("type");
            editor.commit();
            type = preferences.getString("type", "favrttab");
//            folderpostion = 0;

            favrtArrayList.clear();
            Cursor res = databaseHelper.getalldata();

            //get video from database
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

        } /*else {
            tabnumber = 2;

        }*/
        pager.setCurrentItem(tabnumber);

        //finish  action mode  if tab is changed by the user;
        if(mActionMode!=null)
        {
            mActionMode.finish();
        }


    }

    @Override
    public void onReceiveAd(AdDownloaderInterface adDownloaderInterface, ReceivedBannerInterface receivedBannerInterface) {
        if(receivedBannerInterface.getErrorCode() != ErrorCode.NO_ERROR){
            // Toast.makeText(getBaseContext(), receivedBanner.getErrorMessage(), Toast.LENGTH_SHORT).show();
            Log.d("SmaatoErrorMsg", ""+receivedBannerInterface.getErrorMessage());

            if(receivedBannerInterface.getErrorMessage().equalsIgnoreCase(AddConstants.NO_ADDS))
            {
                smaaToAddContainer.setVisibility(View.GONE);
            }
        }
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

            Toast.makeText(HomeActivity.this, FileCount +" "+ Utilities.getString(mContext, R.string.file_deleted), Toast.LENGTH_SHORT).show();


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

            if (multiselect_list.size() >= 1) {
                mActionMode.setTitle("" + multiselect_list.size());

            } else {
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
        Helper.hideKeyboard(HomeActivity.this);
        invalidateOptionsMenu();
    }

    @Override
    protected void onResume() {

       /* //for smooth scrolling of recyclerview
        videopostion = preferences.getInt("videopostion", videopostion);
        rv_showvideo.smoothScrollToPosition(videopostion);
        progressDialog.dismiss();
*/



        //back from play screen focus on recent tab
        type = preferences.getString("type", "");

        if (type.equals("all")) {
            host.setCurrentTab(0);
        } else if (type.equals("favrt")) {
            host.setCurrentTab(1);
        }


        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        type = preferences.getString("type", "");

        editor.putString("folder", selecteditem);
        editor.putString("folderid", "home");
        editor.putString("folderpos", "" + folderpostion);
        editor.apply();


      /*  if (type.equals("all")) {
            host.setCurrentTab(0);
        } else if (type.equals("favrt")) {
            host.setCurrentTab(1);
        } else {
            host.setCurrentTab(0);
        }
*/
    }

    //get scroll data from array list
    public static void initialiseData() {
        //Recycler view data
//        mDataArray = DataHelper.getAlphabetData();
        try {
            //Alphabet fast scroller data
            mAlphabetItems = new ArrayList<>();
            List<String> strAlphabets = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getName() != null)
                {
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
        }catch (Exception  ex)
        {
            ex.printStackTrace();
        }
    }

    public void getPermissions() {
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                    ) {
                //Show Information about why you need the permission

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Contacts and Location permissions.");

                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(HomeActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Storage and settings permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Utilities.dispToast(mContext, R.string.go_to_permission);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], false);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            getfolders();

        }
    }


    public class WebCall extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                JSONObject requestObj= AddConstants.prepareAddJsonRequest(mContext, AddConstants.VENDOR_ID);
                return OkhttpMethods.CallApi(mContext,AddConstants.API_URL,requestObj.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return ""+e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("JsonResponse", s);

            if (addprefs != null)
            {
                int responseCode=addprefs.getIntValue(AddConstants.API_RESPONSE_CODE, 0);

                if (s != null  && responseCode==200 ) {
                    try {
                        JSONObject mainJson = new JSONObject(s);
                        if (mainJson.has("status")) {
                            String status = JsonParser.getkeyValue_Str(mainJson, "status");

                            String newVersion=JsonParser.getkeyValue_Str(mainJson,"appVersion");
                            addprefs.setValue(AddConstants.APP_VERSION, newVersion);

                            if (status.equalsIgnoreCase("true")) {

                                String adShow = JsonParser.getkeyValue_Str(mainJson, "AdShow");

                                if (adShow.equalsIgnoreCase("true")) {
                                    if (mainJson.has("data")) {
                                        JSONObject dataJson = mainJson.getJSONObject("data");

                                        String show_Add = JsonParser.getkeyValue_Str(mainJson, "AdShow");
                                        String adProviderId =JsonParser.getkeyValue_Str(dataJson, "adProviderId");
                                        String adProviderName = JsonParser.getkeyValue_Str(dataJson, "adProviderName");

                                        String appId_PublisherId = JsonParser.getkeyValue_Str(dataJson, "appId_PublisherId");
                                        String bannerAdId = JsonParser.getkeyValue_Str(dataJson, "bannerAdId");
                                        String interstitialAdId = JsonParser.getkeyValue_Str(dataJson, "interstitialAdId");
                                        String videoAdId = JsonParser.getkeyValue_Str(dataJson, "videoAdId");


//                                        String appId_PublisherId = "ca-app-pub-3940256099942544~3347511713";//testID
//                                        String bannerAdId = "ca-app-pub-3940256099942544/6300978111"; //testId
//                                        String interstitialAdId = "ca-app-pub-3940256099942544/1033173712";//testId
//                                         String videoAdId = "ca-app-pub-3940256099942544/5224354917";//testId


                                           //for smaato bannerAdd  testIds
//                                        String appId_PublisherId = "0";//testID
//                                        String bannerAdId = "0"; //testId


                                        Log.d("AddiDs", adProviderName + " ==" + appId_PublisherId + "==" + bannerAdId + "==" + interstitialAdId + "==" + videoAdId);


                                        //check for true value above in code so  can put true directly;
                                        try {
                                            addprefs.setValue(AddConstants.SHOW_ADD, Boolean.parseBoolean(show_Add));
                                        }catch (Exception e)
                                        {
                                            // IN CASE OF EXCEPTION CONSIDER  FALSE AS THE VALUE WILL NOT BE TRUE,FALSE.
                                            addprefs.setValue(AddConstants.SHOW_ADD, false);
                                        }

                                        addprefs.setValue(AddConstants.ADD_PROVIDER_ID, adProviderId);
                                        addprefs.setValue(AddConstants.APP_ID, appId_PublisherId);
                                        addprefs.setValue(AddConstants.BANNER_ADD_ID, bannerAdId);
                                        addprefs.setValue(AddConstants.INTERESTIAL_ADD_ID, interstitialAdId);
                                        addprefs.setValue(AddConstants.VIDEO_ADD_ID, videoAdId);
                                             AddMobUtils util=new AddMobUtils();
                                        if (adContainer != null  && adProviderId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId))

                                        {
                                            // requst googleAdd

                                            util.displayServerBannerAdd(addprefs, adContainer, mContext);
                                            // util.showInterstitial(addprefs,HomeActivity.this, interstitialAdId);
                                            //util.displayRewaredVideoAdd(addprefs,mContext, videoAdId);


                                        }
                                        else if (adProviderId.equalsIgnoreCase(AddConstants.InMobiProvideId))
                                        {

                                            // inmobi adds not being implemented in this version
                                            // inmobi adds not being implemented in this version

                                        }
                                        else if( smaaTobannerView !=null && adProviderId.equalsIgnoreCase(AddConstants.SmaatoProvideId))
                                        {
                                            //requestSmaatoBanerAdds


                                            try {
                                                int publisherId = Integer.parseInt(addprefs.getStringValue(AddConstants.APP_ID, AddConstants.NOT_FOUND));
                                                int addSpaceId = Integer.parseInt(addprefs.getStringValue(AddConstants.BANNER_ADD_ID, AddConstants.NOT_FOUND));
                                                util.displaySmaatoBannerAdd(smaaTobannerView, smaaToAddContainer, publisherId, addSpaceId);
                                            }catch (Exception e)
                                            {
                                                String string = e.getMessage();
                                                System.out.print(""+string);
                                            }


//
                                            //requestSmaatoBanerAdds
                                        }

                                        //requestfacebookBanerAdds
                                         else if(adProviderId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
                                           {
                                                   util.dispFacebookBannerAdd(mContext,addprefs , HomeActivity.this);
                                              }




                                    } else {
                                        String message = JsonParser.getkeyValue_Str(mainJson, "message");
                                        Log.d("message", "" + message);
                                    }
                                } else {
                                    String message = JsonParser.getkeyValue_Str(mainJson, "message");

                                    Log.d("message", "" + message);

                                }


                            }

                            dispUpdateDialog();
                        }

                    } catch (JSONException e) {
                        Log.d("jsonParse", "error while parsing json -->" + e.getMessage());
                        e.printStackTrace();
                    }


                } else {
                    // display loccal AddiDs Adds;
                    if (mAdView != null) {
                        AddMobUtils util = new AddMobUtils();
                        util.displayLocalBannerAdd(mAdView);
                        //util.showInterstitial(addprefs,HomeActivity.this, null);
                        // util.displayRewaredVideoAdd(addprefs,mContext, null);
                    }
                }


            }

        }
    }

    private void getPushToken()
    {
        /***********************Start**********************************************/

        deviceID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("Android ID : ",""+deviceID);
        nameOfDevice = Build.MANUFACTURER+" "+Build.MODEL+" "+Build.VERSION.RELEASE;
        Log.e("Device Name : ",""+nameOfDevice);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionName = pinfo.versionName;
            Log.e("App Version Name : ",""+appVersionName);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }catch (Exception ex){ ex.printStackTrace();}



        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( HomeActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcm_Token = instanceIdResult.getToken();
                        Log.e("New Token : ", fcm_Token);

                        if (AddConstants.checkIsOnline(mContext)) {
                            Log.e("Network is available ", "PushNotification Called");
                            new PushNotificationCall().execute();
                        } else {
                            Log.e("No Network", "PushNotification Call failed");
                        }
                    }
                });


        Intent intent = new Intent();
        String manufacturer = android.os.Build.MANUFACTURER;
        switch (manufacturer) {

            case "xiaomi":
                intent.setComponent(new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                break;
            case "oppo":
                intent.setComponent(new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));

                break;
            case "vivo":
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                break;
        }

        List<ResolveInfo> arrayListInfo =  getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        if (arrayListInfo.size() > 0) {
            startActivity(intent);
        }


    }

    public class PushNotificationCall extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Log.e("deviceId ", deviceID);
                Log.e("deviceName ", nameOfDevice);
                Log.e("fcmToken ", fcm_Token);
                Log.e("appVer ", appVersionName);

                JSONObject requestObj = AddConstants.prepareFcmJsonRequest(mContext, deviceID, nameOfDevice, fcm_Token , appVersionName);
                return OkhttpMethods.CallApi(mContext, AddConstants.API_PUSH_NOTIFICATION, requestObj.toString());

            } catch (IOException e) {
                e.printStackTrace();
                return ""+e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("Push Json Response ", s);




            if (s != null  ) {
                try {
                    JSONObject mainJson = new JSONObject(s);
                    if (mainJson.has("status")) {
                        String status = JsonParser.getkeyValue_Str(mainJson, "status");
                        Log.e("status", "" + status);


                        if (status.equalsIgnoreCase("false")) {

                            if (mainJson.has("data")) {
                                JSONObject dataJson = mainJson.getJSONObject("data");
                            } else {
                                String message = JsonParser.getkeyValue_Str(mainJson, "message");
                                Log.e("message", "" + message);
                            }
                        }
                        if (status.equalsIgnoreCase("false")) {
                            Log.e("status", "" + status);

                            if(max_execute<=5){
                                new PushNotificationCall().execute();
                                max_execute++;
                            }
                        }
                        else {
                            if(addprefs!=null)
                                addprefs.setValue(AddConstants.isFcmRegistered, true);
                        }
                    }
                } catch (JSONException e) {
                    Log.d("jsonParse", "error while parsing json -->" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.e("", "else"  );
            }

        }
    }






    private void dispUpdateDialog() {
        try {
            String currentVersion = "0";
            String newVersion="0";
            if(addprefs!=null)
                newVersion=addprefs.getStringValue(AddConstants.APP_VERSION, AddConstants.NOT_FOUND);

            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                Log.d("currentVersion", "" + currentVersion);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (Float.parseFloat(newVersion) > Float.parseFloat(currentVersion) && !newVersion.equalsIgnoreCase("0"))

            {
                if (mContext != null) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_version_update);
                    long time = addprefs.getLongValue("displayedTime", 0);
                    long diff=86400000; // one day
                    //long diff=60000; // one minute;

                    if (time < System.currentTimeMillis() - diff) {
                        dialog.show();
                        addprefs.setValue("displayedTime", System.currentTimeMillis());
                    }

                    TextView later = dialog.findViewById(R.id.idDialogLater);
                    TextView updateNow = dialog.findViewById(R.id.idDialogUpdateNow);
                    TextView idVersionDetailsText = dialog.findViewById(R.id.idVersionDetailsText);
                    TextView idAppVersionText = dialog.findViewById(R.id.idAppVersionText);
                    TextView idVersionTitleText = dialog.findViewById(R.id.idVersionTitleText);


                    idVersionTitleText.setTypeface(Helper.typeFace_corbel(mContext));
                    idVersionDetailsText.setTypeface(Helper.typeFace_corbel(mContext));
                    idAppVersionText.setTypeface(Helper.typeFace_corbel(mContext));
                    later.setTypeface(Helper.typeFace_corbel(mContext));
                    updateNow.setTypeface(Helper.typeFace_corbel(mContext));

                    idAppVersionText.setText(newVersion);

                    later.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                        }
                    });


                    updateNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final String appPackageName = getPackageName(); // package name of the app
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }


                            dialog.dismiss();
                        }
                    });


                }


            }
        }
        catch (Exception e)
        {

        }

    }

}