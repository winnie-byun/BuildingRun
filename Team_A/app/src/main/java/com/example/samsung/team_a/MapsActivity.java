package com.example.samsung.team_a;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.team_a.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    public static boolean mapback =false;
    Context context;
    //bluetooth from heartrate
    private int MAX_SIZE = 60; //graph max size
    boolean searchBt = true;
    BluetoothAdapter mBluetoothAdapter;
    List<BluetoothDevice> pairedDevices = new ArrayList<>();
    boolean menuBool = false; //display or not the disconnect option
    boolean h7 = false; //Was the BTLE tested
    boolean normal = false; //Was the BT tested
    private Spinner spinner1;
    //
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private PlaceInfo mPlace;
    private CameraPosition mCameraPosition;
    private HttpURLConnection conn;
    Marker selectedMarker;
    View marker_root_view;
    TextView tv_marker;
    ArrayList<HashMap<String, String>> contactList;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    public static String MAC = "";
    private String time="";
    private int testDate = 0;
    public static double CO = 0.0, NO2 = 0.0, temperature = 0.0, O3 = 0.0, SO2 = 0.0, PM25;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 18;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    public static Location mLastKnownLocation;
    private GoogleApiClient mGoogleApiClient;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mInfo;
    public static boolean TestFlag = false;
    private BluetoothAdapter bt_adapter = null;
    private BluetoothConnection bt_connection;
    private BluetoothDevice bt_device;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    Intent main_to_devicelist;
    Frag_realtime fr=new Frag_realtime();
    private String Realtime;

    Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            getandgodb();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mInfo = (ImageView) findViewById(R.id.place_info);
        bt_adapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothConnection.state =2;
        // DeviceListActivity로 정보 보내주려고 Intent 생성
        main_to_devicelist = new Intent(MapsActivity.this, DeviceListActivity.class);

        // If the adapter is null, then Bluetooth is not supported
        if (!bt_adapter.isEnabled()) {
            Intent bt_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bt_enable, REQUEST_ENABLE_BT);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fr_case, new Frag_realtime())
                .commit();
        BluetoothConnection.state = 2;
        startActivityForResult(main_to_devicelist, REQUEST_CONNECT_DEVICE_SECURE);


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        contactList = new ArrayList<>();
        //while(true){
        //sHandler.sendEmptyMessageDelayed(0, 3000);
        //}
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            ConnectDevice(data, true);
        }
    }

    private void ConnectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        // Get the BluetoothDevice object

            bt_device = bt_adapter.getRemoteDevice(address);
            // Attempt to connect to the device
            bt_connection = new BluetoothConnection(bt_device, bt_adapter, bt_receivemsg);
            bt_connection.start();
            MAC = bt_device.getAddress().toString();
            Toast.makeText(MapsActivity.this, "CONNECTED WITH " + bt_device.getAddress() + " " + bt_device.getName(), Toast.LENGTH_SHORT).show();
            BluetoothConnection.checkconnect = 1;

    }

    private Handler bt_receivemsg = new Handler() { // ★ 메세지
        @Override
        public void handleMessage(Message message) {
            Bundle data = message.getData();
            Realtime = data.getString("realtime");
            try {
                JSONArray jsonarray = new JSONArray(Realtime);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsondata = jsonarray.getJSONObject(i);
                    MAC = (String) jsondata.getString("MAC");
                    time = (String) jsondata.getString("TIME");
                    NO2 = (Double) jsondata.getDouble("NO2");
                    O3 = (Double) jsondata.getDouble("O3");
                    CO = (Double) jsondata.getDouble("CO");
                    SO2 = (Double) jsondata.getDouble("SO2");
                    PM25 = (Double) jsondata.getDouble("PM25");
                    temperature = (Double) jsondata.getDouble("TEMP");
                    String result = "connecting..";
                    Log.d("result",String.valueOf(NO2));
                    Log.d("result",String.valueOf(O3));
                    Log.d("result",String.valueOf(CO));
                    Log.d("result",String.valueOf(SO2));
                    Log.d("result",String.valueOf(PM25));
                    Log.d("result",String.valueOf(temperature));
                    fr.settxt(CO,NO2,O3,SO2,temperature,PM25,result);
                    Toast.makeText(getApplicationContext(),"MAC : "+MAC+" time : "+time+" NO2 : "+NO2+" O3 : "+O3
                            +" CO : "+CO+" SO2 : "+SO2+" PM25 : "+PM25+" temperature : "+temperature,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {

            }
        }
    };
    public void MapInfoOnclick(View v) {
        Toast.makeText(MapsActivity.this, "If you want real time data of " +
                "your sensor, Click bluetooth icon and connect with your sensor.", Toast.LENGTH_LONG).show();

    }

    public void bluetoothconnect(View v) {
        // Launch the DeviceListActivity to see devices and do scan
        BluetoothConnection.state = 2;
        startActivityForResult(main_to_devicelist, REQUEST_CONNECT_DEVICE_SECURE);

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void init() {
        Log.d(TAG, "init : initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mSearchText.setOnItemClickListener(mAutocompleteOnItemClickListener);
        //I don't know why there is error but maybe here. that's about API but key is not a problem shit i don't know

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,
                Places.getGeoDataClient(this, null), LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mapback = true;
        Log.d("back button","click");

    }

    private void geoLocate() {
        Log.d(TAG, "GeoLocate : geolocating");
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate ; IOExecption :" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate : found a location : " + address.toString());
            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        //mMap.setOnMarkerClickListener(this);
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), tab1AirQuality.class);
                        startActivity(i);
                    }
                });
                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        setCustomMarkerView();
        getSampleMarkerItems();
        init();
    }

    private void setCustomMarkerView() {

        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }


    private void getSampleMarkerItems() {
        ArrayList<MarkerItem> sampleList = new ArrayList();

        sampleList.add(new MarkerItem(32.887087, -117.244065, 1, "James", "EQ:2E:F1:Q2:R3:QE"));
        sampleList.add(new MarkerItem(32.887011, -117.242639, 15, "Wendy", "VQ:2E:F1:Q2:R3:1W"));
        sampleList.add(new MarkerItem(32.887208, -117.241683, 32, "Jack", "EE:VB:F2:Q2:R3:QE"));
        sampleList.add(new MarkerItem(32.881844, -117.239987, 6, "joowon", "AJ:2E:F1:Q2:R3:QE"));


        for (MarkerItem markerItem : sampleList) {
            addMarker(markerItem, false);
        }

    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {


        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        int ssn = markerItem.getssn();
        String address = markerItem.getaddress();
        String name = markerItem.getname();
        String formatted = NumberFormat.getCurrencyInstance().format((ssn));
        String label = address;
        tv_marker.setText(name);

        if (isSelectedMarker) {
            tv_marker.setBackgroundResource(R.mipmap.ic_marker_phone_blue);
            tv_marker.setTextColor(Color.WHITE);
        } else {
            tv_marker.setBackgroundResource(R.mipmap.ic_marker_phone);
            tv_marker.setTextColor(Color.BLACK);
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("SSN : " + Integer.toString(ssn) + "\nAddress : " + address + "\nMAC : " + MAC + "NO2 : " + NO2);
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));


        return mMap.addMarker(markerOptions);

    }

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    private Marker addMarker(Marker marker, boolean isSelectedMarker) {
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        int ssn = Integer.parseInt(marker.getTitle());
        String name = marker.getSnippet();
        String address = marker.getId();
        MarkerItem temp = new MarkerItem(lat, lon, ssn, name, address);
        return addMarker(temp, isSelectedMarker);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        mMap.animateCamera(center);
        changeSelectedMarker(marker);
        GetContacts contacts = new GetContacts();
        //contacts.execute();
        return true;
    }


    private void changeSelectedMarker(Marker marker) {
        // 선택했던 마커 되돌리기
        if (selectedMarker != null) {
            addMarker(selectedMarker, false);
            selectedMarker.remove();
        }

        // 선택한 마커 표시
        if (marker != null) {
            selectedMarker = addMarker(marker, true);
            marker.remove();
        }


    }


    @Override
    public void onMapClick(LatLng latLng) {
        changeSelectedMarker(null);
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                mMap.setMyLocationEnabled(true);
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            /*if (Frag_realtime.temperature >=15) {
                                Circle circle = mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                        .radius(10000)
                                        .strokeColor(Color.RED)
                                        .fillColor(Color.RED));
                                circle.isVisible();
                            }*/
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private AdapterView.OnItemClickListener mAutocompleteOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult : Place query did not complete Successfully :" + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId());
                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult : place details" + mPlace.toString());
            } catch (NullPointerException e) {
                Log.e(TAG, "onResult : NullPointerException: " + e.getMessage());
            }
            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), 15, mPlace.getName());
            places.release();
        }
    };

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera : moving the camera to : 1st : " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
    }

    public static int getandgodb() {
        int result = 0;
        Random rand = new Random();
        double a = 31 * rand.nextDouble();
        double b = 31 * rand.nextDouble();
        double c = 31 * rand.nextDouble();
        double d = 31 * rand.nextDouble();
        double e = 31 * rand.nextDouble();
        double Vtemp = 0.0, VNO2 = 0.0, VO3 = 0.0, VCO = 0.0, VSO2 = 0.0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String Vdate = sdf.format(new Date());
        JSONObject json = new JSONObject();
        try {
            json.put("temp", String.valueOf(a));
            json.put("NO2", String.valueOf(b));
            json.put("O3", String.valueOf(c));
            json.put("CO", String.valueOf(d));
            json.put("SO2", String.valueOf(e));
            json.put("date", Vdate);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        String body = json.toString();
        Log.d("JSON_body : ", body);
        String response;
        try {
            JSONObject responseJSON = new JSONObject(body);
            Vtemp = (Double) responseJSON.getDouble("temp");
            VNO2 = (Double) responseJSON.getDouble("NO2");
            VO3 = (Double) responseJSON.getDouble("O3");
            VCO = (Double) responseJSON.getDouble("CO");
            VSO2 = (Double) responseJSON.getDouble("SO2");
            Vdate = (String) responseJSON.getString("date");
            Frag_realtime.Ftemperature = Double.valueOf(String.format("%.2f", Vtemp));
            Frag_realtime.FNO2 = Double.valueOf(String.format("%.2f", VNO2));
            Frag_realtime.FO3 = Double.valueOf(String.format("%.2f", VO3));
            Frag_realtime.FCO = Double.valueOf(String.format("%.2f", VCO));
            Frag_realtime.FSO2 = Double.valueOf(String.format("%.2f", VSO2));
            Frag_realtime.date = Vdate;

            if (body.equals("")) {
                result = 0;
            } else {
                result = 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("JSON_2line:", "problem");
        }
        return result;
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MapsActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                JSONObject jsonObj = new JSONObject();

                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("contacts");
                contacts.put(BluetoothChatFragment.AirQuality);
                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    String VMAC = c.getString("MAC");

                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();

                       /* // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);
*/
                    // adding contact to contact list
                    contactList.add(contact);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
    class AirtoServer extends AsyncTask<String, Integer, Integer> {
        Context context;

        AirtoServer(Context etx) {
            context = etx;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... value) {
            int result = airtoserver();
            switch (result) {
                case 1:
                    publishProgress(1);
                    break;

                case 2:
                    publishProgress(2);

                    break;
                default:
//                    Toast.makeText(getApplicationContext(), "System Error/Connection Fail", Toast.LENGTH_SHORT).show();
                    break;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            AlertDialog alertdialog = new AlertDialog.Builder(context).create();
            if (value[0] == 1) {
                Toast.makeText(context,"Air quality transfer success",Toast.LENGTH_SHORT).show();
            } else if (value[0] == 2) {
                Toast.makeText(context,"Air quality transfer failed",Toast.LENGTH_SHORT).show();
            }
        }

        public int airtoserver() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            int result = 0;
            int usn;
            try {
                URL url = new URL("http://teama-iot.calit2.net/app/airQualityDataTransfer");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("USN", loginActivity.ST_usn);
                    json.put("TIME",currentDateandTime );
                    json.put("MAC",BluetoothConnection.MAC);
                    json.put("latitude",MapsActivity.mLastKnownLocation.getLatitude());
                    json.put("longitude",MapsActivity.mLastKnownLocation.getLongitude());
                    json.put("CO",BluetoothConnection.CO);
                    json.put("SO2",BluetoothConnection.SO2);
                    json.put("NO2",BluetoothConnection.NO2);
                    json.put("O3",BluetoothConnection.O3);
                    json.put("PM25",BluetoothConnection.PM25);
                    json.put("TEMP",BluetoothConnection.TEMP);


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                String body = json.toString();
                Log.d("JSON_body : ", body);
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes());
                    os.flush();
                    String response;
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;
                        while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();
                        response = new String(byteData);
                        Log.d("response",response);
                        JSONObject responseJSON = new JSONObject(response);
                        result = (Integer) responseJSON.getInt("Result");
                        Log.d("AIRQUAILITY RESULT: ",String.valueOf(result));
                        is.close();
                        os.close();
                        conn.disconnect();
                    }
                } else {
                    Log.d("JSON", "Connection fail");
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d("JSON_2line:", "problem");
            }
            return result;
        }

    }
}



