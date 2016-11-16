package com.iiitd.team10.nearyou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class ShowAll extends Activity implements LocationListener {

    private final String TAG = "NearYou";
    DBHelper dbHelper = new DBHelper(this);
    private ArrayList<String> places;
    private ArrayList<PlaceData> placeDataList;
    private ArrayList<Map<PlaceItem, Integer>> itemList;
    private Button button;
    //private TextView textView;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
//    private StringBuffer buffer;
MyCustomAdapter dataAdapter = null;
    ArrayList<String> values;
    ArrayList<String> distance;
    ArrayList<String> address;
    ArrayList<String>dest_lat;
    ArrayList<String>dest_lng;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location mBestReading;
    private double my_latitude;
    private double my_longitude;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
    private static final float MIN_ACCURACY = 25.0f;
    private static final long TEN_SECONDS = 1000 * 10;
    TourGuide mTourGuideHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        boolean firstboot2 = getSharedPreferences("BOOT_PREF", MODE_PRIVATE).getBoolean("firstboot2", true);
      //  list = (ListView) findViewById(R.id.listView1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);


        ToolTip toolTip = new ToolTip().setDescription("Click on logout "+"Click on Setting Button to change Prefernces." +
                "Click on Map Button to Open Map for all nearby places" +
                "Click on Item-Name to display full list of nearby Places" +
                "Click on Uber Button to Book cab for destination").
                setGravity(Gravity.BOTTOM | Gravity.LEFT).
                setBackgroundColor(Color.parseColor("#bdc3c7")).setShadow(true);


        Overlay overlay = new Overlay()
                .setBackgroundColor(Color.parseColor("#330000FF"))
                        // Note: disable click has no effect when setOnClickListener is used, this is here for demo purpose
                        // if setOnClickListener is not used, disableClick() will take effect
                .disableClick(false)
                .setStyle(Overlay.Style.Circle)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTourGuideHandler.cleanUp();
                    }
                });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, TEN_SECONDS);
        if (null != mBestReading)
        {
            my_latitude = mBestReading.getLatitude();
            my_longitude = mBestReading.getLongitude();
        }
        else
        {
            my_latitude = 28.5439467;
            my_longitude = 77.2724376;
        }

        //Toast.makeText(ShowAll.this, String.valueOf(my_latitude), Toast.LENGTH_LONG).show();
        //Toast.makeText(ShowAll.this, String.valueOf(my_longitude), Toast.LENGTH_LONG).show();

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (null == mBestReading || location.getAccuracy() < mBestReading.getAccuracy())
                {
                    mBestReading = location;
                    my_latitude = mBestReading.getLatitude();
                    my_longitude = mBestReading.getLongitude();

                    //Toast.makeText(ShowAll.this, String.valueOf(my_latitude), Toast.LENGTH_LONG).show();
                    //Toast.makeText(ShowAll.this, String.valueOf(my_longitude), Toast.LENGTH_LONG).show();

                    if (mBestReading.getAccuracy() < MIN_ACCURACY) {
                        try {
                            locationManager.removeUpdates(locationListener);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {

            }
        };

        places = new ArrayList<String>();
        placeDataList = new ArrayList<PlaceData>();
  //      buffer = new StringBuffer();
        StringBuffer responseText = new StringBuffer();

        button = (Button)findViewById(R.id.prefbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreferenceSelection.class);
                intent.putExtra("value",true);
                startActivity(intent);
            }
        });

        button = (Button)findViewById(R.id.signout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);

                startActivity(intent);
            }
        });

         ArrayList<String> local=new ArrayList<String>();
        ArrayList<String> temp=new ArrayList<>();
        local=dbHelper.getAllSelectdPlaces();

        for(int i=0;i<local.size();i++)
        {
            if(local.get(i).equals("Bus Station"))
              temp.add("bus_station");
            else if(local.get(i).equals("Cinema Hall"))
                temp.add("movie_theater");
            else if(local.get(i).equals("Metro Station"))
                temp.add("subway_station");
            else if(local.get(i).equals("Police Station"))
                temp.add("police");
            else if(local.get(i).equals("Railway Station"))
                temp.add("train_station");
            else if(local.get(i).equals("Shopping Mall"))
                temp.add("shopping_mall");
            else
                temp.add(local.get(i));

        }
            places=temp;
     //   places = dbHelper.getAllSelectdPlaces();
        responseText.append("The following items were selected in the Preference Selection page...\n");
        for (int i = 0; i < places.size(); i++) {
            responseText.append("\n" + places.get(i));
        }
        Log.d(TAG, responseText.toString());

        for (int i = 0; i < places.size(); i++) {
            placeDataList.add(i, new PlaceData(places.get(i).toLowerCase(), my_latitude, my_longitude));
        }

        itemList = new ArrayList<Map<PlaceItem, Integer>>();
        Toast.makeText(ShowAll.this, "placeDataList Length: " + String.valueOf(placeDataList.size()), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < placeDataList.size(); i++) {
            itemList.add(i, placeDataList.get(i).getplacelatlang(3000));
            //buffer.append("\n" + response);
        }
        values = new ArrayList();
        distance=new ArrayList();
        address=new ArrayList();
        dest_lat=new ArrayList();
        dest_lng=new ArrayList();
    //    buffer.delete(0, buffer.length());
        for (int i = 0; i < placeDataList.size(); i++)
        {
            Map<PlaceItem, Integer> obj = itemList.get(i);
            int counter = 0;
            for (Map.Entry<PlaceItem, Integer> entry : obj.entrySet())
            {
                if(counter > 0)
                    break;
                else
                {
                    String name = PlaceData.getName(entry.getKey().getType(), entry.getKey().getLatitude(), entry.getKey().getLongitude());
                    entry.getKey().setName(name);
                    String temptype=null;
                    if(entry.getKey().getType().equals("bus_station"))
                        temptype="Bus Station";
                    else if (entry.getKey().getType().equals("movie_theater"))
                        temptype="Cinema Hall";
                    else if(entry.getKey().getType().equals("subway_station"))
                        temptype="Metro Station";
                    else if(entry.getKey().getType().equals("police"))
                        temptype="Police Station";
                    else if(entry.getKey().getType().equals("train_station"))
                        temptype="Railway Station";
                    else if(entry.getKey().getType().equals("shopping_mall"))
                        temptype="Shopping Mall";
                    else
                        temptype=entry.getKey().getType();
                    values.add(temptype.toUpperCase());
                    if(name.equals("")||name==null)
                        values.add(temptype);
                    else
                      values.add(name);
                    distance.add(entry.getKey().getDistance_Text());
                    address.add("\n"+entry.getKey().getAddress());
                    dest_lat.add(String.valueOf(entry.getKey().getLatitude()));
                    dest_lng.add(String.valueOf(entry.getKey().getLongitude()));
//                    buffer.append(entry.getKey().getName() + " : " + entry.getKey().getDistance_Text() + "\n");
                }
                counter++;
            }
        }

      //  Log.d(TAG, buffer.toString());
        //textView.setText(buffer.toString());


        /*Intent intent = new Intent(this, ShowSelectedResourceList.class);
        startActivity(intent);*//*
        getplacelatlang(radius);
        getplacedistance(lat, lng);
        textView.setText(buffer.toString());*/

        displayListView();
        if(firstboot2) {
            mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(toolTip)
                    .setOverlay(overlay)
                    .playOn(button);
            getSharedPreferences("BOOT_PREF", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstboot2", false)
                    .commit();
        }

    }

    // Unregister location listeners
    @Override
    protected void onPause() {
        super.onPause();

        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    // Get the last known location from all providers
    // return best reading that is as accurate as minAccuracy and
    // was taken no longer then minAge milliseconds ago. If none,
    // return null.
    private Location bestLastKnownLocation(float minAccuracy, long maxAge)
    {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestAge = Long.MIN_VALUE;
        List<String> matchingProviders = locationManager.getAllProviders();

        for (String provider : matchingProviders)
        {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null)
                {
                    float accuracy = location.getAccuracy();
                    long time = location.getTime();

                    if (accuracy < bestAccuracy)
                    {
                        bestResult = location;
                        bestAccuracy = accuracy;
                        bestAge = time;
                    }
                }
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy
                || (System.currentTimeMillis() - bestAge) > maxAge) {
            return null;
        }
        else {
            return bestResult;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void displayListView() {

        dataAdapter = new MyCustomAdapter(this,R.layout.showall_name, values);
        ListView listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(dataAdapter);

/*
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                String places = (String) parent.getItemAtPosition(position);

                if(placeStatus.get(position)==0)
                    placeStatus.set(position,1);
                if(placeStatus.get(position)==1)
                    placeStatus.set(position,0);
              Toast.makeText(getApplicationContext(),
                        "You select: " + places,
                        Toast.LENGTH_LONG).show();
            }
        });*/

    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> values;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> places) {
            super(context, textViewResourceId, places);
            this.values = new ArrayList<String>();
            this.values.addAll(places);
        }

        private class ViewHolder {
            TextView name;
            TextView dist;
            TextView typeof;
            TextView addr;
            Button btn;
            Button share;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.showall_name, null);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.textView1);
                holder.dist= (TextView) convertView.findViewById(R.id.textView2);
                holder.typeof=(TextView) convertView.findViewById(R.id.textView3);
                holder.addr=(TextView)convertView.findViewById(R.id.textView4);
                holder.btn=(Button)convertView.findViewById(R.id.btn1);
                holder.share=(Button)convertView.findViewById(R.id.btn2);
                if(position%2==0)
                {

                    holder.typeof.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.typeof.setGravity(Gravity.CENTER);
                    holder.btn.setVisibility(View.GONE);
                    holder.share.setVisibility(View.VISIBLE);
                }

                else
                {
                    holder.name.setTextSize(20);
                    holder.dist.setTextSize(20);
                    holder.name.setTextColor(Color.BLACK);
                    holder.dist.setTextColor(Color.BLACK);
                    holder.share.setVisibility(View.GONE);
                    holder.btn.setVisibility(View.VISIBLE);
                }

                convertView.setTag(holder);
                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        /*if (position % 2 == 0) {
                            Intent intent = new Intent(getApplicationContext(), ShowSelectedResourceList.class);
                            intent.putExtra("type", values.get(position));
                            startActivity(intent);
                        } else {
                       */
                        String latitude1 = String.valueOf(my_latitude);
                        String longitude1 = String.valueOf(my_longitude);
                        String latitude2 = String.valueOf(dest_lat.get(position / 2));
                        String longitude2 = String.valueOf(dest_lng.get(position / 2));

                        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + latitude1 + "," + longitude1 + "&daddr=" + latitude2 + "," + longitude2;
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                        //}
                    }
                });



                holder.share.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (position % 2 == 0) {
                            Intent intent = new Intent(getApplicationContext(), ShowMapActivity.class);
                            intent.putExtra("type", values.get(position));
                            Log.v("Type Patidar", values.get(position));
                            startActivity(intent);
                        }
                    }
                });


                holder.btn.setOnClickListener(new View.OnClickListener() {

                    String latitude1 = String.valueOf(my_latitude);
                    String longitude1 = String.valueOf(my_longitude);
                    String latitude2 = String.valueOf(dest_lat.get(position / 2));
                    String longitude2 = String.valueOf(dest_lng.get(position / 2));
                    String dname=values.get(position);

                    public void onClick(View v) {

                        if (position % 2 != 0) {
                            PackageManager pm = getPackageManager();
                            try {
                                pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                                String uri = null;
                                try {
                                    uri = "uber://?action=setPickup&product_id=91901472-f30d-4614-8ba7-9fcc937cebf5&pickup[latitude]="+latitude1+"&pickup[longitude]="+longitude1+"&dropoff[latitude]="+latitude2+"&dropoff[longitude]="+longitude2+"&dropoff[nickname]="+ URLEncoder.encode(dname, "UTF-8");
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
                    }
                });

                holder.addr.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {

                        if(position%2==0)
                        {
                            /*Intent intent=new Intent(getApplicationContext(), ShowSelectedResourceList.class);
                            intent.putExtra("type",values.get(position));
                            startActivity(intent);*/

                            Intent intent = new Intent(getApplicationContext(),ShowSelectedResourceList.class);
                            intent.putExtra("place_list", (Serializable) itemList.get(position/2));
                            intent.putExtra("lat",String.valueOf(my_latitude));
                            intent.putExtra("lng",String.valueOf(my_longitude));

                            startActivity(intent);
                        }
/*                        else
                        {
                            String latitude1 = String.valueOf(my_latitude);
                            String longitude1 = String.valueOf(my_longitude);
                            String latitude2 = String.valueOf(dest_lat.get(position/2));
                            String longitude2 = String.valueOf(dest_lng.get(position/2));

                            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+latitude1+","+longitude1+"&daddr="+latitude2+","+longitude2;
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        }*/
                    }
                });


            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }



            if(position%2!=0)
            {

                holder.dist.setText(distance.get(position/2)+"\n");
                holder.addr.setText(address.get(position / 2));
                holder.name.setText(values.get(position));
            }

            else
            {
                holder.addr.setTextSize(22);
                holder.addr.setTextColor(Color.RED);
                holder.addr.setText(values.get(position));
                holder.addr.setTypeface(Typeface.DEFAULT_BOLD);
            }
            return convertView;

        }

    }

}

