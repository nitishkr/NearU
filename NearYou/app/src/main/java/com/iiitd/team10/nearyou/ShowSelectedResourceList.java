package com.iiitd.team10.nearyou;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

public class ShowSelectedResourceList extends Activity{
    Button button,sbutton;
    private final String TAG = "NearYou";
    Map<PlaceItem, Integer> placeList;
    private ListView list;
    MyCustomAdapter dataAdapter = null;
    ArrayList<String> values;
    ArrayList<String> distance;
    ArrayList<String> address;
    ArrayList<String>dest_lat;
    ArrayList<String>dest_lng;
    String my_latitude;
    String my_longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_resource_list);

        values = new ArrayList();
        distance = new ArrayList();
        address = new ArrayList();
        dest_lat = new ArrayList();
        dest_lng = new ArrayList();

        Intent intent=getIntent();
        my_latitude=intent.getStringExtra("lat");
        my_longitude=intent.getStringExtra("lng");
        placeList=( Map<PlaceItem, Integer>)intent.getSerializableExtra("place_list");
        placeList = PlaceData.sortByValues(placeList);
        int counter=0;
        for (Map.Entry<PlaceItem, Integer> entry : placeList.entrySet())
        {
            if(counter > 5)
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

                if(counter == 0) {
                    values.add(temptype.toUpperCase());
                    distance.add("Nearyou");
                    address.add("Nearyou");
                    dest_lat.add("Nearyou");
                    dest_lng.add("Nearyou");
                }
                else {
                    if (name.equals("") || name == null)
                        values.add(temptype);
                    else
                        values.add(name);
                    distance.add(entry.getKey().getDistance_Text());
                    address.add("\n" + entry.getKey().getAddress());
                    dest_lat.add(String.valueOf(entry.getKey().getLatitude()));
                    dest_lng.add(String.valueOf(entry.getKey().getLongitude()));
                }
            }
            counter++;
        }
        button = (Button)findViewById(R.id.prefbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreferenceSelection.class);
                intent.putExtra("value",true);
                startActivity(intent);
            }
        });

        sbutton = (Button)findViewById(R.id.signout);
        sbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);

                startActivity(intent);
            }
        });
        displayListView();

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
            Button uber;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.showselectedvalues, null);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.textView1);
                holder.dist= (TextView) convertView.findViewById(R.id.textView2);
                holder.typeof=(TextView) convertView.findViewById(R.id.textView3);
                holder.addr=(TextView)convertView.findViewById(R.id.textView4);
                holder.uber=(Button)convertView.findViewById(R.id.uber);
                if(position==0)
                {
                    holder.addr.setTextSize(25);
                    holder.addr.setTextColor(Color.RED);
                    holder.addr.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.typeof.setGravity(Gravity.CENTER);
                    holder.uber.setVisibility(View.GONE);
                }

                else
                {
                    holder.name.setTextSize(20);
                    holder.dist.setTextSize(20);
                    holder.name.setTextColor(Color.BLACK);
                    holder.dist.setTextColor(Color.BLACK);
                    holder.uber.setVisibility(View.VISIBLE);
                }

                convertView.setTag(holder);
                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (position == 0) {
                            Intent intent = new Intent(getApplicationContext(), ShowMapActivity.class);
                            intent.putExtra("type", values.get(position));
                            startActivity(intent);
                        } else {
                            String latitude1 = String.valueOf(my_latitude);
                            String longitude1 = String.valueOf(my_longitude);
                            String latitude2 = String.valueOf(dest_lat.get(position));
                            String longitude2 = String.valueOf(dest_lng.get(position));

                            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + latitude1 + "," + longitude1 + "&daddr=" + latitude2 + "," + longitude2;
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        }
                    }
                });

                holder.uber.setOnClickListener(new View.OnClickListener() {

                    String latitude1 = String.valueOf(my_latitude);
                    String longitude1 = String.valueOf(my_longitude);
                    String latitude2 = String.valueOf(dest_lat.get(position));
                    String longitude2 = String.valueOf(dest_lng.get(position));
                    String dname=values.get(position);

                    public void onClick(View v) {

                        if (position!= 0) {
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

                        if(position==0)
                        {
                            Intent intent=new Intent(getApplicationContext(), ShowMapActivity.class);
                            intent.putExtra("type", values.get(position));
                            startActivity(intent);
                        }
/*                        else
                        {
                            String latitude1 = String.valueOf(my_latitude);
                            String longitude1 = String.valueOf(my_longitude);
                            String latitude2 = String.valueOf(dest_lat.get(position));
                            String longitude2 = String.valueOf(dest_lng.get(position));

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



            if(position!=0)
            {
                holder.dist.setText(distance.get(position)+"\n");
                holder.addr.setText(address.get(position));
                holder.name.setText(values.get(position));
            }

            else
            {
                holder.addr.setText(values.get(position));
            }
            return convertView;

        }

    }

}

