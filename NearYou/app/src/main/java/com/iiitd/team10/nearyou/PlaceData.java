package com.iiitd.team10.nearyou;

import android.app.Activity;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlaceData extends Activity
{
    private String type;
    private int radius;
    private double latitude_my_location;
    private double longitude_my_location;

    private double lat[];
    private double lng[];
    private int loopCount;
    private List<PlaceItem> list;
    private StringBuffer buffer;

    private String googlePlacesData;
    private JSONObject googlePlacesJson;
    private JSONArray jsonArray;
    private static String GOOGLE_API_KEY = "AIzaSyA3vRJ35MMDDt5HWz2ds2NnPdXlQYDhD1c";

    PlaceData(String place_type, double latitude, double longitude)
    {
        type = place_type;
        radius = 0;
        latitude_my_location = latitude; //28.5439467;
        longitude_my_location = longitude; //77.2724376;
    }

    public String getType() { return this.type; }

    private StringBuffer getBuffer() { return this.buffer; }

    public Map<PlaceItem, Integer> getplacelatlang(int r)
    {

        radius = r;
        System.out.println("Type: " + getType());
        String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location=" +
                String.valueOf(latitude_my_location) + "," + String.valueOf(longitude_my_location) + "&radius=" + radius +
                "&types="+getType()+"&sensor=falses&key=" + GOOGLE_API_KEY;
        System.out.println("GooglePlacesUrl: " + googlePlacesUrl);

        HTTP_FetchName http = new HTTP_FetchName();
        if(http != null)
        {
            try {
                loopCount = 0;
                googlePlacesData = http.read(googlePlacesUrl);
                googlePlacesJson = new JSONObject(googlePlacesData);
                jsonArray = googlePlacesJson.getJSONArray("results");
                System.out.println("Success with radius" + String.valueOf(radius) + ":" + String.valueOf(jsonArray.length()));

                int placesCount = jsonArray.length();
                if (placesCount > 50)
                    loopCount = 50;
                else
                    loopCount = placesCount;

                lat = new double[loopCount];
                lng = new double[loopCount];
                list = new ArrayList<PlaceItem>(loopCount);

                for (int i = 0; i < loopCount; i++) {
                    lat[i] = ((JSONObject) jsonArray.get(i)).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    lng[i] = ((JSONObject) jsonArray.get(i)).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    System.out.println("Latitude: " + String.valueOf(lat[i]) + " Longitude:" + String.valueOf(lng[i]));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        Map<PlaceItem, Integer> sortedDistanceList = getplacedistance();
        //return getBuffer().toString();
        return sortedDistanceList;
    }

    public Map<PlaceItem, Integer> getplacedistance()
    {



        Map<PlaceItem , Integer> requiredList = new HashMap<>();

        buffer = new StringBuffer();
        buffer.delete(0, buffer.length());

        StringBuffer url = new StringBuffer();
        url.delete(0, url.length());
        url.append("https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + String.valueOf(latitude_my_location) + "," + String.valueOf(longitude_my_location) + "&destinations=");
        for(int i = 0; i < loopCount; i++)
        {
            url.append(String.valueOf(lat[i])+","+String.valueOf(lng[i]));
            if(i!= loopCount -1)
            {
                url.append("|");
            }
        }
        url.append("&key=" + GOOGLE_API_KEY);
        System.out.println("URL:" + url.toString());

        HTTP_FetchName http = new HTTP_FetchName();
        if(http != null)
        {
            try {
                String googlePlacesData = http.read(url.toString());
                 Thread.sleep(200);
                System.out.println("googlePlacesData: " + googlePlacesData);
                JSONObject googlePlacesJson = new JSONObject(googlePlacesData);
                JSONArray addressArray = googlePlacesJson.getJSONArray("destination_addresses");
                JSONArray jsonArray = googlePlacesJson.getJSONArray("rows");
                JSONArray elementArray = jsonArray.getJSONObject(0).getJSONArray("elements");
                int placesCount = elementArray.length();

                Map<PlaceItem, Integer> distanceList = new HashMap<>();

                for (int i = 0; i < placesCount; i++)
                {
                    PlaceItem item = new PlaceItem(getType());
                    StringBuffer address = new StringBuffer();
                    address.delete(0, address.length());
                    address.append(addressArray.getString(i));
                    item.setAddress(addressArray.getString(i));

                    JSONObject obj = (JSONObject) elementArray.get(i);
                    int distance = obj.getJSONObject("distance").getInt("value");
                    item.setDistance(distance);

                    String distance_Text = obj.getJSONObject("distance").getString("text");
                    item.setDistance_Text(distance_Text);
                    item.setLatitude(lat[i]);
                    item.setLongitude(lng[i]);

                    System.out.println("Address:" + address.toString());

                    list.add(item);
                    distanceList.put(item, distance);
                }

                Map<PlaceItem, Integer> sortedDistanceList = sortByValues(distanceList);
                int counter = 0;
                for (Map.Entry<PlaceItem, Integer> entry : sortedDistanceList.entrySet())
                {
                    System.out.println("Counter " + counter + " : " + entry.getValue());
                    if(counter > 19)
                        break;
                    else
                    {
                        requiredList.put(entry.getKey(), entry.getValue());
                        buffer.append(entry.getValue() + " : " + entry.getKey().getAddress() + "\n");
                    }
                    counter++;
                }
                requiredList = sortByValues(requiredList);
            }
            catch (JSONException e) {
                Log.d("Show All Distance", e.toString());
                e.printStackTrace();
            }
            catch (Exception e) {
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Error in connecting to Google API..", Toast.LENGTH_SHORT).show();


        return requiredList;
    }

    public static Map<PlaceItem, Integer> sortByValues(Map<PlaceItem, Integer> unsortMap)
    {
        List<Map.Entry<PlaceItem, Integer>> list = new LinkedList<Map.Entry<PlaceItem, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<PlaceItem, Integer>>() {
            public int compare(Map.Entry<PlaceItem, Integer> oper1,
                               Map.Entry<PlaceItem, Integer> oper2) {

                return (oper1.getValue().compareTo(oper2.getValue()));
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<PlaceItem, Integer> sortedMap = new LinkedHashMap<PlaceItem, Integer>();
        for (Iterator<Map.Entry<PlaceItem, Integer>> iter = list.iterator(); iter.hasNext();)
        {
            Map.Entry<PlaceItem, Integer> entry = iter.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static String getName(String type, double latitude, double longitude)
    {
        StringBuffer getNameFromAPI = new StringBuffer();
        getNameFromAPI.delete(0, getNameFromAPI.length());
        String name = "";

        getNameFromAPI.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + String.valueOf(latitude) + "," + String.valueOf(longitude)
                + "&radius=3&types=" + type + "&sensor=true&key=" + GOOGLE_API_KEY);

        HTTP_FetchName con = new HTTP_FetchName();
       // Http con = new Http();
        if(con != null)
        {
            String searchName ;
            try
            {
                 searchName = con.read(getNameFromAPI.toString());
                JSONObject response = new JSONObject(searchName);
                JSONArray resultArray = response.getJSONArray("results");
                name = resultArray.getJSONObject(0).getString("name");
            }
            catch (Exception e)
            {
                name=type;
                e.printStackTrace();

            }
        }
        return name;
    }


}
