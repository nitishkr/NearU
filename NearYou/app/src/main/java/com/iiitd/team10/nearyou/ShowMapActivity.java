package com.iiitd.team10.nearyou;

/**
 * Created by nitishkumar on 02/11/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class ShowMapActivity extends FragmentActivity  implements GoogleMap.OnMarkerClickListener ,GoogleMap.OnMapClickListener{
    private static final String GOOGLE_API_KEY = "AIzaSyA3vRJ35MMDDt5HWz2ds2NnPdXlQYDhD1c";
    GoogleMap googleMap;
    static String type;
    ImageButton imageButton,share;
    EditText placeText;
    double latitude = 0;
    double longitude = 0;
    double dlat = 0;
    double dlong=0;
    String dname="",dadd="";
    int flag = 0;
    private int PROXIMITY_RADIUS = 3000;
    TourGuide mTourGuideHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map_values);
        boolean firstboot1 = getSharedPreferences("BOOT_PREF", MODE_PRIVATE).getBoolean("firstboot1", true);
        ToolTip toolTip = new ToolTip().setDescription("Click on Share  Button to send Message to others." +
                "Click on Navigation Button to Open Google Navigation" +
                "Select any flag by clicking on it" +
                "Click on Uber Button to Book cab for destination").
                setGravity(Gravity.BOTTOM | Gravity.RIGHT).
                setBackgroundColor(Color.parseColor("#bdc3c7")).setShadow(true);


        Overlay overlay = new Overlay()
                .setBackgroundColor(Color.parseColor("#330000FF"))
                        // Note: disable click has no effect when setOnClickListener is used, this is here for demo purpose
                        // if setOnClickListener is not used, disableClick() will take effect
                .disableClick(false)
                .setStyle(Overlay.Style.Rectangle)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTourGuideHandler.cleanUp();
                    }
                });
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        Intent intent = getIntent();
        type= intent.getStringExtra("type").toLowerCase();
     //   placeText = (EditText) findViewById(R.id.placeText);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fragment.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restraunt));

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        addListenerOnButton();

        share();

        if(firstboot1) {
            mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(toolTip)
                    .setOverlay(overlay)
                    .playOn(share);
            getSharedPreferences("BOOT_PREF", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstboot1", false)
                    .commit();
        }

    }

    public boolean checkNetworkStates()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected==true)
            return true;
        else
            return false;
    }

    private void showAlert(String message, String title, int n)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert =builder.create();
        alert.setTitle(title);
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        alert.show();
        new CountDownTimer(n,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                alert.dismiss();
            }
        }.start();
    }

    public void share()
    {

        share = (ImageButton) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                //i.setType("message/rfc822");
                i.setType("text/plain");
                //i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"naveen15038@iiitd.ac.in"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Travel Destination Info");
                i.putExtra(Intent.EXTRA_TEXT, "I am travelling to " + dname + ", " + dadd + ". Let's catch up !!");
                try {
                    startActivity(Intent.createChooser(i, "Share location using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no client installed for such query", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void addListenerOnButton() {

         imageButton = (ImageButton) findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                PackageManager pm = getPackageManager();
                try {
                    pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                    String uri = null;
                    try {
                        uri = "uber://?action=setPickup&product_id=91901472-f30d-4614-8ba7-9fcc937cebf5&pickup[latitude]="+latitude+"&pickup[longitude]="+longitude+"&dropoff[latitude]="+String.valueOf(dlat)+"&dropoff[longitude]="+String.valueOf(dlong)+"&dropoff[nickname]="+ URLEncoder.encode(dname, "UTF-8");
                        Log.v("uber uri",uri);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    ;

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                    // Launch App.
                } catch (PackageManager.NameNotFoundException e) {
                // No Uber app! Open Mobile Website.
                    String url = "https://m.uber.com/sign-up";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

            }

        });

    }

    String get_metric(String x) {

        if(x.equals("bus station"))
            return("bus_station");
        else if(x.equals("cinema hall"))
            return("movie_theater");
        else if(x.equals("metro station"))
            return("subway_station");
        else if(x.equals("police station"))
            return("police");
        else if(x.equals("railway station"))
            return("train_station");
        else if(x.equals("shopping mall"))
            return("shopping_mall");
        else
            return(x);

    }

    public void showoptions() {
        type = get_metric(type);
        type = type.toLowerCase();
        Log.v("Nitish Type", type);
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        Log.v("NetworkRequest", String.valueOf(googlePlacesUrl));

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
      //  toPass[2] = type;
        googlePlacesReadTask.execute(toPass);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

            if (googleMap != null && flag ==0) {
                flag = 1;
                showoptions();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            }
        }
    };
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        LatLng l = marker.getPosition();
        dlat = l.latitude;
        dlong = l.longitude;
        dname = marker.getTitle();
        dadd =  marker.getSnippet();
        Toast.makeText(getApplicationContext(),dname, Toast.LENGTH_LONG).show();
        imageButton.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        imageButton.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
    }

/*
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }*/
}