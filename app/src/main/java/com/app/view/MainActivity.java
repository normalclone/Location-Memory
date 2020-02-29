package com.app.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Scene;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.adapter.SearchGoogleResultAdapter;
import com.app.adapter.SearchLocalResultAdapter;
import com.app.adapter.WayStopAdapter;
import com.app.adapter.tLocation2OverrideAdapter;
import com.app.dao.PlaceDAO;
import com.app.dao.tLocationDAO;
import com.app.model.Place;
import com.app.model.tLocation;
import com.app.util.DateTimeUtil;
import com.app.util.MapboxUtil;
import com.app.util.PlaceUtil;
import com.app.util.SystemUtil;
import com.app.util.TransitionUtil;
import com.app.util.WindowUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener , NavigationView.OnNavigationItemSelectedListener{
    private int LAUNCH_SEEDETAIL_ACTIVITY = 12;
    //Core declaration
    private LocationManager locationManager;

    //Zone declaration
    private DrawerLayout drawerLayout;
    private MapboxMap mapboxMap;
    private MapView mapView;

    //View declaration
    private View rippleView;
    private View selectedMarkerHolder;
    private LinearLayout searchRecyclerViewHolder;

    //Element declaration
    private FloatingActionButton fab_reLocation;
    private FloatingActionButton fab_director;
    private FloatingActionButton fab_saveLocation;
    private FloatingActionButton fab_saveMemoryHolder;
    private FloatingActionButton fab_seeDetailHolder;

    private FloatingActionButton fab_addStop;
    private FloatingActionButton fab_directionList;
    private FloatingActionButton fab_directorCancel;
    private FloatingActionButton fab_startNavigating;
    private RotateLoading fab_searchLoading;

    private EditText edt_search;
    private Toolbar toolbar;
    private ImageButton btn_search;
    private ViewGroup searchView;

    //Variables declaration
    private boolean launchedSaveLocationActivity;
    private boolean launchedSaveMemoryActivity;
    private boolean launchedActivity;

    private boolean f_isEnableLocationComponent = false;
    private boolean f_isSearchFocused = false;
    private boolean f_isOpenListLocation = false;
    private boolean f_isOpenLocationDetail = false;
    private double fab_LocationX = 0;
    private double fab_LocationY = 0;
    private double fab_LocationWidth = 0;

    private RelativeLayout.LayoutParams toolbarUnfocusedParams;
    private RelativeLayout.LayoutParams searchRecycleViewParams;

    private tLocationDAO locationDAO;
    private List<tLocation> savedLocations;
    private Location currentLocation;
    private List<tLocation> nearLocations;

    private Marker desMarker;
    private Location currentMapClickLocation;
    private NavigationMapRoute navigationMapRoute;
    private List<Location> listNavStop;
    private boolean f_isNavigationMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        setupTransitions();
        locationDAO = new tLocationDAO(MainActivity.this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Lấy trạng thái GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //Status bar
        LinearLayout statusBar = findViewById(R.id.status_bar);
        RelativeLayout.LayoutParams statusBarParam = (RelativeLayout.LayoutParams) statusBar.getLayoutParams();
        statusBarParam.height = WindowUtil.getStatusBarHeight(this);
        statusBar.setLayoutParams(statusBarParam);

        //Floating button zone
        LinearLayout fabZone = findViewById(R.id.fab_zone);
        FrameLayout.LayoutParams fabZoneParam = (FrameLayout.LayoutParams) fabZone.getLayoutParams();
        fabZoneParam.bottomMargin = WindowUtil.getNavigationBarHeight(this);
        fabZone.setLayoutParams(fabZoneParam);

        LinearLayout fabDirectorZone = findViewById(R.id.fab_director_zone);
        FrameLayout.LayoutParams fabDirectorZoneParam = (FrameLayout.LayoutParams) fabDirectorZone.getLayoutParams();
        fabDirectorZoneParam.bottomMargin = WindowUtil.getNavigationBarHeight(this);
        fabDirectorZone.setLayoutParams(fabDirectorZoneParam);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.setOnFocusChangeListener(edt_searchFocused);
        edt_search.setOnEditorActionListener(edt_searchInput);

        //Drawer menu
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        syncToolballWithNavigationDrawer();

        //Button
        fab_reLocation = findViewById(R.id.fab_reLocation);
        fab_director = findViewById(R.id.fab_director);
        fab_saveLocation = findViewById(R.id.fab_save_location);

        fab_addStop = findViewById(R.id.fab_add_stop);
        fab_directionList = findViewById(R.id.fab_director_list);
        fab_directorCancel = findViewById(R.id.fab_cancel);
        fab_startNavigating = findViewById(R.id.fab_start_navigate);
        fab_addStop.setEnabled(false);
        fab_directionList.setEnabled(false);
        fab_startNavigating.setEnabled(false);
        TransitionUtil.fabHalfFadeOut(fab_addStop);
        TransitionUtil.fabHalfFadeOut(fab_directionList);
        TransitionUtil.fabHalfFadeOut(fab_startNavigating);

        fab_reLocation.setOnClickListener(fab_reLocationClicked);
        fab_director.setOnClickListener(fab_directorClicked);
        fab_saveLocation.setOnClickListener(fab_saveLocationClicked);
        fab_addStop.setOnClickListener(fab_addStopClicked);
        fab_directionList.setOnClickListener(fab_directionListClicked);
        fab_directorCancel.setOnClickListener(fab_directorCancelClicked);
        fab_startNavigating.setOnClickListener(fab_startNavigatingClicked);

        TransitionUtil.hideNavigationModeFab(fab_directorCancel,
                fab_directionList, fab_addStop, fab_startNavigating);

        btn_search = findViewById(R.id.search_button);
        btn_search.setOnClickListener(btn_searchClicked);
        fab_searchLoading =(RotateLoading) findViewById(R.id.rotateloading);
        fab_searchLoading.setVisibility(View.INVISIBLE);

        //View
        rippleView = findViewById(R.id.ripple_view);
        searchRecyclerViewHolder = findViewById(R.id.search_recycler_view_holder);
    }
    //Search loading
    public  void startSearchingLoading(){
        btn_search.setVisibility(View.INVISIBLE);
        fab_searchLoading.setVisibility(View.VISIBLE);
        fab_searchLoading.start();
    }

    public void stopSearchingLoading(){
        fab_searchLoading.stop();
        fab_searchLoading.setVisibility(View.INVISIBLE);
        btn_search.setVisibility(View.VISIBLE);
    }

    //Override back button
    public void onBackPressed(){
        if(f_isSearchFocused){
            edt_search.clearFocus();
            initUnfocusedToolbarParams();
        }else if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            super.onBackPressed();
        }
    }

    //Override activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == LAUNCH_SEEDETAIL_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                Double lat = data.getDoubleExtra("lat",1);
                Double lng = data.getDoubleExtra("lng", 1);
                Location temp = new Location("Target location");
                temp.setLatitude(lat);
                temp.setLongitude(lng);
                if(MapboxUtil.calculateBetween2Location(temp, currentLocation)>10){
                    listNavStop = new ArrayList<>();
                    listNavStop.add(currentLocation);
                    listNavStop.add(temp);

                    f_isNavigationMode = true;
                    TransitionUtil.navigationModeFabSlideIn(fab_directorCancel, fab_directionList, fab_addStop, fab_startNavigating);

                    if(!fab_directionList.isEnabled() && listNavStop.size() > 0) {
                        fab_directionList.setEnabled(true);
                        TransitionUtil.fabHalfFadeIn(fab_directionList);
                    }

                    if(listNavStop != null && listNavStop.size() > 1) getRoute(listNavStop);
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.distance_not_allow_director), Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    //EditText Event
    private View.OnFocusChangeListener edt_searchFocused = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                initFocusedToolbarParams();
                searchOnDelayed();
            }else{
                checkSearchEditTextTask.cancel(true);
                initUnfocusedToolbarParams();
            }
        }
    };
    private CheckSearchEditTextTask checkSearchEditTextTask = null;
    private String currentEditTextSearch = "";

    private TextView.OnEditorActionListener edt_searchInput = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setSearchResultAdapter();
            }
            return false;
        }

//        @Override
//        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            if(event.getAction() == KeyEvent.ACTION_DOWN){
//                if(keyCode == KeyEvent.KEYCODE_ENTER){setSearchResultAdapter();}
//                else{
//                    if(checkSearchEditTextTask != null){
//                        checkSearchEditTextTask.cancel(true);
//                    }
//                    checkSearchEditTextTask.execute();
//                }
//                return true;
//            }
//            return false;
//        }
    };

    private void searchOnDelayed(){
        checkSearchEditTextTask = new CheckSearchEditTextTask();
        checkSearchEditTextTask.execute();
    }
    String searchedString = "";
    private final class CheckSearchEditTextTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentEditTextSearch = edt_search.getText().toString();
        }

        @Override
        protected String doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // We were cancelled; stop sleeping!
                }
                return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if(currentEditTextSearch.equals("")){
                searchOnDelayed();
            }
            else if(!currentEditTextSearch.equals(searchedString) && currentEditTextSearch.equals(edt_search.getText().toString())){
                setSearchResultAdapter();
                searchOnDelayed();
                searchedString = edt_search.getText().toString();
            }else{
                searchOnDelayed();
            }
        }
    }

    private void initFocusedToolbarParams(){
        toolbarUnfocusedParams = WindowUtil.getToolBarParams(MainActivity.this, toolbar);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rect_toolbar) );
        } else {
            toolbar.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rect_toolbar));
        }
        RelativeLayout.LayoutParams toolbarForcusedParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        toolbarForcusedParams.topMargin = WindowUtil.getStatusBarHeight(MainActivity.this);
        toolbarForcusedParams.leftMargin = 0;
        toolbarForcusedParams.rightMargin = 0;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);

        toolbar.setLayoutParams(toolbarForcusedParams);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(navIconClicked);

        Transition slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(200);
        slide.setInterpolator(new FastOutSlowInInterpolator());
        slide.addListener(searchHolderSlideIn);

        mapboxMap.getUiSettings().setAllGesturesEnabled(false);
        searchRecycleViewParams = (RelativeLayout.LayoutParams) searchRecyclerViewHolder.getLayoutParams();
        searchRecycleViewParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        searchRecyclerViewHolder.setLayoutParams(searchRecycleViewParams);

        Scene searchScene = Scene.getSceneForLayout(searchRecyclerViewHolder, R.layout.listview_search,MainActivity.this);
        TransitionManager.go(searchScene, slide);
        searchView = searchScene.getSceneRoot();
        f_isSearchFocused = true;
        setGoogleSearchResultAdapter(new PlaceDAO(MainActivity.this).getAll(), "");
    }

    private void initUnfocusedToolbarParams(){
        edt_search.setText("");
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.round_toolbar) );
        } else {
            toolbar.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.round_toolbar));
        }
        toolbar.setLayoutParams(toolbarUnfocusedParams);
        setSupportActionBar(toolbar);
        syncToolballWithNavigationDrawer();

        Scene searchScene = Scene.getSceneForLayout(searchRecyclerViewHolder, R.layout.close_scene,MainActivity.this);
        Transition slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(200);
        slide.setInterpolator(new FastOutSlowInInterpolator());
        slide.addListener(searchHolderSlideOut);
        TransitionManager.go(searchScene, slide);
        mapboxMap.getUiSettings().setAllGesturesEnabled(true);
        f_isSearchFocused = false;
        searchView = null;
    }

    //Toolbar action
    private View.OnClickListener navIconClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            edt_search.clearFocus();
            edt_search.setText("");
            initUnfocusedToolbarParams();
        }
    };

    private  void syncToolballWithNavigationDrawer(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#000000"));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.open_list_location:{
                Intent i = new Intent(MainActivity.this, ListLocationActivity.class);
                i.putExtra("longitude", currentLocation.getLongitude());
                i.putExtra("latitude", currentLocation.getLatitude());
                startActivity(i);
                f_isOpenListLocation = true;
            }break;
            case R.id.open_list_memory:{
                SystemUtil.showLoading(getString(R.string.loading), MainActivity.this);
                Intent i = new Intent(MainActivity.this, ListMemoryActivity.class);
                startActivity(i);
            }break;
        }
        return true;
    }

    RecyclerView localSearch;
    RecyclerView googleSearch;
    private View.OnClickListener btn_searchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!f_isSearchFocused){
                initFocusedToolbarParams();
                edt_search.requestFocus();
            }else {
                setSearchResultAdapter();
            }
        }
    };

    public void flyToSavedLocation(tLocation location){
        edt_search.clearFocus();
        initUnfocusedToolbarParams();
        MapboxUtil.flyToLocation(location.getLocation().getLatitude(),location.getLocation().getLongitude(), mapboxMap);
    }

    public void createMarkAndFlyToLocation(final Place place){
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        Picasso.get().load(place.getIcon()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                pd.dismiss();
                edt_search.clearFocus();
                initUnfocusedToolbarParams();
                Marker marker = mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(),place.getLongitude()))
                        .snippet(place.getAddress())
                        .title(place.getName())
                        .icon(IconFactory.getInstance(MainActivity.this).fromBitmap(bitmap))
                );
                MapboxUtil.flyToLocation(place.getLatitude(), place.getLongitude(), mapboxMap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                pd.dismiss();
                edt_search.clearFocus();
                initUnfocusedToolbarParams();
                Marker marker = mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(),place.getLongitude()))
                        .snippet(place.getAddress())
                        .title(place.getName())
                        .icon(IconFactory.getInstance(MainActivity.this).fromResource(R.drawable.ic_location_on_red_24dp))
                );
                MapboxUtil.flyToLocation(place.getLatitude(), place.getLongitude(), mapboxMap);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                pd.setCancelable(false);
                pd.setMessage(getString(R.string.loading));
                pd.create();
            }
        });
    }

    public void setSearchResultAdapter(){
        setLocalSearchResultAdapter(edt_search.getText().toString());
        PlaceUtil.getSearchResult(MainActivity.this, edt_search.getText().toString(), currentLocation, this);
    }

    public void setGoogleSearchResultAdapter(List<Place> list, String kw){
        googleSearch = searchView.findViewById(R.id.search_recycler_view_google);
        googleSearch.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        SearchGoogleResultAdapter google = new SearchGoogleResultAdapter(MainActivity.this, list, MainActivity.this);
        googleSearch.setAdapter(google);
    }

    public void setLocalSearchResultAdapter(String kw){
        localSearch = searchView.findViewById(R.id.search_recycler_view_local);
        localSearch.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        if(!kw.equals("")){
            List<tLocation> list = locationDAO.search(kw);
            SearchLocalResultAdapter local = new SearchLocalResultAdapter(MainActivity.this, list, currentLocation, kw, this);
            localSearch.setAdapter(local);
        }
    }
    //Button Clicking Event
    private View.OnClickListener fab_startNavigatingClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavigationLauncherOptions opt = NavigationLauncherOptions.builder().directionsRoute(currentRoute).shouldSimulateRoute(true).build();
            NavigationLauncher.startNavigation(MainActivity.this, opt);
        }
    };

    private Dialog currentShowedDialog;
    public void DisableFabStartNavigation(){
        fab_startNavigating.setEnabled(false);
        TransitionUtil.fabHalfFadeOut(fab_startNavigating);
    }
    public void dismissDialogAndFlyToLocation(Location temp){
        if(currentShowedDialog.isShowing()) currentShowedDialog.dismiss();
        MapboxUtil.flyToLocation(temp.getLatitude(), temp.getLongitude(), mapboxMap);
    }
    public void dismissDialogAndDisableFabDirectorList(){
        if(currentShowedDialog.isShowing()) currentShowedDialog.dismiss();
        fab_directionList.setEnabled(false);
        TransitionUtil.fabHalfFadeOut(fab_directionList);
    }
    private  View.OnClickListener fab_directionListClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(listNavStop.size()>0){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.list_stop));
                alertDialogBuilder.setPositiveButton(getString(R.string.close),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                WayStopAdapter wayStopAdapter = new WayStopAdapter(MainActivity.this, R.layout.listitem_waystop, listNavStop);
                LayoutInflater  inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.listview_choose_location_to_override, null);
                ListView listView = (ListView) view.findViewById(R.id.lv_choose_location_to_override);
                listView.setAdapter(wayStopAdapter);
                alertDialogBuilder.setView(view);

                final AlertDialog dialog = alertDialogBuilder.create();
                currentShowedDialog = dialog;
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    AlertDialog d = dialog;
                    @Override
                    public void onShow(DialogInterface dia) {
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                    }
                });
                dialog.show();
            }
        }
    };

    public void refreshRoute(){
        if(navigationMapRoute != null){
            navigationMapRoute.removeRoute();
        }
        if(listNavStop.size()>1){
            getRoute(listNavStop);
        }
    }

    private View.OnClickListener fab_directorCancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listNavStop = null;
            fab_addStop.setEnabled(false);
            fab_directionList.setEnabled(false);
            fab_startNavigating.setEnabled(false);
            TransitionUtil.fabHalfFadeOut(fab_addStop);
            TransitionUtil.fabHalfFadeOut(fab_directionList);
            TransitionUtil.fabHalfFadeOut(fab_startNavigating);
            if(desMarker != null){
                mapboxMap.removeMarker(desMarker);
            }
            if(navigationMapRoute!=null){
                navigationMapRoute.removeRoute();
            }
            f_isNavigationMode = false;
            TransitionUtil.navigationModeFabSlideOut(fab_directorCancel,
                    fab_directionList, fab_addStop, fab_startNavigating);
        }
    };

    private View.OnClickListener fab_addStopClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(listNavStop.size() > 3){
                Toast.makeText(MainActivity.this, getString(R.string.add_to_stop_list_fail), Toast.LENGTH_LONG).show();
            }else{
                listNavStop.add(currentMapClickLocation);
                Toast.makeText(MainActivity.this, getString(R.string.add_to_stop_list), Toast.LENGTH_LONG).show();
            }
            if(!fab_directionList.isEnabled() && listNavStop.size() > 0) {
                fab_directionList.setEnabled(true);
                TransitionUtil.fabHalfFadeIn(fab_directionList);
            }
            fab_addStop.setEnabled(false);
            TransitionUtil.fabHalfFadeOut(fab_addStop);

            if(listNavStop != null && listNavStop.size() > 1) getRoute(listNavStop);
        }
    };

    private View.OnClickListener fab_saveLocationClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nearLocations = new ArrayList<tLocation>();
            currentLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
            for(tLocation i: savedLocations){
                double dis = i.getLocation().distanceTo(currentLocation);
                if(dis < 50){
                    nearLocations.add(i);
                }
            }
            if (TransitionUtil.isAtLeastLollipop()) {
                if(nearLocations.size() == 0){
                    startRippleTransitionRevealSaveLocation(fab_LocationX, fab_LocationY, fab_LocationWidth, 0, currentLocation.getLatitude(), currentLocation.getLongitude(), false);
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle(getString(R.string.dialog_location_nearby_title));
                    alertDialogBuilder.setMessage(getString(R.string.dialog_location_nearby_message)).setCancelable(false);
                    alertDialogBuilder.setPositiveButton(getString(R.string.dialog_location_nearby_pass_button),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startRippleTransitionRevealSaveLocation(fab_LocationX, fab_LocationY, fab_LocationWidth, 0,currentLocation.getLatitude(), currentLocation.getLongitude(), false);
                                }
                            });
                    alertDialogBuilder.setNeutralButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    dialog.cancel();
                                }
                            });
                    final tLocation2OverrideAdapter adapter = new tLocation2OverrideAdapter(MainActivity.this, R.layout.listitem_location_to_override_adapter, nearLocations, currentLocation);
                    LayoutInflater  inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = inflater.inflate(R.layout.listview_choose_location_to_override, null);
                    ListView listNearLocation = (ListView) view.findViewById(R.id.lv_choose_location_to_override);

                    listNearLocation.setAdapter(adapter);
                    alertDialogBuilder.setView(view);

                    final AlertDialog dialog = alertDialogBuilder.create();

                    listNearLocation.setOnItemClickListener(new ListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            tLocation temp = nearLocations.get(position);
                            dialog.dismiss();
                            startRippleTransitionRevealSaveLocation(fab_LocationX, fab_LocationY, fab_LocationWidth, temp.getId() ,temp.getLocation().getLatitude(), temp.getLocation().getLongitude(), true);
                        }
                    });
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        AlertDialog d = dialog;
                        @Override
                        public void onShow(DialogInterface dia) {
                            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                        }
                    });

                    dialog.show();
                }
            } else { askIfNearSavedLocationNoAnimate();}
        }
    };

    private View.OnClickListener fab_directorClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listNavStop = new ArrayList<>();
            f_isNavigationMode = true;
            TransitionUtil.navigationModeFabSlideIn(fab_directorCancel,
                    fab_directionList, fab_addStop, fab_startNavigating);
        }
    };

    private View.OnClickListener fab_reLocationClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(f_isEnableLocationComponent){
                checkAndShowCurrentLocation();
            }else{
                enableLocationComponent(mapboxMap.getStyle());
            }
        }
    };

    //Animate for fab_saveLocation
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRippleTransitionRevealSaveLocation(double X, double Y, double width, final int id, final double latitude, final double longitude, final boolean isOldSaved) {
        fab_saveLocation.setVisibility(View.INVISIBLE);
        fab_reLocation.setVisibility(View.INVISIBLE);
        fab_director.setVisibility(View.INVISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal(rippleView,
                (int) X + (int) width / 2,
                (int) Y, (float) width / 2, TransitionUtil.getViewRadius(rippleView) * 2);
        rippleView.setVisibility(View.VISIBLE);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                toolbar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent i = new Intent(MainActivity.this, SaveLocationActivity.class);
                i.putExtra("longitude", longitude);
                i.putExtra("latitude", latitude);
                i.putExtra("oldSavedId", id);
                i.putExtra("isOldSaved", isOldSaved);
                ActivityCompat.startActivity(MainActivity.this,
                        i,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                launchedSaveLocationActivity = true;
                launchedActivity = true;
                rippleView.setVisibility(View.VISIBLE);
                mapboxMap.clear();
            }
        });
        animator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRippleTransitionUnrevealSaveLocation(double X, double Y, double width) {
        rippleView.setVisibility(View.VISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal(rippleView,
                (int) X + (int) width / 2,
                (int) Y, TransitionUtil.getViewRadius(rippleView) * 2, (int) width / 2);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab_saveLocation.startAnimation(TransitionUtil.fabFadeIn(fab_saveLocation));
                fab_reLocation.startAnimation(TransitionUtil.fabFadeIn(fab_reLocation));
                fab_director.startAnimation(TransitionUtil.fabFadeIn(fab_director));
                toolbar.startAnimation(TransitionUtil.toolbarFadeIn(toolbar));

                rippleView.setVisibility(View.INVISIBLE);
                displayAllSavedLocation();
                launchedSaveLocationActivity = false;
                launchedActivity = false;
            }
        });
        animator.start();
    }



    //End fab_saveLocation animation
    //fab_createNewMemory animation
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRippleTransitionRevealSaveMemory(FloatingActionButton fab_createNewMemory, final int locationId) {
        fab_saveLocation.setVisibility(View.INVISIBLE);
        fab_reLocation.setVisibility(View.INVISIBLE);
        fab_director.setVisibility(View.INVISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal(rippleView,
                (int) WindowUtil.getViewX(fab_createNewMemory) + (int) fab_createNewMemory.getWidth() / 2,
                (int) WindowUtil.getViewY(fab_createNewMemory), (float) fab_createNewMemory.getWidth() / 2, TransitionUtil.getViewRadius(rippleView) * 2);
        rippleView.setVisibility(View.VISIBLE);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                toolbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent i = new Intent(MainActivity.this, SaveMemoryActivity.class);
                i.putExtra("locationId", locationId);
                ActivityCompat.startActivity(MainActivity.this,
                        i,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                rippleView.setVisibility(View.VISIBLE);
                selectedMarkerHolder.setVisibility(View.INVISIBLE);
                launchedSaveMemoryActivity = true;
                launchedActivity = true;
            }
        });
        animator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRippleTransitionUnrevealSaveMemory(final FloatingActionButton fab_createNewMemory) {
        Animator animator = ViewAnimationUtils.createCircularReveal(rippleView,
                (int) WindowUtil.getViewX(fab_createNewMemory) + (int) fab_createNewMemory.getWidth() / 2,
                (int) WindowUtil.getViewY(fab_createNewMemory), TransitionUtil.getViewRadius(rippleView) * 2, (int) fab_createNewMemory.getWidth() / 2);
        rippleView.setVisibility(View.VISIBLE);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                selectedMarkerHolder.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab_saveLocation.startAnimation(TransitionUtil.fabFadeIn(fab_saveLocation));
                fab_reLocation.startAnimation(TransitionUtil.fabFadeIn(fab_reLocation));
                fab_director.startAnimation(TransitionUtil.fabFadeIn(fab_director));
                toolbar.startAnimation(TransitionUtil.toolbarFadeIn(toolbar));

                rippleView.setVisibility(View.INVISIBLE);
                launchedSaveMemoryActivity = false;
                launchedActivity = false;
            }
        });
        animator.start();
    }

    //end fab_createNewMemory animation

    //fab_seedetail animation
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRippleTransitionRevealSeeDetail(FloatingActionButton fab_seeDetail, final int locationId) {
        fab_saveLocation.setVisibility(View.INVISIBLE);
        fab_reLocation.setVisibility(View.INVISIBLE);
        fab_director.setVisibility(View.INVISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal(rippleView,
                (int) WindowUtil.getViewX(fab_seeDetail) + (int) fab_seeDetail.getWidth() / 2,
                (int) WindowUtil.getViewY(fab_seeDetail), (float) fab_seeDetail.getWidth() / 2, TransitionUtil.getViewRadius(rippleView) * 2);
        rippleView.setVisibility(View.VISIBLE);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                toolbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent i = new Intent(MainActivity.this, LocationDetailActivity.class);
                i.putExtra("locationId", locationId);
                ActivityCompat.startActivityForResult(MainActivity.this,
                        i,LAUNCH_SEEDETAIL_ACTIVITY,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                rippleView.setVisibility(View.VISIBLE);
                selectedMarkerHolder.setVisibility(View.INVISIBLE);
                f_isOpenLocationDetail = true;
                launchedActivity = true;
            }
        });
        animator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRippleTransitionUnrevealSeeDetail(final FloatingActionButton fab_seeDetail) {
        Animator animator = ViewAnimationUtils.createCircularReveal(rippleView,
                (int) WindowUtil.getViewX(fab_seeDetail) + (int) fab_seeDetail.getWidth() / 2,
                (int) WindowUtil.getViewY(fab_seeDetail), TransitionUtil.getViewRadius(rippleView) * 2, (int) fab_seeDetail.getWidth() / 2);
        rippleView.setVisibility(View.VISIBLE);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                selectedMarkerHolder.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab_saveLocation.startAnimation(TransitionUtil.fabFadeIn(fab_saveLocation));
                fab_reLocation.startAnimation(TransitionUtil.fabFadeIn(fab_reLocation));
                fab_director.startAnimation(TransitionUtil.fabFadeIn(fab_director));
                toolbar.startAnimation(TransitionUtil.toolbarFadeIn(toolbar));

                rippleView.setVisibility(View.INVISIBLE);
                f_isOpenLocationDetail = false;
                launchedActivity = false;
            }
        });
        animator.start();
    }

    //end fab_seedetail animation

    private void setupTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());
        }
    }

    //MapBox

    private void displayAllSavedLocation(){
        savedLocations = locationDAO.getAll();
        mapboxMap.clear();
        for(tLocation i : savedLocations){
            Marker marker = mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(i.getLocation().getLatitude(),i.getLocation().getLongitude()))
                    .snippet(getString(R.string.DD)+ DateTimeUtil.convertDateToString(i.getCreated_at()))
                    .title(i.getLocationName())
                    .icon(IconFactory.getInstance(MainActivity.this).fromResource(R.drawable.finish))
            );
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
            checkAndShowCurrentLocation();
            currentLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
            f_isEnableLocationComponent = true;
        } else {
            PermissionsManager permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void checkAndShowCurrentLocation(){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            currentLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
            showCurrentLocation();
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void showCurrentLocation(){
        MapboxUtil.flyToLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), mapboxMap);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        mapboxMap.addOnMapClickListener(this);
        displayAllSavedLocation();
        mapboxMap.setStyle(Style.MAPBOX_STREETS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        //Get current locaion
                        enableLocationComponent(style);
                    }
                });
        mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull final Marker marker) {
                Location location = new Location("Marker");
                location.setLatitude(marker.getPosition().getLatitude());
                location.setLongitude(marker.getPosition().getLongitude());
                LinearLayout parent = new LinearLayout(MainActivity.this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.VERTICAL);
                LayoutInflater  inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.card_info_window, null);
                TextView locationName = (TextView)view.findViewById(R.id.tv_location_name);
                TextView dd = (TextView)view.findViewById(R.id.tv_discovery_date);

                final FloatingActionButton fab_seeDetail = (FloatingActionButton) view.findViewById(R.id.fab_location_detail);
                final FloatingActionButton fab_save_memory = (FloatingActionButton) view.findViewById(R.id.fab_save_memory);
                if(locationDAO.getByLocation(location) == null){
                    fab_seeDetail.setVisibility(View.GONE);
                    fab_save_memory.setVisibility(View.GONE);
                }
                fab_seeDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Location markerLocation = new Location("Marker location");
                        markerLocation.setLongitude(marker.getPosition().getLongitude());
                        markerLocation.setLatitude(marker.getPosition().getLatitude());

                        if (TransitionUtil.isAtLeastLollipop()) {
                            selectedMarkerHolder = marker.getInfoWindow().getView();
                            fab_seeDetailHolder = fab_seeDetail;
                            startRippleTransitionRevealSeeDetail(fab_seeDetail, locationDAO.getByLocation(markerLocation).getId());
                        }else{
                            Intent i = new Intent(MainActivity.this, LocationDetailActivity.class);
                            i.putExtra("locationId", locationDAO.getByLocation(markerLocation).getId());
                            startActivity(i);
                            selectedMarkerHolder = marker.getInfoWindow().getView();
                            f_isOpenLocationDetail = true;
                        }
                    }
                });

                fab_save_memory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Location markerLocation = new Location("Marker location");
                        markerLocation.setLongitude(marker.getPosition().getLongitude());
                        markerLocation.setLatitude(marker.getPosition().getLatitude());

                        if (TransitionUtil.isAtLeastLollipop()) {
                            selectedMarkerHolder = marker.getInfoWindow().getView();
                            fab_saveMemoryHolder = fab_save_memory;
                            startRippleTransitionRevealSaveMemory(fab_save_memory, locationDAO.getByLocation(markerLocation).getId());
                        }else{

                        }
                    }
                });

                locationName.setText(marker.getTitle());
                dd.setText(marker.getSnippet());
                parent.addView(view);
                return parent;
            }
        });
        fab_LocationX = WindowUtil.getViewX(fab_saveLocation);
        fab_LocationY = WindowUtil.getViewY(fab_saveLocation) + WindowUtil.getStatusBarHeight(MainActivity.this);
        fab_LocationWidth = fab_saveLocation.getWidth();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if(f_isNavigationMode){
            currentMapClickLocation = new Location("Clicked location");
            currentMapClickLocation.setLatitude(point.getLatitude());
            currentMapClickLocation.setLongitude(point.getLongitude());
            fab_addStop.setEnabled(true);
            TransitionUtil.fabHalfFadeIn(fab_addStop);

            if(desMarker!=null){
                mapboxMap.removeMarker(desMarker);
            }
            desMarker = mapboxMap.addMarker(new MarkerOptions().position(point).setTitle(getString(R.string.map_stop)).setSnippet(getString(R.string.latlng)+point.getLatitude()+", "+point.getLongitude()));
        }
        return true;
    }

    DirectionsRoute currentRoute;
    private void getRoute(List<Location> list){
        NavigationRoute.Builder routeBuilder  = NavigationRoute.builder(this)
                .accessToken(getString(R.string.access_token))
                .origin(Point.fromLngLat(list.get(0).getLongitude(), list.get(0).getLatitude()))
                .destination(Point.fromLngLat(list.get(list.size()-1).getLongitude(), list.get(list.size()-1).getLatitude()));
        for(int i = 1; i < list.size() -1 ; i++){
            routeBuilder.addWaypoint(Point.fromLngLat(list.get(i).getLongitude(), list.get(i).getLatitude()));
        }
        routeBuilder.build().getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body() == null){
                            Log.e("Tag", "No route found 1");
                            Log.e("Tag",response.toString());
                        }else if(response.body().routes().size() == 0){
                            Log.e("Tag", "No route found 2");
                        }else {
                            currentRoute = response.body().routes().get(0);
                            currentRoute.routeOptions();
                            if(navigationMapRoute != null){
                                navigationMapRoute.removeRoute();
                            }else{
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                            }
                            navigationMapRoute.addRoute(currentRoute);
                            fab_startNavigating.setEnabled(true);
                            TransitionUtil.fabHalfFadeIn(fab_startNavigating);
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e("Tag", "Fail");
                    }
                });
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        SystemUtil.hideLoading();
        if (rippleView.getVisibility() == View.VISIBLE && !launchedActivity) {
            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setAnimationListener(new Animation.AnimationListener() {@Override public void onAnimationStart(Animation animation) {} @Override public void onAnimationEnd(Animation animation) { rippleView.setVisibility(View.INVISIBLE); } @Override public void onAnimationRepeat(Animation animation) {} });
            anim.setDuration(250);
            rippleView.startAnimation(anim);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && launchedSaveLocationActivity && (rippleView.getVisibility() == View.VISIBLE)) {
            startRippleTransitionUnrevealSaveLocation(fab_LocationX, fab_LocationY, fab_LocationWidth);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && launchedSaveMemoryActivity && (rippleView.getVisibility() == View.VISIBLE)) {
            startRippleTransitionUnrevealSaveMemory(fab_saveMemoryHolder);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && f_isOpenLocationDetail && (rippleView.getVisibility() == View.VISIBLE)) {
            startRippleTransitionUnrevealSeeDetail(fab_seeDetailHolder);
        }
        if(f_isOpenListLocation){
            displayAllSavedLocation();
            f_isOpenListLocation = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    //End mapbox

    //Permission
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.location_turn_off)).setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.location_turn_off_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            AlertDialog d = dialog;
            @Override
            public void onShow(DialogInterface dia) {
                d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#403f3f"));
                d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4287f5"));
            }
        });

        dialog.show();
    }

    //Overriding
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //End Permission

    //Transition
    private Transition.TransitionListener searchHolderSlideOut = new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(@NonNull Transition transition) {
            searchRecyclerViewHolder.setBackground(null);
            WindowUtil.hideKeyboard(MainActivity.this, edt_search);
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            searchRecycleViewParams.addRule(RelativeLayout.BELOW, R.id.mapView);
            searchRecyclerViewHolder.setLayoutParams(searchRecycleViewParams);
            searchRecyclerViewHolder.removeAllViewsInLayout();
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {

        }
    };

    private Transition.TransitionListener searchHolderSlideIn = new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            searchRecyclerViewHolder.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rect_toolbar));
        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    };

    //Long func
    private void askIfNearSavedLocationNoAnimate(){
        if(nearLocations.size() == 0){
            Intent i = new Intent(MainActivity.this, SaveLocationActivity.class);
            i.putExtra("longitude", currentLocation.getLongitude());
            i.putExtra("latitude", currentLocation.getLatitude());
            i.putExtra("isOldSaved", false);
            ActivityCompat.startActivity(MainActivity.this,
                    i,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            launchedSaveLocationActivity = true;
            launchedActivity = true;
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle(R.string.dialog_location_nearby_title);
            alertDialogBuilder.setMessage(R.string.dialog_location_nearby_message).setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.dialog_location_nearby_pass_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MainActivity.this, SaveLocationActivity.class);
                            i.putExtra("longitude", currentLocation.getLongitude());
                            i.putExtra("latitude", currentLocation.getLatitude());
                            i.putExtra("isOldSaved", false);
                            ActivityCompat.startActivity(MainActivity.this,
                                    i,
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                            launchedSaveLocationActivity = true;
                            launchedActivity = true;
                        }
                    });
            alertDialogBuilder.setNeutralButton(R.string.cancel,
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                        }
                    });
            final tLocation2OverrideAdapter adapter = new tLocation2OverrideAdapter(MainActivity.this, R.layout.listitem_location_to_override_adapter, nearLocations, currentLocation);
            LayoutInflater  inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.listview_choose_location_to_override, null);
            ListView listNearLocation = (ListView) view.findViewById(R.id.lv_choose_location_to_override);

            listNearLocation.setAdapter(adapter);
            alertDialogBuilder.setView(view);

            final AlertDialog dialog = alertDialogBuilder.create();

            listNearLocation.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tLocation temp = nearLocations.get(position);
                    dialog.dismiss();
                    Intent i = new Intent(MainActivity.this, SaveLocationActivity.class);
                    i.putExtra("longitude", temp.getLocation().getLongitude());
                    i.putExtra("latitude", temp.getLocation().getLatitude());
                    i.putExtra("oldSavedId", temp.getId());
                    i.putExtra("isOldSaved", true);
                    ActivityCompat.startActivity(MainActivity.this,
                            i,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    launchedSaveLocationActivity = true;
                    launchedActivity = true;
                }
            });
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                AlertDialog d = dialog;
                @Override
                public void onShow(DialogInterface dia) {
                    d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                }
            });

            dialog.show();
        }
    }
}
