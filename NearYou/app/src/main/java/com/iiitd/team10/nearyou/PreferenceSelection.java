package com.iiitd.team10.nearyou;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
public class PreferenceSelection extends Activity {

    DBHelper dbHelper;
    DBHelper1 dbHelper1;
    MyCustomAdapter dataAdapter = null;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<Integer>placeStatus= new ArrayList<>();
    boolean atleastOneSelect=false;
    int counter=0;
    Button button;
    TourGuide mTourGuideHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        boolean firstboot = getSharedPreferences("BOOT_PREF", MODE_PRIVATE).getBoolean("firstboot", true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_selection);

        ToolTip toolTip = new ToolTip().setDescription("Select prefernces from list and click to save").
                setGravity(Gravity.TOP | Gravity.RIGHT).
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


        dbHelper = new DBHelper(this);
        dbHelper1 = new DBHelper1(this);

       // dbHelper.delete();
        if (dbHelper.check() == false) {
            dbHelper.insertPlace("Atm", 0);          //atm
            dbHelper.insertPlace("Bus Station", 0);     //bus_station
            dbHelper.insertPlace("Cinema Hall", 0);   //movie_theater
            dbHelper.insertPlace("Hospital", 0);     //hospital
            dbHelper.insertPlace("Metro Station", 0); //subway_station
            dbHelper.insertPlace("Pharmacy", 0);   //pharmacy
            dbHelper.insertPlace("Police Station", 0);   //police
            dbHelper.insertPlace("Railway Station", 0);  //train_station
            dbHelper.insertPlace("Restaurant", 0);       //restaurant
            dbHelper.insertPlace("Shopping Mall", 0);  //shopping_mall
        }
        places = dbHelper.getAllPlaces();
        placeStatus=dbHelper.getAllPlacesStatus();

        button = (Button)findViewById(R.id.signout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);

                startActivity(intent);
            }
        });

        //Generate list View from ArrayList
        displayListView();
        checkButtonClick();

        Button reset = (Button) findViewById(R.id.resetSelected);
        reset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> places = dataAdapter.places;
                for (int i = 0; i < places.size(); i++) {
                    placeStatus.set(i, 0);
                    dbHelper.updateStatus(places.get(i), 0);
                }
                displayListView();
                Toast.makeText(getApplicationContext(),
                        "Cleared", Toast.LENGTH_LONG).show();
                dbHelper1.updateStatusValue("status", 0);
            }
        });

        Button mybutton = (Button)findViewById(R.id.findSelected);
        if(firstboot) {
            mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(toolTip)
                    .setOverlay(overlay)
                    .playOn(mybutton);
            getSharedPreferences("BOOT_PREF", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstboot", false)
                    .commit();
        }
    }

    private void displayListView() {
        //create an ArrayAdapter from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.place_info, places);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> places;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> places) {
            super(context, textViewResourceId, places);
            this.places = new ArrayList<String>();
            this.places.addAll(places);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.place_info, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        if(cb.isChecked())
                        {
                            placeStatus.set(position,1);
                            dbHelper.updateStatus(places.get(position),1);
                        }
                        else
                        {
                            placeStatus.set(position,0);
                            dbHelper.updateStatus(places.get(position),0);

                        }
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(places.get(position));
            if(placeStatus.get(position) == 1)
              holder.name.setChecked(true);
            if(placeStatus.get(position) == 0)
                holder.name.setChecked(false);

            return convertView;
        }
    }

    private void checkButtonClick() {
        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

//                StringBuffer responseText = new StringBuffer();
  //              responseText.append("The following were selected...\n");

                ArrayList<String> places = dataAdapter.places;
                for(int i = 0; i < places.size(); i++){
                    if(placeStatus.get(i) == 1){
    //                    responseText.append("\n" + places.get(i));
                        atleastOneSelect=true;
                       counter++;
                    }
                }

                if(atleastOneSelect == false)
                {
                    Toast.makeText(PreferenceSelection.this, "Please select at least one preference item to proceed..", Toast.LENGTH_SHORT).show();
                }
                else if(counter>4)
                {
                    Toast.makeText(PreferenceSelection.this, "You can select atmost four preference..", Toast.LENGTH_SHORT).show();
                    counter=0;
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(),ShowAll.class);
                    startActivity(intent);
                    atleastOneSelect=false;
                    counter=0;
                }

            }
        });

        dbHelper1.updateStatusValue("status", 1);
    }
}
