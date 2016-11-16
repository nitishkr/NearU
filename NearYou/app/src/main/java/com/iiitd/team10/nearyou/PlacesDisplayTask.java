package com.iiitd.team10.nearyou;

/**
 * Created by nitishkumar on 02/11/15.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    String type;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
           // type = (String)inputObj[2];
            //Log.v ("ATM type",type);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }
     String change(String type)
     {
         if(type.equals("bus_station"))
             return("bus");
         else if (type.equals("movie_theater"))
             return("cinema");
         else if(type.equals("subway_station"))
             return("metro");
         else if(type.equals("police"))
             return("Police Station");
         else if(type.equals("train_station"))
             return("train");
         else if(type.equals("shopping_mall"))
             return("airport");
         else
             return(type);
     }
    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        googleMap.clear();
      int x =0 ;
        String t = change(ShowMapActivity.type);
        t = t.toLowerCase().trim();
        if (t == "restaurant")
            x=  R.drawable.restraunt;
         else if (t == "atm")
            x = R.drawable.atm;
        else if (t == "airport")
            x = R.drawable.airport;
        else if (t == "bus")
            x=R.drawable.bus;
        else if (t == "hospital" || t == "pharmacy" )
            x=R.drawable.hospital;
        else if (t == "metro")
            x=R.drawable.metro;
        else if (t == "university")
            x=R.drawable.university;
        else if (t == "cinema")
            x=R.drawable.cinema;
        else if (t == "train")
            x=R.drawable.railway;
        else
            x=R.drawable.police;
        Log.v("marker Type", t);


        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
           markerOptions.icon(BitmapDescriptorFactory.fromResource(x));
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.snippet(vicinity);
            markerOptions.flat(true);
            googleMap.addMarker(markerOptions);
        }
    }
}