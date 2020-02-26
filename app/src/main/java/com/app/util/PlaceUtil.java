package com.app.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.app.R;
import com.app.dao.PlaceDAO;
import com.app.model.Place;
import com.app.view.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PlaceUtil {
    static String kw;
    public static Context context;
    static MainActivity activity;
    static ProgressDialog pd;
    static Place place;

    public static void getPlaceDetail(Context mContext, Place thePlace, MainActivity mainActivity) {
        place = thePlace;
        context = mContext;
        activity = mainActivity;
        try {
            new PlaceDetailTask().execute("https://maps.googleapis.com/maps/api/place/details/json?" +
                    "place_id=" + URLEncoder.encode(place.getPlace_id(), "UTF-8")+
                    "&fields=name,geometry,icon" +
                    "&key="+context.getString(R.string.google_api));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getSearchResult(Context mContext, String str, Location currentLocation, MainActivity mainActivity){
        context = mContext;
        activity = mainActivity;
        kw = str;
        try {
            new JsonTask().execute("https://maps.googleapis.com/maps/api/place/autocomplete/json?" +
                    "input=" + URLEncoder.encode(str, "UTF-8")+
                    "&origin="+currentLocation.getLatitude()+","+currentLocation.getLongitude()+
                    "&types=geocode&language=vi&key="+context.getString(R.string.google_api));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static class PlaceDetailTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage(context.getString(R.string.loading));
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            JSONObject jsonRs = null;
            try {
                jsonRs = new JSONObject(result);
                if(jsonRs.getString("status").equals("OK")){
                    JSONObject rs = jsonRs.getJSONObject("result");
                    place.setLatitude(rs.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    place.setLongitude(rs.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    place.setIcon(rs.getString("icon"));
                    activity.createMarkAndFlyToLocation(place);
                }else if(jsonRs.getString("status").equals("ZERO_RESULTS")){
                    Toast.makeText(context, context.getString(R.string.zero_result), Toast.LENGTH_LONG).show();
                    System.out.println(result);
                }else if(jsonRs.getString("status").equals("OVER_QUERY_LIMIT")){
                    Toast.makeText(context, context.getString(R.string.zero_result), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                   // Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            JSONObject jsonRs = null;
            List<Place> list = new ArrayList<Place>();
            try {
                jsonRs = new JSONObject(result);
                if(jsonRs.getString("status").equals("OK")){
                    JSONArray predictions = jsonRs.getJSONArray("predictions");
                    for(int i = 0; i < predictions.length(); i++){
                        JSONObject object = predictions.getJSONObject(i);
                        Place temp = new Place();
                        temp.setPlace_id(object.getString("place_id"));
                        temp.setAddress(object.getString("description"));
                        try{
                            temp.setDistance(object.getDouble("distance_meters"));
                        }catch (JSONException ex){
                            temp.setDistance(0);
                        }
                        temp.setName(object.getJSONObject("structured_formatting").getString("main_text"));
                        list.add(temp);
                    }
                }else if(jsonRs.getString("status").equals("ZERO_RESULTS")){
                    Toast.makeText(context, context.getString(R.string.zero_result), Toast.LENGTH_LONG).show();
                    System.out.println(result);
                }else if(jsonRs.getString("status").equals("OVER_QUERY_LIMIT")){
                    Toast.makeText(context, context.getString(R.string.zero_result), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                System.out.println(result);
            }
            PlaceDAO dao = new PlaceDAO(context);
            dao.deleteAll();
            for (Place i:
                 list) {
                dao.save(i);
            }
            activity.setGoogleSearchResultAdapter(list, kw);
        }
    }
}
